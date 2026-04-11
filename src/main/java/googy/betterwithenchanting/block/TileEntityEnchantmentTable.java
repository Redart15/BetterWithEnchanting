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

import java.util.Random;

public class TileEntityEnchantmentTable extends TileEntity implements Container {
	protected ItemStack[] items = new ItemStack[2];
	protected Random random = new Random();

	protected int ticks;
	protected float pageFlip;
	protected float prevPageFlip;
	protected float flipT;
	protected float flipA;
	protected float bookSpread;
	protected float prevBookSpread;
	protected float bookRot;
	protected float prevBookRot;
	protected float tRot;
	protected float itemRot;

	public static String[] labels = new String[]{
		"powerful", "strong", "loyal", "vital", "enduring", "focused", "potent", "swift", "agile",
		"unbreaking", "fortunate", "wise", "keen", "resilient", "tireless", "durable", "fierce",
		"lethal", "dominant", "pure", "exalted", "blessed", "enchanced", "elevated",

		"frail", "feeble", "briddle", "cursed", "blighted", "tainted", "rotten", "vulnerable", "exposed",
		"broken", "ruined", "fractured", "crippled", "confused", "dazed", "unstable", "deranged", "delirious",
		"drained", "exhausted", "sinister", "suppressed", "profane", "forsaken"
	};
	private final int[] labelIndexes = new int[3];
	private byte type = 0;

	public void setRandomLabel() {
		for (int i = 0; i < labelIndexes.length; i++) {
			labelIndexes[i] = this.getNewLabel();
		}
		this.type = (byte) this.random.nextInt(8);
	}

	public int 	getNewLabel() {
		return random.nextInt(labels.length);
	}

	public String getAtIndex(int i) {
		return labels[labelIndexes[i % 3] % labels.length];
	}

	public boolean getType(int i) {
		return ((this.type >> (i % 3)) & 1) == 1;
	}

	@Override
	public void tick() {
		ticks++;
		prevBookSpread = bookSpread;
		prevBookRot = bookRot;

		Player player = worldObj.getClosestPlayer((float) this.x + 0.5F, (float) this.y + 0.5F, (float) this.z + 0.5F, 3.0D);
		boolean cRot = false;

		if (player != null) {
			double x = player.x - (double) ((float) this.x + 0.5F);
			double z = player.z - (double) ((float) this.z + 0.5F);
			tRot = (float) Math.atan2(z, x);
			cRot = true;
		}

		if (!cRot) tRot += 0.02F;
		itemRot += 0.03F;

		if (player != null || items[0] != null) {
			bookSpread += 0.1F;
			if (bookSpread < 0.5F || worldObj.rand.nextInt(40) == 0) {
				float f = flipT;
				while (true) {
					flipT += (float) (worldObj.rand.nextInt(4) - worldObj.rand.nextInt(4));
					if (f != flipT) break;
				}
			}
		} else bookSpread -= 0.1F;

		while (bookRot >= (float) Math.PI) bookRot -= ((float) Math.PI * 2.0F);
		while (bookRot < -(float) Math.PI) bookRot += ((float) Math.PI * 2.0F);
		while (tRot >= (float) Math.PI) tRot -= ((float) Math.PI * 2.0F);
		while (tRot < -(float) Math.PI) tRot += ((float) Math.PI * 2.0F);
		while (itemRot >= (float) Math.PI) itemRot -= ((float) Math.PI * 2.0F);
		while (itemRot < -(float) Math.PI) itemRot += ((float) Math.PI * 2.0F);

		float f;

		for (f = tRot - bookRot; f >= (float) Math.PI; f -= (float) Math.PI * 2.0F) ;
		while (f < -(float) Math.PI) f += ((float) Math.PI * 2.0F);

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
		if (tagCompound.containsKey("Labels")) {
			this.type = (byte) (tagCompound.getByte("Type") & 0b111);
			ListTag labelTag = tagCompound.getList("Labels");
			for (int i = 0; i < labelIndexes.length; i++) {
				if (i < labelTag.tagCount()) {
					CompoundTag label = (CompoundTag) labelTag.tagAt(i);
					this.labelIndexes[i] = label.getInteger("Index") % labels.length;
				} else {
					this.labelIndexes[i] = this.getNewLabel();
				}
			}
		}else{
			this.setRandomLabel();
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
		tagCompound.putByte("Type", this.type);
		ListTag labelTagList = new ListTag();
		for (int i = 0; i < this.labelIndexes.length; i++) {
			int label = labelIndexes[i];
			if (label > labels.length || label < 0) {
				label = this.getNewLabel();
			}
			CompoundTag labelTag = new CompoundTag();
			labelTag.putInt("Index", label);
		}
		tagCompound.put("Labels", labelTagList);
	}

	@Override
	public void sortContainer() {
	}

	@Override
	public boolean stillValid(Player entityplayer) {
		if (this.worldObj != null && this.worldObj.getTileEntity(this.x, this.y, this.z) == this) {
			return entityplayer.distanceToSqr(this.x + 0.5, this.y + 0.5, this.z + 0.5) <= 64.0;
		} else {
			return false;
		}
	}
}
