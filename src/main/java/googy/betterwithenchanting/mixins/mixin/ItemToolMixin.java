package googy.betterwithenchanting.mixins.mixin;

import googy.betterwithenchanting.api.Enchantments;
import googy.betterwithenchanting.api.EnchantmentContainer;
import net.minecraft.core.block.Block;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.tool.ItemTool;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ItemTool.class, remap = false)
public abstract class ItemToolMixin {

	@Inject(method = "getStrVsBlock", at = @At("RETURN"), cancellable = true)
	void getStrVsBlock(ItemStack stack, Block block, CallbackInfoReturnable<Float> info) {
		float ret = info.getReturnValue();
		if (ret <= 1.0) {
			return;
		}
		int hasteLevel = EnchantmentContainer.getLevel(stack, Enchantments.HASTE);
		if (hasteLevel <= 0) {
			return;
		}
		ret += hasteLevel * hasteLevel + 2;
		info.setReturnValue(ret);
	}

}
