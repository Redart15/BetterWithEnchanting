package googy.betterwithenchanting.mixins.mixin.render;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import googy.betterwithenchanting.mixins.EnchantmentMixins;
import net.minecraft.client.gui.TooltipElement;
import net.minecraft.core.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = TooltipElement.class, remap = false)
public abstract class TooltipElementMixin {

	@WrapOperation(method = "getTooltipText(Lnet/minecraft/core/item/ItemStack;ZLnet/minecraft/core/player/inventory/slot/Slot;)Ljava/lang/String;", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/TooltipElement;formatDescription(Ljava/lang/String;I)Ljava/lang/String;"))
	public String onGetTooltipTextT(String desc, int prefLineLength, Operation<String> original, ItemStack stack, @Local(type = StringBuilder.class, ordinal = 0) StringBuilder builder) {
		EnchantmentMixins.getEnchantmentText(stack, builder);
		return original.call(desc, prefLineLength);
	}
}
