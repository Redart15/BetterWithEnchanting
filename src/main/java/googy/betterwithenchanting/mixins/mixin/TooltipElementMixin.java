package googy.betterwithenchanting.mixins.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import googy.betterwithenchanting.api.EnchantmentContainer;
import net.minecraft.client.gui.TooltipElement;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.lang.I18n;
import net.minecraft.core.net.command.TextFormatting;
import net.minecraft.core.player.inventory.slot.Slot;
import org.spongepowered.asm.mixin.Mixin;

import java.util.List;

@Mixin(value = TooltipElement.class, remap = false)
public abstract class TooltipElementMixin {
	@WrapMethod(method = "getTooltipText(Lnet/minecraft/core/item/ItemStack;ZLnet/minecraft/core/player/inventory/slot/Slot;)Ljava/lang/String;")
	public String onGetTooltipText(ItemStack itemStack, boolean showDescription, Slot slot, Operation<String> original) {
		String toolTip = original.call(itemStack, showDescription, slot);
		StringBuilder enchantmentText = new StringBuilder();
		List<EnchantmentContainer.EnchantmentData> enchantmentsData = EnchantmentContainer.getEnchantments(itemStack);
		for (EnchantmentContainer.EnchantmentData enchantData : enchantmentsData) {
			boolean isNull = enchantData.enchantment == null;
			boolean noLevel = isNull || enchantData.enchantment.minLevel() == enchantData.enchantment.maxLevel();
			String key = isNull ? "disabled" : enchantData.enchantment.translationKey() + ".name";
			String enchantLevel = noLevel ? "" : String.valueOf(enchantData.level);
			String enchantName = TextFormatting.formatted(I18n.getInstance().translateKey(key), TextFormatting.CYAN);
			enchantLevel = TextFormatting.formatted(enchantLevel, TextFormatting.CYAN);
			enchantmentText.append(enchantName).append(" ").append(enchantLevel).append("\n");
		}
		toolTip += "\n";
		toolTip += enchantmentText;
		return toolTip;
	}
}
