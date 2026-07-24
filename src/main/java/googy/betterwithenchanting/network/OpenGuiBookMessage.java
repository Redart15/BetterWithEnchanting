package googy.betterwithenchanting.network;

import googy.betterwithenchanting.gui.book.ScreenEnchantmentBook;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.core.item.ItemStack;
import org.jspecify.annotations.NonNull;
import turniplabs.halplibe.helper.network.NetworkMessage;
import turniplabs.halplibe.helper.network.UniversalPacket;

public class OpenGuiBookMessage implements NetworkMessage {
	public int windowId;
	public ItemStack itemStack;

	public OpenGuiBookMessage(){
		super();
	}

	public OpenGuiBookMessage(int windowId, ItemStack itemStack){
		this.windowId = windowId;
		this.itemStack = itemStack;
	}

	@Override
	public final void encodeToUniversalPacket(@NonNull UniversalPacket packet) {
		packet.writeByte(this.windowId);
		packet.writeItemStack(itemStack);
	}

	@Override
	public final void decodeFromUniversalPacket(@NonNull UniversalPacket packet) {
		this.windowId = packet.readByte();
		this.itemStack = packet.readItemStack();
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void handleClientEnv(NetworkContext context) {
		Minecraft.getMinecraft().displayScreen(new ScreenEnchantmentBook(context.player.inventory, this.itemStack));
		context.player.containerMenu.containerId = windowId;
	}
}
