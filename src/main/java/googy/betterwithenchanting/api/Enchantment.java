package googy.betterwithenchanting.api;

import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemStack;

import java.util.Objects;
import java.util.function.IntUnaryOperator;
import java.util.function.Predicate;

public class Enchantment {
	private String id;
	private String translationKey;
	private double weight = 1.0f;
	private int maxLevel = 1;
	private int minLevel = 1;
	private Predicate<Item> target = (item -> true);
	private IntUnaryOperator minEnchantability = (level) -> 1 + (level - 1) + 10;
	private IntUnaryOperator maxEnchatability = (level) -> this.minEnchantability.applyAsInt(level) + 50;

	public Enchantment(String modID, String id) {
		this(modID, id, id.replace('_', '.'));
	}

	public Enchantment(String modID, String id, String translationKey) {
		this.id = String.format("%s:%s", modID, id);
		this.translationKey = String.format("enchantment.%s.%s", modID, translationKey);
	}

	public boolean canApply(Item item) {
		return target.test(item);
	}

	public String id() {
		return id;
	}

	public String translationKey() {
		return translationKey;
	}

	public double weight() {
		return weight;
	}

	public int maxLevel() {
		return maxLevel;
	}

	public int minLevel() {
		return minLevel;
	}

	public int getMinEnchantability(int level) {
		return this.minEnchantability.applyAsInt(level);
	}

	public int getMaxEnchantability(int level) {
		return this.maxEnchatability.applyAsInt(level);
	}

	public final boolean canEnchant(ItemStack itemStack) {
		if (itemStack == null || itemStack.getMaxStackSize() > 1) {
			return false;
		}
		return this.canApply(itemStack.getItem());
	}

	public Enchantment setWeight(double chance) {
		this.weight = chance;
		return this;
	}

	public Enchantment setMaxLevel(int maxLevel) {
		this.maxLevel = maxLevel;
		return this;
	}

	public Enchantment setMinLevel(int minLevel) {
		this.minLevel = minLevel;
		return this;
	}

	public Enchantment setTarget(Predicate<Item> target) {
		this.target = target;
		return this;
	}

	public Enchantment setMinEnchantability(IntUnaryOperator minEnchantability) {
		this.minEnchantability = minEnchantability;
		return this;
	}

	public Enchantment setMaxEnchatability(IntUnaryOperator maxEnchatability) {
		this.maxEnchatability = maxEnchatability;
		return this;
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof Enchantment)) return false;
		Enchantment that = (Enchantment) object;
		return Objects.equals(id, that.id);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(id);
	}
}

