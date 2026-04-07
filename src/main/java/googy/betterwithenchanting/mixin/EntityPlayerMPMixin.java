package googy.betterwithenchanting.mixin;

import googy.betterwithenchanting.BetterWithEnchanting;
import googy.betterwithenchanting.block.entity.TileEntityEnchantmentTable;
import googy.betterwithenchanting.interfaces.mixins.IEntityPlayer;
import googy.betterwithenchanting.inventory.ContainerEnchantmentTable;
import net.minecraft.core.crafting.ContainerListener;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.net.packet.PacketContainerOpen;
import net.minecraft.core.world.World;
import net.minecraft.server.entity.player.PlayerServer;
import net.minecraft.server.net.handler.PacketHandlerServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = PlayerServer.class, remap = false)
public abstract class EntityPlayerMPMixin extends Player implements IEntityPlayer, ContainerListener {
	@Shadow
	protected abstract void getNextWindowId();

	@Shadow
	private int currentWindowId;

	@Shadow
	public PacketHandlerServer playerNetServerHandler;

	public EntityPlayerMPMixin(World world) {
		super(world);
	}

	@Override
	public void displayGUIEnchantmentTable(TileEntityEnchantmentTable enchantmentTable) {
		this.getNextWindowId();

		this.playerNetServerHandler.sendPacket(
			new PacketContainerOpen(
				this.currentWindowId,
				BetterWithEnchanting.config.getInt("enchantment_window_type_id"),
				enchantmentTable.getNameTranslationKey(),
				enchantmentTable.getContainerSize()
			));

		this.craftingInventory = new ContainerEnchantmentTable(this.inventory, enchantmentTable);
		this.craftingInventory.onCraftGuiClosed(this);
		this.craftingInventory.containerId = this.currentWindowId;
		this.craftingInventory.addSlotListener(this);
	}

}

