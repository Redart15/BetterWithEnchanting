package googy.betterwithenchanting.mixins.mixin.net;

import googy.betterwithenchanting.block.TileEntityEnchantmentTable;
import googy.betterwithenchanting.gui.GuiEnchantmentTable;
import googy.betterwithenchanting.mixins.interfaces.IEntityPlayer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.PlayerLocal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = PlayerLocal.class, remap = false)
public abstract class PlayerLocalMixin implements IEntityPlayer {

	@Shadow
	protected Minecraft mc;

	@Override
	public void displayGUIEnchantmentTable(TileEntityEnchantmentTable enchantmentTable) {
		mc.displayScreen(new GuiEnchantmentTable(this.mc.thePlayer.inventory, enchantmentTable));
	}
}
