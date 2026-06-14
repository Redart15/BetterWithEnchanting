package googy.betterwithenchanting.gui;

import googy.betterwithenchanting.api.EnchantmentStack;
import googy.betterwithenchanting.block.TileEntityEnchantmentTable;
import googy.betterwithenchanting.api.EnchantmentContainer;
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

import java.util.Collections;
import java.util.List;
import java.util.Random;

import static googy.betterwithenchanting.item.EnchantingTags.UNECHANT;

public class MenuEnchantmentTable extends MenuAbstract {
	public final TileEntityEnchantmentTable enchantmentTable;
	protected final int[] enchantCost = new int[3];
	protected final int[] labelIndexes = new int[3];
	protected byte type = 0;
	protected int bookLevel;

	private final Random random = new Random();

	public MenuEnchantmentTable(ContainerInventory inventoryplayer, TileEntityEnchantmentTable enchantmentTable) {
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
		this.broadcastChanges();
	}

	public int getCostAtIndex(int i){
		return this.enchantCost[i % enchantCost.length];
	}

	public int getLabelIndexAtIndex(int i){
		return this.labelIndexes[i % labelIndexes.length];
	}

	public boolean getType(int i) {
		return ((this.type >> (i % 3)) & 1) == 1;
	}

	public boolean enchantItem(Player player, int enchantOption) {
		if (!playerCanEnchant(player, enchantOption)) {
			return false;
		}
		int cost = enchantCost[enchantOption];
		ItemStack stack = this.getSlot(0).getItemStack();
		List<EnchantmentStack> enchantments = EnchantmentContainer.generateEnchantmentsList(random, stack, cost);
		if (enchantments.isEmpty()) {
			return false;
		}
		if (player.gamemode != Gamemode.creative) {
			player.score -= cost;
			if (this.getSlot(1).hasItem() && this.getSlot(1).getItemStack() != null) {
				this.getSlot(1).getItemStack().stackSize -= enchantOption + 1;
			}
		}
		EnchantmentContainer.addEnchantments(stack, enchantments);
		forceUpdateInventory();
		return true;
	}


	@Override
	public void slotsChanged(Container container) {
		this.updateEnchantmentsCosts();
		super.slotsChanged(container);
	}

	void updateEnchantmentsCosts() {
		World world = enchantmentTable.worldObj;
		ItemStack stack = this.getSlot(0).getItemStack();
		if (world == null || stack == null) {
			return;
		}

		int posX = this.enchantmentTable.x;
		int posY = this.enchantmentTable.y;
		int posZ = this.enchantmentTable.z;

		this.bookLevel = 0;

		for (int x = -1; x <= 1; x++) {
			for (int z = -1; z <= 1; z++) {
				if ((x == 0 && z == 0) || !world.isAirBlock(posX + x, posY, posZ + z) || !world.isAirBlock(posX + x, posY + 1, posZ + z)) {
					continue;
				}
				this.checkForBookShelf(world, posX + x * 2, posY, posZ + z * 2);
				this.checkForBookShelf(world, posX + x * 2, posY + 1, posZ + z * 2);
				if (x == 0 || z == 0) {
					continue;
				}
				this.checkForBookShelf(world, posX + x * 2, posY, posZ + z);
				this.checkForBookShelf(world, posX + x * 2, posY + 1, posZ + z);
				this.checkForBookShelf(world, posX + x, posY, posZ + z * 2);
				this.checkForBookShelf(world, posX + x, posY + 1, posZ + z * 2);
			}
		}
		this.bookLevel = Math.min(this.bookLevel, 15);
		for (int i = 0; i < 3; i++) {
			this.enchantCost[i] = EnchantmentContainer.calcEnchantCost(i, this.bookLevel);
		}
	}

	private void checkForBookShelf(World world, int x, int y, int z) {
		if (world.getBlockId(x, y, z) == Blocks.BOOKSHELF_PLANKS_OAK.id()) {
			bookLevel++;
		}
	}

	@Override
	public void broadcastChanges() {
		super.broadcastChanges();
		for (ContainerListener crafting : this.containerListeners) {
			for (int i = 0; i < enchantCost.length; i++) {
				crafting.updateCraftingInventoryInfo(this, i, enchantCost[i]);
				crafting.updateCraftingInventoryInfo(this, i + 4, this.enchantmentTable.labelIndexes[i]);
			}
			if (this.type != this.enchantmentTable.type) {
				crafting.updateCraftingInventoryInfo(this, 3, this.enchantmentTable.type);
			}
		}
		this.type = this.enchantmentTable.type;
		System.arraycopy(this.enchantmentTable.labelIndexes, 0, this.labelIndexes, 0, labelIndexes.length);
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
		if (id >= 0 && id < enchantCost.length) {
			enchantCost[id] = value;
		}
		if(id == 3){
			this.type = (byte) value;
		}
		if(id >= 3 && id <= 3 + labelIndexes.length){
			this.labelIndexes[id - 4] = value;
		}
	}

	public boolean playerCanEnchant(Player player, int option) {
		ItemStack itemStack = this.getSlot(0).getItemStack();
		if(!this.getSlot(0).hasItem()) 									return false;
		if(itemStack == null) 											return false;
		if(!EnchantmentContainer.getEnchantments(itemStack).isEmpty()) 	return false;
		if(!EnchantmentContainer.hasApplicable(itemStack))				return false;
		if(itemStack.getItem().hasTag(UNECHANT))						return false;
		boolean enoughScore = player.score >= enchantCost[option];
		boolean enoughFuel = this.getFuelAmount() > option;
		boolean isCreative = player.gamemode == Gamemode.creative;
		return ((enoughScore && enoughFuel) || isCreative);
	}

	public int getFuelAmount() {
		if (!this.getSlot(1).hasItem()) {
			return 0;
		}
		return this.getSlot(1).getItemStack().stackSize;
	}

	@Override
	public boolean stillValid(Player player) {
		return enchantmentTable.stillValid(player);
	}

	@Override
	public List<Integer> getMoveSlots(InventoryAction action, Slot slot, int target, Player entityPlayer) {
		if (slot.index >= 0 && slot.index <= 3) {
			return this.getSlots(slot.index, 1, false);
		} else {
			if (action == InventoryAction.MOVE_ALL) {
				if (slot.index >= 3 && slot.index <= 30) {
					return this.getSlots(3, 27, false);
				}

				if (slot.index >= 30 && slot.index <= 38) {
					return this.getSlots(30, 9, false);
				}
			}

			return slot.index >= 3 && slot.index <= 38 ? this.getSlots(3, 36, false) : null;
		}
	}

	@Override
	public List<Integer> getTargetSlots(InventoryAction action, Slot slot, int target, Player entityPlayer) {
		if (slot.index >= 2 && slot.index <= 39) {
			if (action != InventoryAction.MOVE_ALL) {
				if (target == 1) {
					return this.getSlots(0, 1, false);
				}
				if (target == 2) {
					return this.getSlots(1, 1, false);
				}
			}
			if (slot.index <= 29) {
				return this.getSlots(30, 9, false);
			}
			if (slot.index >= 31 && slot.index <= 38) {
				return this.getSlots(3, 27, false);
			}
		}
		if (slot.index >= 0 && slot.index <= 1) {
			return this.getSlots(2, 36, false);
		} else {
			return Collections.emptyList();
		}
	}
}
