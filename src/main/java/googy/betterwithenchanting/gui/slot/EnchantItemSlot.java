package googy.betterwithenchanting.gui.slot;

import net.minecraft.core.item.ItemStack;
import net.minecraft.core.player.inventory.container.Container;
import net.minecraft.core.player.inventory.slot.Slot;

public class EnchantItemSlot extends Slot {
	public EnchantItemSlot(Container container, int index, int x, int y) {
		super(container, index, x, y);
	}

	@Override
	public int getMaxStackSize() {
		return 1;
	}

}
