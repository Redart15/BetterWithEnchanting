package googy.betterwithenchanting.interfaces.mixins;

import googy.betterwithenchanting.network.packet.PacketEnchantItem;

public interface INetServerHandler {
	void handleEnchantItem(PacketEnchantItem packet);
}
