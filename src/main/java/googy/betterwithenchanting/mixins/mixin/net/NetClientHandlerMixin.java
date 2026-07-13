package googy.betterwithenchanting.mixins.mixin.net;

import googy.betterwithenchanting.BetterWithEnchanting;
import googy.betterwithenchanting.block.TileEntityEnchantmentTable;
import googy.betterwithenchanting.mixins.interfaces.PlayerAdditionalGui;
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

	@Inject(method = "handleContainerOpen", at = @At("TAIL"))
	public void handleOpenWindow(PacketContainerOpen packet, CallbackInfo info) {
		if (packet.inventoryType != BetterWithEnchanting.WINDOW_ID) {
			return;
		}
		TileEntityEnchantmentTable tile = new TileEntityEnchantmentTable();
		((PlayerAdditionalGui) mc.thePlayer).displayGuiEnchantmentTable(tile);
		this.mc.thePlayer.containerMenu.containerId = packet.windowId;
	}

}
