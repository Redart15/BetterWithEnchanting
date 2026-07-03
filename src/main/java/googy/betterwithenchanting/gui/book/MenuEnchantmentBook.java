package googy.betterwithenchanting.gui.book;

import googy.betterwithenchanting.api.EnchantmentContainer;
import googy.betterwithenchanting.api.EnchantmentStack;
import googy.betterwithenchanting.gui.slot.EnchantItemSlot;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.core.InventoryAction;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.player.inventory.container.ContainerInventory;
import net.minecraft.core.player.inventory.menu.MenuAbstract;
import net.minecraft.core.player.inventory.slot.Slot;
import net.minecraft.core.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static googy.betterwithenchanting.item.EnchantingTags.UNECHANT;

public class MenuEnchantmentBook extends MenuAbstract {
	private final @NotNull World world;

	public MenuEnchantmentBook(@NotNull ContainerInventory inventory, @NotNull World world) {
		this.world = world;
		this.addSlot(new EnchantItemSlot(inventory ,0, 79, 39));

		for(int y = 0; y < 3; ++y) {
			for(int x = 0; x < 9; ++x) {
				int id = x + y * 9 + 9;
				int slotX = 7 + x * 18;
				int slotY = 91 + y * 18;
				this.addSlot(new Slot(inventory, id, slotX, slotY));
			}
		}
		for(int i = 0; i < 9; ++i) {
			this.addSlot(new Slot(inventory, i, 7 + i * 18, 150));
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
			if (slot.index >= 1 && slot.index < 30) {
				return this.getSlots(3, 27, false);
			}

			if (slot.index >= 30 && slot.index < 39) {
				return this.getSlots(30, 9, false);
			}
		}
		return slot.index >= 1 && slot.index < 39 ? this.getSlots(1, 36, false) : null;
	}

	@Override
	public IntList getTargetSlots(@NotNull InventoryAction action, Slot slot, int target, Player entityPlayer) {
		if (slot.index >= 3 && slot.index <= 39) {
			if (action != InventoryAction.MOVE_ALL && target == 1) {
					return this.getSlots(0, 1, false);
			}
			if (slot.index < 30) {
				return this.getSlots(30, 9, false);
			}
			if (slot.index < 39) {
				return this.getSlots(3, 27, false);
			}
		}
		if (slot.index == 0) {
			return this.getSlots(0, 36, false);
		} else {
			return null;
		}
	}

	public boolean playerCanEnchant() {
		Slot slot = this.getSlot(0);
		if(slot == null) 												{return false;}
		ItemStack itemStack = slot.getItemStack();
		if(itemStack == null) 											{return false;}
		if(!EnchantmentContainer.getEnchantments(itemStack).isEmpty()) 	{return false;}
		return !itemStack.getItem().hasTag(UNECHANT);
	}

	public List<EnchantmentStack> getOption(int i) {
		return new ArrayList<>();
	}
}
