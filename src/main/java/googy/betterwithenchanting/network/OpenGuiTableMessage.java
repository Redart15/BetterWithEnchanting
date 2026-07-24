package googy.betterwithenchanting.network;

import googy.betterwithenchanting.block.TileEntityEnchantmentTable;
import googy.betterwithenchanting.gui.table.ScreenEnchantmentTable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.world.World;
import net.minecraft.core.world.pos.TilePos;
import net.minecraft.core.world.pos.TilePosc;
import org.jspecify.annotations.NonNull;
import turniplabs.halplibe.helper.network.NetworkMessage;
import turniplabs.halplibe.helper.network.UniversalPacket;

public class OpenGuiTableMessage implements NetworkMessage {
	public int windowId;
	public int tileX;
	public int tileY;
	public int tileZ;

	public OpenGuiTableMessage(){}

	public OpenGuiTableMessage(int windowId, TilePosc tilePosc){
		this(windowId, tilePosc.x(), tilePosc.y(), tilePosc.z());

	}

	public OpenGuiTableMessage(int windowId, int tileX, int tileY, int tileZ){
		this.windowId = windowId;
		this.tileX = tileX;
		this.tileY = tileY;
		this.tileZ = tileZ;
	}

	@Override
	public void encodeToUniversalPacket(@NonNull UniversalPacket packet) {
		packet.writeInt(this.windowId);
		packet.writeInt(this.tileX);
		packet.writeInt(this.tileY);
		packet.writeInt(this.tileZ);
	}

	@Override
	public void decodeFromUniversalPacket(@NonNull UniversalPacket packet) {
		this.windowId = packet.readInt();
		this.tileX = packet.readInt();
		this.tileY = packet.readInt();
		this.tileZ = packet.readInt();
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void handleClientEnv(NetworkContext context) {
		Player player = context.player;
		World world = player.world;
		TileEntity tileEntity = world.getTileEntity(new TilePos(this.tileX, this.tileY, this.tileZ));
		if(tileEntity instanceof TileEntityEnchantmentTable table){
			Minecraft.getMinecraft().displayScreen(new ScreenEnchantmentTable(context.player.inventory, table));
			context.player.containerMenu.containerId = windowId;
		}
	}
}
