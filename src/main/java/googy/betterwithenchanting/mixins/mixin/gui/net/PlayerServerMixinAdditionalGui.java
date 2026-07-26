package googy.betterwithenchanting.mixins.mixin.gui.net;

import googy.betterwithenchanting.block.TileEntityEnchantmentTable;
import googy.betterwithenchanting.gui.book.MenuEnchantmentBook;
import googy.betterwithenchanting.mixins.interfaces.PlayerAdditionalGui;
import googy.betterwithenchanting.gui.table.MenuEnchantmentTable;
import googy.betterwithenchanting.network.OpenGuiBookMessage;
import googy.betterwithenchanting.network.OpenGuiTableMessage;
import net.minecraft.core.crafting.ContainerListener;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.world.World;
import net.minecraft.server.entity.player.PlayerServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import turniplabs.halplibe.helper.network.NetworkHandler;

@Mixin(value = PlayerServer.class, remap = false)
public abstract class PlayerServerMixinAdditionalGui extends Player implements PlayerAdditionalGui, ContainerListener {
	@Shadow
	protected abstract void getNextWindowId();

	@Shadow
	private int currentWindowId;

	@Unique
	private final PlayerServer thisAs = (PlayerServer) (Object) this;

	private PlayerServerMixinAdditionalGui(World world) {
		super(world);
	}

	@Override
	public void displayGuiEnchantmentTable(TileEntityEnchantmentTable enchantmentTable) {
		this.getNextWindowId();
		NetworkHandler.sendToPlayer(thisAs, new OpenGuiTableMessage(this.currentWindowId, enchantmentTable.tilePos));
		this.containerMenu.onCraftGuiClosed(this);
		this.containerMenu = new MenuEnchantmentTable(this.inventory, enchantmentTable);
		this.containerMenu.containerId = this.currentWindowId;
		this.containerMenu.addSlotListener(this);
	}

	@Override
	public void displayGuiEnchantmentBook(ItemStack book) {
		this.getNextWindowId();
		NetworkHandler.sendToPlayer(thisAs, new OpenGuiBookMessage(this.currentWindowId, book));
		this.containerMenu.onCraftGuiClosed(this);
		this.containerMenu = new MenuEnchantmentBook(this.inventory, book);
		this.containerMenu.containerId = this.currentWindowId;
		this.containerMenu.addSlotListener(this);
	}
}

