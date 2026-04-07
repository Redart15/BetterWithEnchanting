package googy.betterwithenchanting.block.entity;

import com.mojang.nbt.tags.CompoundTag;
import com.mojang.nbt.tags.ListTag;
import googy.betterwithenchanting.BetterWithEnchanting;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.player.inventory.container.Container;
import org.jetbrains.annotations.Nullable;

public class TileEntityEnchantmentTable extends TileEntity implements Container {
	protected ItemStack[] items = new ItemStack[2];

	@Override
	public void setItem(int slot, @Nullable ItemStack stack) {
		items[slot] = stack;
		if (stack != null && stack.stackSize > getMaxStackSize()) {
			stack.stackSize = getMaxStackSize();
		}
	}

	@Override
	public @Nullable ItemStack removeItem(int i, int amount) {
		if (items[i] == null) return null;

		if (items[i].stackSize <= amount) {
			ItemStack itemstack = this.items[i];
			items[i] = null;
			return itemstack;
		}

		ItemStack itemstack = items[i].splitStack(amount);
		if (items[i].stackSize <= 0) {
			items[i] = null;
		}

		return itemstack;
	}

	@Override
	public int getMaxStackSize() {
		return 64;
	}

	@Override
	public String getNameTranslationKey() {
		return BetterWithEnchanting.ENCHANTMENT_TABLE_NAME;
	}

	@Override
	public int getContainerSize() {
		return 2;
	}

	@Override
	public @Nullable ItemStack getItem(int i) {
		return items[i];
	}

	@Override
	public void readFromNBT(CompoundTag tagCompound) {
		super.readFromNBT(tagCompound);
		ListTag itemList = tagCompound.getList("Items");
		for (int i = 0; i < itemList.tagCount(); i++) {
			CompoundTag itemTag = (CompoundTag) itemList.tagAt(i);
			byte slot = itemTag.getByte("Slot");
			if (slot >= 0 && slot < items.length) {
				items[slot] = ItemStack.readItemStackFromNbt(itemTag);
			}
		}
	}

	@Override
	public void writeToNBT(CompoundTag tagCompound) {
		super.writeToNBT(tagCompound);
		ListTag itemsTag = new ListTag();
		for (int i = 0; i < items.length; i++) {
			if (items[i] == null) {
				continue;
			}
			CompoundTag itemTag = new CompoundTag();
			itemTag.putByte("Slot", (byte) i);
			items[i].writeToNBT(itemTag);
			itemsTag.addTag(itemTag);
		}
		tagCompound.put("Items", itemsTag);
	}

	@Override
	public void sortContainer() {}

	@Override
	public boolean stillValid(Player entityplayer) {
		if (this.worldObj != null && this.worldObj.getTileEntity(this.x, this.y, this.z) == this) {
			return entityplayer.distanceToSqr(this.x + 0.5, this.y + 0.5, this.z + 0.5) <= 64.0;
		} else {
			return false;
		}
	}
}
