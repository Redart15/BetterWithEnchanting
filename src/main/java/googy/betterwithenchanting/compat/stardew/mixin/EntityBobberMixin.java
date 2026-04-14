package googy.betterwithenchanting.compat.stardew.mixin;

import googy.betterwithenchanting.api.Enchantments;
import googy.betterwithenchanting.api.EnchantmentContainer;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.EntityFishingBobber;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(value = EntityFishingBobber.class, remap = false)
public abstract class EntityBobberMixin extends Entity {
	public EntityBobberMixin(World world) {
		super(world);
	}

	@ModifyVariable(method = "tick", at = @At("STORE"), ordinal = 1)
	int getCatchRate(int catchRate) {
		if (catchRate != 500) return catchRate;

		EntityFishingBobber thisBobber = (EntityFishingBobber) (Object) this;

		ItemStack stack = thisBobber.owner.getCurrentEquippedItem();
		int baitLevel = EnchantmentContainer.getLevel(stack, Enchantments.BAIT);

		int rate = catchRate - (baitLevel * 100);

		boolean rainBonus = thisBobber.world.canBlockBeRainedOn((int) Math.floor(thisBobber.x), (int) Math.floor(thisBobber.y) + 1, (int) Math.floor(thisBobber.z));
		boolean algaeRate = thisBobber.world.getBlockId((int) Math.floor(thisBobber.x), (int) Math.floor(thisBobber.y) + 1, (int) Math.floor(thisBobber.z)) == Blocks.ALGAE.id();

		int limit = 50; // smallest possible catchRate value
		if (rainBonus) limit += 200;
		if (algaeRate) limit += 100;

		rate = Math.max(rate, limit);

		return rate;
	}
}
