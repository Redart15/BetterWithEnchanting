package googy.betterwithenchanting.mixins.mixin.net;

import googy.betterwithenchanting.mixins.interfaces.INetServerHandler;
import googy.betterwithenchanting.network.packet.PacketEnchantItem;
import googy.betterwithenchanting.inventory.ContainerEnchantmentTable;
import net.minecraft.core.player.inventory.menu.MenuAbstract;
import net.minecraft.server.entity.player.PlayerServer;
import net.minecraft.server.net.handler.PacketHandlerServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = PacketHandlerServer.class, remap = false)
public abstract class NetServerHandlerMixin implements INetServerHandler {
	@Shadow
	private PlayerServer playerEntity;

	@Override
	public void handleEnchantItem(PacketEnchantItem packet) {
		MenuAbstract container = playerEntity.craftingInventory;
		if (!(container instanceof ContainerEnchantmentTable)) {
			return;
		}
		ContainerEnchantmentTable enchantment = (ContainerEnchantmentTable) container;
		enchantment.enchantItem(playerEntity, packet.enchantmentOption);
	}
}
