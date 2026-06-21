package googy.betterwithenchanting.api;

import com.mojang.nbt.tags.CompoundTag;
import net.minecraft.core.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import static googy.betterwithenchanting.BetterWithEnchanting.TRANSLATE;

public class EnchantmentStack {
	private static final String SID_KEY = "sid";
	private static final String LEVEL_KEY = "lvl";
	private static final String ID_KEY = "id";

	private Enchantment enchantment;
	private int level;

	public EnchantmentStack(CompoundTag tag) {
		this.readNBT(tag);
	}

	public EnchantmentStack(Enchantment enchantment, int level) {
		this.enchantment = enchantment;
		this.setLevel(level);
	}

	@NotNull
	public CompoundTag readNBT(CompoundTag nbt) {
		if (nbt.containsKey(ID_KEY)) {
			int id = nbt.getShort(ID_KEY);
			for (Enchantment enchantment : Enchantments.getInstance()) {
				if (enchantment.getIntID() == id) {
					this.enchantment = enchantment;
					break;
				}
			}
		} else {
			this.enchantment = Enchantments.getInstance().getItem(nbt.getString(SID_KEY));
		}
		this.level = nbt.getShort(LEVEL_KEY);
		return nbt;
	}

	@NotNull
	public CompoundTag writeNBT(CompoundTag nbt) {
		nbt.putString(SID_KEY, this.enchantment.id());
		nbt.putShort(LEVEL_KEY, (byte) this.level);
		return nbt;
	}

	public int getLevel() {
		return this.level;
	}

	public EnchantmentStack setLevel(int level) {
		this.level = Math.min(Math.max(level, this.minLevel()), this.maxLevel());
		return this;
	}

	public Enchantment getEnchantment() {
		return this.enchantment;
	}

	public String id() {
		return this.enchantment.id();
	}

	public String getTranslationKey() {
		return this.enchantment.translationKey();
	}

	public double getWeight(int level) {
		return this.enchantment.getWeight(level);
	}

	public int maxLevel() {
		return this.enchantment.maxLevel();
	}

	public int minLevel() {
		return this.enchantment.minLevel();
	}

	public final boolean canEnchant(ItemStack itemStack) {
		return this.enchantment.canEnchant(itemStack);
	}

	public boolean isHidden() {
		return this.enchantment.hidden();
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof EnchantmentStack)) return false;
		EnchantmentStack that = (EnchantmentStack) object;
		return level == that.level && Objects.equals(enchantment, that.enchantment);
	}

	@Override
	public int hashCode() {
		return Objects.hash(enchantment, level);
	}

	@Override
	public String toString() {
		return "enchantmentStack{" +
			"enchant=" + enchantment.id() +
			", lvl=" + level +
			'}';
	}

	public String prettyToString(){
		String levelString = enchantment.maxLevel() > 1 ? String.format("(%s)", this.getLevel()) : "";
		return String.format("%s %s", this.enchantment.translationKeyName(), levelString);
	}
}
