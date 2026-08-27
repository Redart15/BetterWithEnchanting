package googy.betterwithenchanting.mixins.mixin.gui.net;

import googy.betterwithenchanting.block.TileEntityEnchantmentTable;
import googy.betterwithenchanting.gui.book.ScreenEnchantmentBook;
import googy.betterwithenchanting.gui.table.ScreenEnchantmentTable;
import googy.betterwithenchanting.mixins.interfaces.PlayerAdditionalGui;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.PlayerLocal;
import net.minecraft.core.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = PlayerLocal.class, remap = false)
public abstract class PlayerLocalMixinAdditionalGui implements PlayerAdditionalGui {

	@Shadow
	protected Minecraft mc;

	@Override
	public void betterWithEnchanting$displayGuiEnchantmentTable(TileEntityEnchantmentTable enchantmentTable) {
		mc.displayScreen(new ScreenEnchantmentTable(this.mc.thePlayer.inventory, enchantmentTable));
	}

	@Override
	public void betterWithEnchanting$displayGuiEnchantmentBook(ItemStack selfStack){
		mc.displayScreen(new ScreenEnchantmentBook(this.mc.thePlayer.inventory, selfStack));
	}
}
