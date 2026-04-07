package googy.betterwithenchanting.block;

import com.mojang.nbt.tags.CompoundTag;
import com.mojang.nbt.tags.ListTag;
import googy.betterwithenchanting.BetterWithEnchanting;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.player.inventory.container.Container;
import net.minecraft.core.util.helper.MathHelper;
import org.jetbrains.annotations.Nullable;

public class TileEntityEnchantmentTable extends TileEntity implements Container {
	protected ItemStack[] items = new ItemStack[2];

	public int ticks;
	public float pageFlip;
	public float prevPageFlip;
	public float flipT;
	public float flipA;
	public float bookSpread;
	public float prevBookSpread;
	public float bookRot;
	public float prevBookRot;
	public float tRot;
	public float itemRot;

	@Override
	public void tick()
	{
		ticks++;
		prevBookSpread = bookSpread;
		prevBookRot = bookRot;

		Player player = worldObj.getClosestPlayer((float)this.x + 0.5F, (float)this.y + 0.5F, (float)this.z + 0.5F, 3.0D);
		boolean cRot = false;

		if (player != null) {
			double x = player.x - (double)((float)this.x + 0.5F);
			double z = player.z - (double)((float)this.z + 0.5F);
			tRot = (float)Math.atan2(z, x);
			cRot = true;
		}

		if (!cRot) tRot += 0.02F;
		itemRot += 0.03F;

		if (player != null || items[0] != null) {
			bookSpread += 0.1F;
			if (bookSpread < 0.5F || worldObj.rand.nextInt(40) == 0) {
				float f = flipT;
				while (true) {
					flipT += (float)(worldObj.rand.nextInt(4) - worldObj.rand.nextInt(4));
					if (f != flipT) break;
				}
			}
		} else bookSpread -= 0.1F;

		while (bookRot >= (float)Math.PI) bookRot -= ((float)Math.PI * 2.0F);
		while (bookRot < -(float)Math.PI) bookRot += ((float)Math.PI * 2.0F);
		while (tRot >= (float)Math.PI) tRot -= ((float)Math.PI * 2.0F);
		while (tRot < -(float)Math.PI) tRot += ((float)Math.PI * 2.0F);
		while (itemRot >= (float)Math.PI) itemRot -= ((float)Math.PI * 2.0F);
		while (itemRot < -(float)Math.PI) itemRot += ((float)Math.PI * 2.0F);

		float f;

		for (f = tRot - bookRot; f >= (float)Math.PI; f -= (float)Math.PI * 2.0F);
		while (f < -(float)Math.PI) f += ((float)Math.PI * 2.0F);

		bookRot += f * 0.4F;
		bookSpread = MathHelper.clamp(bookSpread, 0.0F, 1.0F);
		prevPageFlip = pageFlip;
		float f2 = (flipT - pageFlip) * 0.4F;
		f2 = MathHelper.clamp(f2, -0.2F, 0.2F);
		flipA += (f2 - flipA) * 0.9F;
		pageFlip += flipA;
	}

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
