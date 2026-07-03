package googy.betterwithenchanting.api;

import net.minecraft.core.item.Item;

import java.util.function.IntUnaryOperator;
import java.util.function.Predicate;

public class EnchantmentBuilder {
	private final Enchantment enchantment;
	private double weight = 10.0f;
	private int maxLevel = 1;
	private int minLevel = 1;
	private Predicate<Item> target = (item -> true);
	private IntUnaryOperator minEnchantability = level -> 1 + (level - 1) + 10;
	private IntUnaryOperator maxEnchatability = level -> this.minEnchantability.applyAsInt(level) + 50;
	private boolean hidden = false;

	public EnchantmentBuilder(Enchantment enchantment) {
		this.enchantment = enchantment;
	}

	public EnchantmentBuilder setWeight(double chance) {
		this.weight = chance;
		return this;
	}

	public EnchantmentBuilder setMaxLevel(int maxLevel) {
		this.maxLevel = maxLevel;
		return this;
	}

	public EnchantmentBuilder setMinLevel(int minLevel) {
		this.minLevel = minLevel;
		return this;
	}

	public EnchantmentBuilder setTarget(Predicate<Item> target) {
		this.target = target;
		return this;
	}

	public EnchantmentBuilder setMinEnchantability(IntUnaryOperator minEnchantability) {
		this.minEnchantability = minEnchantability;
		return this;
	}

	public EnchantmentBuilder setMaxEnchatability(IntUnaryOperator maxEnchatability) {
		this.maxEnchatability = maxEnchatability;
		return this;
	}

	public EnchantmentBuilder setHidden(boolean hidden) {
		this.hidden = hidden;
		return this;
	}

	public EnchantmentBuilder setHidden() {
		this.hidden = true;
		return this;
	}

	public EnchantmentBuilder setEnchantability(int start, int end, double overlay) {
		int[] values = EnchantmentBuilder.getFactorAndAdder(this.maxLevel, start, end, overlay);
		this.minEnchantability = level -> (level - 1) * values[0] + start;
		this.maxEnchatability =  level ->(level - 1) * values[0] + values[1] + start;
		return this;
	}

	public EnchantmentBuilder setEnchantability(int max, int start, int end, double overlay) {
		int[] values = EnchantmentBuilder.getFactorAndAdder(max, start, end, overlay);
		this.maxLevel = max;
		this.minEnchantability = level -> (level - 1) * values[0] + start;
		this.maxEnchatability =  level ->(level - 1) * values[0] + values[1] + start;
		return this;
	}

	public EnchantmentBuilder setEnchantability(int start, int end, double overlay, int lastAvailableLevel) {
		int[] values = EnchantmentBuilder.getFactorAndAdder(this.maxLevel, start, end, overlay, lastAvailableLevel);
		this.minEnchantability = level -> (level - 1) * values[0] + start;
		this.maxEnchatability =  level ->(level - 1) * values[0] + values[1] + start;
		return this;
	}

	public EnchantmentBuilder setEnchantability(int max, int start, int end, double overlay, int lastAvailableLevel) {
		int[] values = EnchantmentBuilder.getFactorAndAdder(max, start, end, overlay, lastAvailableLevel);
		this.maxLevel = max;
		this.minEnchantability = level -> (level - 1) * values[0] + start;
		this.maxEnchatability =  level ->(level - 1) * values[0] + values[1] + start;
		return this;
	}

	public Enchantment build() {
		this.enchantment
			.setWeight(this.weight)
			.setTarget(this.target)
			.setMaxLevel(this.maxLevel)
			.setMinLevel(this.minLevel)
			.setHidden(this.hidden)
			.setMinEnchantability(this.minEnchantability)
			.setMaxEnchatability(this.maxEnchatability);
		Enchantments.getInstance().register(enchantment.id(), enchantment);
		return this.enchantment;
	}

	public static int[] getFactorAndAdder(int maxLevel, int start, int end, double overlap){
		double diff = end - (double)start;
		double len = 1.0f + (1 - overlap) * (maxLevel - 1);
		double windowSize = diff / len;
		return new int[]{(int)Math.ceil((1 - overlap) * (windowSize)), (int)Math.ceil(windowSize) };
	}


	public static int[] getFactorAndAdder(int maxLevel, int start, int end, double overlap, int lastLevel){
		double diff = end - (double)start;
		double len = 1.0f + (1 - overlap) * (lastLevel) - 1;
		double windowSize = diff / len;
		return new int[]{(int)Math.ceil((1 - overlap) * (windowSize)), (int)Math.ceil(windowSize) };
	}

	/**
	 * @deprecated Old enchantment system used numerical id, that are only here for compatibility reason.
	 * Do not use them!
	 */
	@Deprecated
	public EnchantmentBuilder(Enchantment enchantment, int intID) {
		this.enchantment = enchantment;
		this.enchantment.setIntID(intID);
	}
}
