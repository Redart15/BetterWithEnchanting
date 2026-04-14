package googy.betterwithenchanting.mixins.interfaces;

import googy.betterwithenchanting.network.packet.PacketEnchantItem;

public interface INetServerHandler {
	void handleEnchantItem(PacketEnchantItem packet);
}
