package googy.betterwithenchanting.mixin;

import googy.betterwithenchanting.enchantment.EnchantmentData;
import googy.betterwithenchanting.utils.EnchantmentUtils;
import net.minecraft.client.gui.TooltipElement;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.net.command.TextFormatting;
import net.minecraft.core.player.inventory.slot.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(value = TooltipElement.class, remap = false)
public class TooltipElementMixin {
	@Inject(at = @At("TAIL"), method = "getTooltipText(Lnet/minecraft/core/item/ItemStack;ZLnet/minecraft/core/player/inventory/slot/Slot;)Ljava/lang/String;", cancellable = true)
	public void onGetTooltipText(ItemStack stack, boolean showDescription, Slot slot, CallbackInfoReturnable<String> info) {
		String toolTip = info.getReturnValue();

		StringBuilder enchantmentText = new StringBuilder();
		List<EnchantmentData> enchantmentsData = EnchantmentUtils.getEnchantments(stack);

		for (EnchantmentData enchantData : enchantmentsData) {
			boolean isNull = enchantData.enchantment == null;
			boolean noLevel = isNull || enchantData.enchantment.getMinLevel() == enchantData.enchantment.getMaxLevel();

			String enchantName = isNull ? "Unknown" : enchantData.enchantment.getName();
			String enchantLevel = noLevel ? "" : String.valueOf(enchantData.level);

			enchantName = TextFormatting.formatted(enchantName, TextFormatting.LIGHT_GRAY);
			enchantLevel = TextFormatting.formatted(enchantLevel, TextFormatting.YELLOW);

			enchantmentText.append(enchantName + " " + enchantLevel + "\n");
		}

		toolTip += "\n";
		toolTip += enchantmentText;

		info.setReturnValue(toolTip);
	}
}
