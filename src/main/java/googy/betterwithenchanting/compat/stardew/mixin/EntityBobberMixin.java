package googy.betterwithenchanting.compat.stardew.mixin;

import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import googy.betterwithenchanting.api.Enchantments;
import googy.betterwithenchanting.api.EnchantmentContainer;
import googy.betterwithenchanting.mixins.MixinsHelperLogic;
import googy.betterwithenchanting.mixins.mixin.accessor.EntityAccessor;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.entity.EntityFishingBobber;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.ItemFishingRod;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.world.pos.TilePos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = EntityFishingBobber.class, remap = false)
public abstract class EntityBobberMixin{

	@ModifyVariable(method = "tick", at = @At("STORE"), ordinal = 1)
	private int getCatchRate(int catchRate) {
		if (catchRate != 500) {return catchRate;}
		EntityFishingBobber thisBobber = (EntityFishingBobber) (Object) this;
		ItemStack stack = thisBobber.owner.getCurrentEquippedItem();
		int baitLevel = EnchantmentContainer.getLevel(stack, Enchantments.BAIT);
		int rate = catchRate - (baitLevel * 100);
		TilePos tilePos = new TilePos((int) Math.floor(thisBobber.x), (int) Math.floor(thisBobber.y) + 1, (int) Math.floor(thisBobber.z));
		Block<?> block = thisBobber.world.getBlockType(tilePos);
		boolean rainBonus = thisBobber.world.isBlockBeingRainedOn(tilePos);
		boolean algaeRate = block.id() == Blocks.ALGAE.id();
		int limit = 50; // smallest possible catchRate value
		if (rainBonus) {limit += 200;}
		if (algaeRate) {limit += 100;}
		rate = Math.max(rate, limit);
		return rate;
	}

	@ModifyArg(method = "yoink", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/entity/EntityItem;<init>(Lnet/minecraft/core/world/World;DDDLnet/minecraft/core/item/ItemStack;)V"), index = 4)
	private ItemStack extraFish(ItemStack itemstack, @Share("fishCount") LocalIntRef count){
		EntityFishingBobber bobber = (EntityFishingBobber) (Object) this;
		Player player = bobber.owner;
		ItemStack rod = player.getHeldItem();
		if(rod == null || !(rod.getItem() instanceof ItemFishingRod)){
			return itemstack;
		}
		int c = 0;
		for(int i = 0; i < EnchantmentContainer.getLevel(rod, Enchantments.HAUL); i++) {
			if (((EntityAccessor) bobber).getRandom().nextInt(5) == 0) {
				itemstack.stackSize += 1;
				c++;
			}
		}
		count.set(c);
		return itemstack;
	}

	@ModifyArg(method = "yoink", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/entity/player/Player;addStat(Lnet/minecraft/core/achievement/stat/Stat;I)V"), index = 1)
	private int extraFish(int i, @Share("fishCount") LocalIntRef count){
		return i +  count.get();
	}

	@Inject(method = "yoink", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/entity/player/Player;addStat(Lnet/minecraft/core/achievement/stat/Stat;I)V", shift = At.Shift.AFTER))
	private void applyInsight(CallbackInfoReturnable<Integer> cir){
		EntityFishingBobber bobber = (EntityFishingBobber) (Object) this;
		Player player = bobber.owner;
		ItemStack rod = player.getHeldItem();
		if(rod == null || !(rod.getItem() instanceof ItemFishingRod)){
			return;
		}
		MixinsHelperLogic.applyInsight(player, rod, 10);
	}
}
