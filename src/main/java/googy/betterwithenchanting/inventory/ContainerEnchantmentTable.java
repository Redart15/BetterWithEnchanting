package googy.betterwithenchanting.inventory;

import googy.betterwithenchanting.block.entity.TileEntityEnchantmentTable;
import googy.betterwithenchanting.enchantment.Enchantment;
import googy.betterwithenchanting.enchantment.EnchantmentData;
import googy.betterwithenchanting.enchantment.Enchantments;
import googy.betterwithenchanting.utils.EnchantmentUtils;
import net.minecraft.core.InventoryAction;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.crafting.ContainerListener;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.player.gamemode.Gamemode;
import net.minecraft.core.player.inventory.container.Container;
import net.minecraft.core.player.inventory.container.ContainerInventory;
import net.minecraft.core.player.inventory.menu.MenuAbstract;
import net.minecraft.core.player.inventory.slot.Slot;
import net.minecraft.core.world.World;

import java.util.List;
import java.util.Random;

public class ContainerEnchantmentTable extends MenuAbstract {
	public TileEntityEnchantmentTable enchantmentTable;
	public int[] enchantCost = new int[3];

	private final Random random = new Random();

	public ContainerEnchantmentTable(ContainerInventory inventoryplayer, TileEntityEnchantmentTable enchantmentTable) {
		this.enchantmentTable = enchantmentTable;
		addSlot(new Slot(enchantmentTable, 0, 15, 47));
		addSlot(new EnchantFuelSlot(enchantmentTable, 1, 35, 47));
		for (int i = 0; i < 3; ++i) {
			for (int j = 0; j < 9; ++j) {
				this.addSlot(new Slot(inventoryplayer, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
			}
		}
		for (int i = 0; i < 9; ++i) {
			this.addSlot(new Slot(inventoryplayer, i, 8 + i * 18, 142));
		}
		updateEnchantmentsCosts();
	}


	public boolean enchantItem(Player player, int enchantOption) {
		if (!playerCanEnchant(player, enchantOption)) return false;
		int cost = enchantCost[enchantOption];
		if (player.gamemode != Gamemode.creative) {
			player.score -= cost;
			if (this.getSlot(1).hasItem()) {
				this.getSlot(1).getItemStack().stackSize -= enchantOption + 1;
			}
		}
		ItemStack stack = getSlot(0).getItemStack();
		List<EnchantmentData> enchantments = EnchantmentUtils.generateEnchantmentsList(random, stack, cost);
		if (enchantments == null) return false;
		EnchantmentUtils.addEnchantments(stack, enchantments);
		forceUpdateInventory();
		return true;
	}


	@Override
	public void slotsChanged(Container iinventory) {
		this.updateEnchantmentsCosts();
		super.slotsChanged(iinventory);
	}

	void updateEnchantmentsCosts() {
		World world = enchantmentTable.worldObj;
		if (world == null) return;

		ItemStack stack = getSlot(0).getItemStack();

		if (stack == null) return;

		List<Enchantment> pool = Enchantments.getPossible(stack.getItem());
		if (pool.isEmpty()) return;


		int posX = enchantmentTable.x;
		int posY = enchantmentTable.y;
		int posZ = enchantmentTable.z;

		int bookshelfs = 0;

		for (int x = -1; x <= 1; x++) {
			for (int z = -1; z <= 1; z++) {

				if (x == 0 && z == 0) continue;

				if (!world.isAirBlock(posX + x, posY, posZ + z) || !world.isAirBlock(posX + x, posY + 1, posZ + z))
					continue; // something obstructing the bookshelf

				int cornerBottom = world.getBlockId(posX + x * 2, posY, posZ + z * 2);
				int cornerTop = world.getBlockId(posX + x * 2, posY + 1, posZ + z * 2);

				if (cornerBottom == Blocks.BOOKSHELF_PLANKS_OAK.id()) bookshelfs++;
				if (cornerTop == Blocks.BOOKSHELF_PLANKS_OAK.id()) bookshelfs++;

				if (x == 0 || z == 0) continue;

				int sideXBottom = world.getBlockId(posX + x * 2, posY, posZ + z);
				int sideXTop = world.getBlockId(posX + x * 2, posY + 1, posZ + z);
				int sideZBottom = world.getBlockId(posX + x, posY, posZ + z * 2);
				int sideZTop = world.getBlockId(posX + x, posY + 1, posZ + z * 2);

				if (sideZBottom == Blocks.BOOKSHELF_PLANKS_OAK.id()) bookshelfs++;
				if (sideZTop == Blocks.BOOKSHELF_PLANKS_OAK.id()) bookshelfs++;
				if (sideXBottom == Blocks.BOOKSHELF_PLANKS_OAK.id()) bookshelfs++;
				if (sideXTop == Blocks.BOOKSHELF_PLANKS_OAK.id()) bookshelfs++;
			}
		}

		if (bookshelfs > 15)
			bookshelfs = 15;

		for (int i = 0; i < 3; i++) {
			enchantCost[i] = EnchantmentUtils.calcEnchantmentCost(i, bookshelfs);
		}
	}

	@Override
	public void broadcastChanges() {
		super.broadcastChanges();
		for (ContainerListener crafting : this.containerListeners) {
			for (int i = 0; i < enchantCost.length; i++) {
				crafting.updateCraftingInventoryInfo(this, i, enchantCost[i]);
			}
		}
	}

	public void forceUpdateInventory() {
		for (int i = 0; i < this.lastSlots.size(); i++) {
			ItemStack stack = slots.get(i).getItemStack();

			ItemStack stackCopy = stack != null ? stack.copy() : null;
			lastSlots.set(i, stackCopy);

			for (ContainerListener crafter : this.containerListeners) {
				crafter.updateInventorySlot(this, i, stackCopy);
			}
		}
		this.broadcastChanges();
	}

	@Override
	public void setData(int id, int value) {
		if (id >= 0 && id < enchantCost.length)
			enchantCost[id] = value;
	}

	public boolean playerCanEnchant(Player player, int option) {
		return getSlot(0).hasItem() &&
			EnchantmentUtils.getEnchantments(getSlot(0).getItemStack()).isEmpty() &&
			(player.score >= enchantCost[option] || player.gamemode == Gamemode.creative) &&
			(getFuelAmount() > option || player.gamemode == Gamemode.creative);
	}

	public int getFuelAmount() {
		if (!getSlot(1).hasItem()) {
			return 0;
		}
		return getSlot(1).getItemStack().stackSize;
	}

	@Override
	public boolean stillValid(Player player) {
		return enchantmentTable.stillValid(player);
	}

	/// TODO: Impelement the functions
	@Override
	public List<Integer> getMoveSlots(InventoryAction action, Slot slot, int target, Player entityPlayer) {
		return null;
	}

	@Override
	public List<Integer> getTargetSlots(InventoryAction action, Slot slot, int target, Player entityPlayer) {
		return null;
	}
}
