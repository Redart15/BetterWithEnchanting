package googy.betterwithenchanting.inventory;

import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.Items;
import net.minecraft.core.player.inventory.container.Container;
import net.minecraft.core.player.inventory.slot.Slot;

public class EnchantFuelSlot extends Slot {
	public EnchantFuelSlot(Container inventory, int id, int x, int y) {
		super(inventory, id, x, y);
	}

	@Override
	public boolean mayPlace(ItemStack itemstack) {
		return itemstack != null && itemstack.getItem().id == Items.DYE.id && itemstack.getMetadata() == 4;
	}
}
