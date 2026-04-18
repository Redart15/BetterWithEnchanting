package googy.betterwithenchanting.mixins.mixin.net;

import googy.betterwithenchanting.BetterWithEnchanting;
import googy.betterwithenchanting.block.TileEntityEnchantmentTable;
import googy.betterwithenchanting.mixins.interfaces.IEntityPlayer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.net.handler.PacketHandlerClient;
import net.minecraft.core.net.packet.PacketContainerOpen;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = PacketHandlerClient.class, remap = false)
public abstract class NetClientHandlerMixin {
	@Final
	@Shadow
	private Minecraft mc;

	@Inject(method = "handleOpenWindow", at = @At("TAIL"))
	public void handleOpenWindow(PacketContainerOpen packet, CallbackInfo info) {
		if (packet.inventoryType != BetterWithEnchanting.CONFIG_HANDLER.getInt("enchantment_window_type_id")) {
			return;
		}
		if (packet.windowId != BetterWithEnchanting.WINDOW_ID) {
			return;
		}
		TileEntityEnchantmentTable tile = new TileEntityEnchantmentTable();
		((IEntityPlayer) mc.thePlayer).displayGUIEnchantmentTable(tile);
		this.mc.thePlayer.craftingInventory.containerId = packet.windowId;

	}

}
