package googy.betterwithenchanting.enchantment.enchantments;

import googy.betterwithenchanting.enchantment.Enchantment;
import googy.betterwithenchanting.enchantment.EnchantmentTarget;

public class BottledScore extends Enchantment {
	public BottledScore(String name, int id, Rarity weight, EnchantmentTarget target) {
		super(name, id, weight, target);
	}

	@Override
	public int getMaxLevel() {
		return 3;
	}

	@Override
	public int getMinEnchantability(int level) {
		return 15 + (level - 1) * 9;
	}

	@Override
	public int getMaxEnchantability(int level) {
		return super.getMaxEnchantability(level) + 50;
	}
}
