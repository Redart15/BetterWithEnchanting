package googy.betterwithenchanting.gui.book;

import googy.betterwithenchanting.api.*;
import googy.betterwithenchanting.mixins.interfaces.ContainerHotbarLocking;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.core.InventoryAction;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.ItemFood;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.player.gamemode.Gamemodes;
import net.minecraft.core.player.inventory.container.Container;
import net.minecraft.core.player.inventory.container.ContainerInventory;
import net.minecraft.core.player.inventory.container.ContainerSimple;
import net.minecraft.core.player.inventory.menu.MenuAbstract;
import net.minecraft.core.player.inventory.slot.Slot;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static googy.betterwithenchanting.item.EnchantmentTags.UNECHANT;

public class MenuEnchantmentBook extends MenuAbstract {
	public static final int SLOT_SIZE = 18;
	private final Container enchantSlot;
	private int optioChoosen;
	private final ItemStack selfStack;
	@SuppressWarnings("unchecked")
	private final List<EnchantmentStack>[] enchantmentsOption = (List<EnchantmentStack>[]) new List<?>[2];

	public MenuEnchantmentBook(@NotNull ContainerInventory inventory, ItemStack selfStack) {
		this.optioChoosen = -1;
		this.selfStack = selfStack;
		this.enchantSlot = new ContainerSimple("enchantmentSlot", 1);
		for(int i = 0; i < 2; i++){
			enchantmentsOption[i] = EnchantmentContainer.getEnchantments(this.selfStack, i);
		}
		this.addSlot(new Slot(enchantSlot, 0, 80, 40 + 7));
		for (int y = 0; y < 3; ++y) {
			for (int x = 0; x < 9; ++x) {
				int id = x + y * 9 + 9;
				int ix = x * SLOT_SIZE + 8;
				int iy = y * SLOT_SIZE + 112;
				this.addSlot(new Slot(inventory, id, ix, iy));
			}
		}
		for (int i = 0; i < 9; ++i) {
			this.addSlot(new Slot(inventory, i, 8 + i * SLOT_SIZE, 150 + 20));
		}
		this.broadcastChanges();
	}

	public boolean playerCanEnchant(int option) {
		Slot slot = this.getSlot(0);
		if (slot == null) {
			return false;
		}
		ItemStack itemStack = slot.getItemStack();
		if (itemStack == null) {
			return false;
		}
		if (itemStack.getItem().hasTag(UNECHANT)) {
			return false;
		}
		for (EnchantmentStack stack : EnchantmentContainer.getEnchantments(this.selfStack, option)) {
			if (stack.canEnchant(itemStack)) {
				return true;
			}
		}

		return false;
	}

	public boolean enchantItem(Player player, int i) {
		Slot slot = this.getSlot(0);
		if (slot == null || slot.getItemStack() == null) {
			return false;
		}
		ItemStack enchantItem = slot.getItemStack();
		List<EnchantmentStack> enchantmentStackList = this.getOption(i);
		if (enchantmentStackList.isEmpty()) {
			return false;
		}
		if (player.gamemode != Gamemodes.CREATIVE) {
			this.optioChoosen = i;
		}
		this.enchantItem(player, i, enchantItem);
		player.triggerAchievement(EnchantmentAchievements.LOST_KNOWLEDGE);
		this.checkAchievements(player, enchantItem);
		return true;
	}

	private void enchantItem(Player player, int i, ItemStack enchantItem) {
		if(EnchantmentContainer.hasEnchantments(enchantItem)){
			for(EnchantmentStack toAddStack: this.getOption(i)){
				if (toAddStack.canEnchant(enchantItem)) {
					EnchantmentContainer.addEnchantment(enchantItem, toAddStack);
					EnchantmentContainer.setLevel(enchantItem, toAddStack.getEnchantment(), toAddStack.getLevel());
				}
			}
			return;
		}
		for(EnchantmentStack toAddStack: this.getOption(i)){
			if (toAddStack.canEnchant(enchantItem)) {
				EnchantmentContainer.addEnchantment(enchantItem, toAddStack);
			}
		}
		player.triggerAchievement(EnchantmentAchievements.ENCHANT_ITEM);
		if(enchantItem.getItem() instanceof ItemFood){
			player.triggerAchievement(EnchantmentAchievements.ENCHANTED_FOOD);
		}
	}

	public void checkAchievements(Player player,@NotNull ItemStack enchantItem) {
		if (player.getStat(EnchantmentAchievements.FULL_ENCHANTED) != 0 && player.getStat(EnchantmentAchievements.HIGH_LEVEL_ENCHANT) != 0) {
			return;
		}
		List<EnchantmentStack> stacks = EnchantmentContainer.getEnchantments(enchantItem);
		EnchantmentAchievements.applyFullEnchant(player, enchantItem, stacks);
		EnchantmentAchievements.applyHighEnchant(player, stacks);
	}

	@Override
	public void onCraftGuiClosed(@NotNull Player player) {
		super.onCraftGuiClosed(player);
		ItemStack itemstack = this.enchantSlot.getItem(0);
		if (itemstack != null) {
			this.storeOrDropItem(player, itemstack);
			player.world.playSoundAtEntity(player, player, "random.insert", 0.1F, 1.0F);
		} else {
			this.enchantSlot.setItem(0, null);
		}
		ContainerHotbarLocking inventory = (ContainerHotbarLocking) player.inventory;
		if (inventory.enchanted$isLocked(player.inventory.getCurrentSlot())) {
			inventory.enchanted$lockSlot(player.inventory.getCurrentSlot(), false);
		}
		if (this.enchantmentWasUsed()) {
			if (this.selfStack.stackSize <= 0) {
				return;
			}
			this.selfStack.stackSize--;
			if (this.selfStack.stackSize == 0) {
				player.inventory.setItem(player.inventory.getCurrentSlot(), null);
			} else {
				player.inventory.setItem(player.inventory.getCurrentSlot(), this.selfStack);
			}
		} else {
			player.inventory.setItem(player.inventory.getCurrentSlot(), this.selfStack);
		}

	}

	@Override
	public boolean stillValid(@NotNull Player player) {
		return true;
	}

	@Override
	public IntList getMoveSlots(@NotNull InventoryAction action, Slot slot, int target, Player entityPlayer) {
		if (slot.index == 0) {
			return this.getSlots(0, 1, false);
		}
		if (action == InventoryAction.MOVE_ALL) {
			if (slot.index >= 1 && slot.index < 28) {
				return this.getSlots(1, 27, false);
			}

			if (slot.index >= 28 && slot.index < 37) {
				return this.getSlots(28, 9, false);
			}
		}
		return slot.index >= 1 && slot.index < 37 ? this.getSlots(1, 36, false) : null;
	}

	@Override
	public IntList getTargetSlots(@NotNull InventoryAction action, Slot slot, int target, Player entityPlayer) {
		if (slot.index >= 1 && slot.index <= 37) {
			if (action != InventoryAction.MOVE_ALL && target == 1) {
				return this.getSlots(0, 1, false);
			}
			if (slot.index < 28) {
				return this.getSlots(28, 9, false);
			}
			if (slot.index < 37) {
				return this.getSlots(1, 27, false);
			}
		}
		if (slot.index == 0) {
			return this.getSlots(1, 36, false);
		} else {
			return null;
		}
	}

	public List<EnchantmentStack> getOption(int i) {
		return enchantmentsOption[i];
	}

	public int enchantmentChoosenOption() {
		return this.optioChoosen;
	}

	public boolean enchantmentWasUsed() {
		return this.optioChoosen != -1;
	}

	public ItemStack selfStack() {
		return this.selfStack.copy();
	}
}
