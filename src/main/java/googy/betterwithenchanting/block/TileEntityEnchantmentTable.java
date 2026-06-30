package googy.betterwithenchanting.block;

import com.mojang.nbt.tags.CompoundTag;
import com.mojang.nbt.tags.ListTag;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.player.inventory.container.Container;
import net.minecraft.core.util.helper.MathHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

import static googy.betterwithenchanting.BetterWithEnchanting.LABELS;
import static googy.betterwithenchanting.BetterWithEnchanting.MOD_ID;

public class TileEntityEnchantmentTable extends TileEntity implements Container {
	protected ItemStack[] items = new ItemStack[2];
	protected Random random = new Random();
	private int ticks;
	private float bookRot;
	private float prevBookRot;
	private float prevPageFlip;
	private float pageFlip;
	private float bookSpread;
	private float prevBookSpread;
	private float itemRot;

	private float flipT;
	private float flipA;
	private float tRot;

	public final int[] labelIndexes = new int[3];
	public byte type = 0;

	public void setRandomLabel() {
		for (int i = 0; i < labelIndexes.length; i++) {
			labelIndexes[i] = this.getNewLabel();
		}
		this.type = (byte) this.random.nextInt(8);
		this.setChanged();
	}

	public int 	getNewLabel() {
		return random.nextInt(LABELS.length);
	}

	@Override
	public void tick() {
		if(worldObj == null){
			return;
		}
		this.ticks++;
		this.prevBookSpread = bookSpread;
		this.prevBookRot = bookRot;
		Player player = this.worldObj.getClosestPlayer(this.tilePos.x() + 0.5F, this.tilePos.y() + 0.5F, this.tilePos.z() + 0.5F, 3.0D);
		boolean cRot = this.adjustRotation(player);
		this.tRot += !cRot ? 0.0F : 0.02F;
		this.itemRot += 0.03F;
		this.setUpBookSpread(player);
		this.adjustments();
		this.bookRot += adjustRotationAngleBook() * 0.4F;
		this.bookSpread = MathHelper.clamp(this.bookSpread, 0.0F, 1.0F);
		this.prevPageFlip = this.pageFlip;
		this.setUpPageFlip();
	}

	private void setUpPageFlip() {
		float f2 = (this.flipT - this.pageFlip) * 0.4F;
		f2 = MathHelper.clamp(f2, -0.2F, 0.2F);
		this.flipA += (f2 - this.flipA) * 0.9F;
		this.pageFlip += this.flipA;
	}

	private float adjustRotationAngleBook() {
		float f = this.tRot - this.bookRot;
		for (;f >= (float) Math.PI; f -= (float) Math.PI * 2.0F);
		while (f < -(float) Math.PI) f += ((float) Math.PI * 2.0F);
		return f;
	}

	private void setUpBookSpread(Player player) {
		if ((player != null || this.items[0] != null) && this.worldObj != null) {
			this.bookSpread += 0.1F;
			if (this.bookSpread < 0.5F || this.worldObj.rand.nextInt(40) == 0) {
				float f = this.flipT;
				while (true) {
					this.flipT += (this.worldObj.rand.nextInt(4) - this.worldObj.rand.nextInt(4));
					if (f != this.flipT) {
						break;
					}
				}
			}
		} else {
			this.bookSpread -= 0.1F;
		}
	}

	private boolean adjustRotation(Player player) {
		if (player != null) {
			double x = player.x - (this.tilePos.x() + 0.5F);
			double z = player.z - (this.tilePos.z() + 0.5F);
			this.tRot = (float) Math.atan2(z, x);
			return true;
		}
		this.tRot += 0.03F;
		return false;
	}

	private void adjustments() {
		while (this.bookRot >= (float) Math.PI) this.bookRot -= ((float) Math.PI * 2.0F);
		while (this.bookRot < -(float) Math.PI) this.bookRot += ((float) Math.PI * 2.0F);
		while (this.tRot >= (float) Math.PI) this.tRot -= ((float) Math.PI * 2.0F);
		while (this.tRot < -(float) Math.PI) this.tRot += ((float) Math.PI * 2.0F);
		while (this.itemRot >= (float) Math.PI) itemRot -= ((float) Math.PI * 2.0F);
		while (this.itemRot < -(float) Math.PI) this.itemRot += ((float) Math.PI * 2.0F);
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
	public @NotNull String getNameTranslationKey() {
		return MOD_ID +  "contianer.enchantment.table.name";
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
	public void readAdditionalData(@NotNull CompoundTag tagCompound) {
		ListTag itemList = tagCompound.getList("Items");
		for (int i = 0; i < itemList.tagCount(); i++) {
			CompoundTag itemTag = (CompoundTag) itemList.tagAt(i);
			byte slot = itemTag.getByte("Slot");
			if (slot >= 0 && slot < items.length) {
				items[slot] = ItemStack.readItemStackFromNbt(itemTag);
			}
		}
		if (tagCompound.containsKey("Labels")) {
			this.type = (byte) (tagCompound.getByte("Type") & 0b111);
			ListTag labelTag = tagCompound.getList("Labels");
			for (int i = 0; i < labelIndexes.length; i++) {
				if (i < labelTag.tagCount()) {
					CompoundTag label = (CompoundTag) labelTag.tagAt(i);
					this.labelIndexes[i] = label.getInteger("Index") % LABELS.length;
				} else {
					this.labelIndexes[i] = this.getNewLabel();
				}
			}
		}else{
			this.setRandomLabel();
		}
	}

	@Override
	public void writeAdditionalData(@NotNull CompoundTag tagCompound) {
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
		tagCompound.putByte("Type", this.type);
		ListTag labelTagList = new ListTag();
		for (int i = 0; i < this.labelIndexes.length; i++) {
			int label = labelIndexes[i];
			if (label > LABELS.length || label < 0) {
				label = this.getNewLabel();
			}
			CompoundTag labelTag = new CompoundTag();
			labelTag.putInt("Index", label);
		}
		tagCompound.put("Labels", labelTagList);
	}

	@Override
	public void sort() {
		/* no need for sorting */
	}


	@Override
	public boolean stillValid(@NotNull Player entityplayer) {
		if (this.worldObj != null && this.worldObj.getTileEntity(this.tilePos) == this) {
			return entityplayer.distanceToSqr(this.tilePos.x() + 0.5f, this.tilePos.y() + 0.5f, this.tilePos.z() + 0.5f) <= 64.0;
		} else {
			return false;
		}
	}

	///  getter for EnchantmentTableRenderer (tileentity renderer)
	public int ticks() {return ticks;}
	public float bookRot() {return bookRot;}
	public float prevBookRot() {return prevBookRot;}
	public float prevPageFlip() {return prevPageFlip;}
	public float pageFlip() {return pageFlip;}
	public float bookSpread() {return bookSpread;}
	public float prevBookSpread() {return prevBookSpread;}
	public float itemRot() {return itemRot;}
}
