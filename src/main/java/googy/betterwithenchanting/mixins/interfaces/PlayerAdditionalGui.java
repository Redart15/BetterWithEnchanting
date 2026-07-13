package googy.betterwithenchanting.mixins.interfaces;

import googy.betterwithenchanting.block.TileEntityEnchantmentTable;
import net.minecraft.core.item.ItemStack;

public interface PlayerAdditionalGui
{
	void displayGuiEnchantmentTable(TileEntityEnchantmentTable enchantmentTable);
	void displayGuiEnchantmentBook(ItemStack book);
}
