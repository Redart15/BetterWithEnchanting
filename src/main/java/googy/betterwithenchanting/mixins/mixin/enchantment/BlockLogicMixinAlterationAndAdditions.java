package googy.betterwithenchanting.mixins.mixin.enchantment;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import googy.betterwithenchanting.mixins.EnchantmentMixins;
import net.minecraft.core.block.BlockLogic;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.enums.EnumDropCause;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = BlockLogic.class, remap = false)
public abstract class BlockLogicMixinAlterationAndAdditions {

	@Inject(method = "harvestBlock", at = @At("TAIL"))
	private void dropExtras(
		World world, Player player,
		int x, int y, int z, int meta, TileEntity tileEntity,
		CallbackInfo ci, @Local ItemStack stack
	) {
		BlockLogic logic = (BlockLogic) (Object) this;
		if (!player.canHarvestBlock(logic.block)) {
			return;
		}
		EnchantmentMixins.applyDiscovery(world, x, y, z, stack);
		EnchantmentMixins.applyFortune(world, x, y, z, stack);
		EnchantmentMixins.applyInsight(player, stack);
	}

	@WrapOperation(method = "dropBlockWithCause", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/block/BlockLogic;getBreakResult(Lnet/minecraft/core/world/World;Lnet/minecraft/core/enums/EnumDropCause;IIIILnet/minecraft/core/block/entity/TileEntity;)[Lnet/minecraft/core/item/ItemStack;"))
	public ItemStack[] adjustResults(
		BlockLogic instance, World world, EnumDropCause dropCause,
		int x, int y, int z, int meta,
		TileEntity tileEntity, Operation<ItemStack[]> original,
		@Local(argsOnly = true) Player player
	) {
		return EnchantmentMixins.applyMolten(dropCause, player, original.call(instance, world, dropCause, x, y, z, meta, tileEntity));
	}
}
