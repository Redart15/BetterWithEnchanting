package googy.betterwithenchanting.mixins.interfaces;

import googy.betterwithenchanting.block.TileEntityEnchantmentTable;
import net.minecraft.core.item.ItemStack;

public interface PlayerAdditionalGui
{
	void betterWithEnchanting$displayGuiEnchantmentTable(TileEntityEnchantmentTable enchantmentTable);
	void betterWithEnchanting$displayGuiEnchantmentBook(ItemStack book);
}
