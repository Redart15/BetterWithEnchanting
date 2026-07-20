package googy.betterwithenchanting.network;

import googy.betterwithenchanting.gui.book.MenuEnchantmentBook;
import googy.betterwithenchanting.gui.table.MenuEnchantmentTable;
import net.minecraft.core.player.inventory.menu.MenuAbstract;
import org.jspecify.annotations.NonNull;
import turniplabs.halplibe.helper.network.NetworkMessage;
import turniplabs.halplibe.helper.network.UniversalPacket;

public class EnchantItemMessage implements NetworkMessage {
	public int windowID;
	public int enchantmentOption;

	public EnchantItemMessage(){}

	public EnchantItemMessage(int windowID, int enchantmentOption) {
		this.windowID = windowID;
		this.enchantmentOption = enchantmentOption;
	}

	@Override
	public void encodeToUniversalPacket(@NonNull UniversalPacket packet) {
		packet.writeInt(windowID);
		packet.writeInt(enchantmentOption);
	}

	@Override
	public void decodeFromUniversalPacket(@NonNull UniversalPacket packet) {
		this.windowID = packet.readInt();
		this.enchantmentOption = packet.readInt();
	}

	@Override
	public void handleServerEnv(NetworkContext context) {
		MenuAbstract container = context.player.containerMenu;
		if (container instanceof MenuEnchantmentTable enchantment) {
			enchantment.enchantItem(context.player, this.enchantmentOption);
			return;
		}
		if(container instanceof MenuEnchantmentBook enchantment){
			enchantment.enchantItem(context.player, this.enchantmentOption);
		}
	}
}
