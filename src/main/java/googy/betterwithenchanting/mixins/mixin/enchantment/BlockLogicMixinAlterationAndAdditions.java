package googy.betterwithenchanting.mixins.mixin.enchantment;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import googy.betterwithenchanting.api.EnchantmentContainer;
import googy.betterwithenchanting.api.Enchantments;
import googy.betterwithenchanting.mixins.MixinsHelperLogic;
import net.minecraft.core.block.BlockLogic;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.enums.EnumDropCause;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.world.World;
import net.minecraft.core.world.pos.TilePosc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings("java:S107")
@Mixin(value = BlockLogic.class, remap = false)
public abstract class BlockLogicMixinAlterationAndAdditions {


	@Definition(id = "isSilkTouch", method = "Lnet/minecraft/core/item/Item;isSilkTouch()Z")
	@Expression("?.isSilkTouch()")
	@ModifyExpressionValue(method = "onHarvest", at = @At("MIXINEXTRAS:EXPRESSION"))
	public boolean adjustCause(boolean original, @Local ItemStack itemStack){
		if(EnchantmentContainer.contains(itemStack,  Enchantments.SILKTOUCH)){
			return true;
		}
		return original;
	}

	@Inject(method = "onHarvest", at = @At("TAIL"))
	private void dropExtras(
		World world, Player player,
		TilePosc tilePos, int data, TileEntity tileEntity,
		CallbackInfo ci
		){
		ItemStack stack = player.inventory.getCurrentItem();
		BlockLogic logic = (BlockLogic) (Object) this;
		if (!player.canHarvestBlock(logic.block)) {
			return;
		}
		MixinsHelperLogic.applyDiscovery(world, tilePos, stack);
		MixinsHelperLogic.applyFortune(world, tilePos, stack);
		MixinsHelperLogic.applyForaging(world, tilePos, stack, logic);
		MixinsHelperLogic.applyInsight(player, stack, 3);
	}

	@WrapOperation(method = "dropWithCause", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/block/BlockLogic;getBreakResult(Lnet/minecraft/core/world/World;Lnet/minecraft/core/enums/EnumDropCause;Lnet/minecraft/core/world/pos/TilePosc;ILnet/minecraft/core/block/entity/TileEntity;)[Lnet/minecraft/core/item/ItemStack;"))
	private ItemStack[] adjustResults(
		BlockLogic instance, World world, EnumDropCause dropCause,
		TilePosc tilePos, int data,
		TileEntity tileEntity, Operation<ItemStack[]> original,
		@Local(argsOnly = true) Player player
	){
		ItemStack[] drops = original.call(instance, world, dropCause, tilePos, data, tileEntity);
		if(player == null){
			return drops;
		}
		int crush = EnchantmentContainer.getLevel(player.getHeldItem(), Enchantments.CRUSH);
		if (crush > 0) {
			dropCause = EnumDropCause.PISTON_CRUSH;
		}
		return MixinsHelperLogic.applyMoltenAndScevange(dropCause, player, drops);
	}
}
