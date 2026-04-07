package googy.betterwithenchanting.enchantment.enchantments;

import googy.betterwithenchanting.enchantment.Enchantment;
import googy.betterwithenchanting.enchantment.EnchantmentTarget;

public class FlameEnchantment extends Enchantment {
	public FlameEnchantment(String name, int id, Rarity weight, EnchantmentTarget target) {
		super(name, id, weight, target);
	}

	@Override
	public int getMaxLevel() {
		return 2;
	}

	@Override
	public int getMinEnchantability(int level) {
		return 10 + 20 * (level - 1);
	}

	@Override
	public int getMaxEnchantability(int level) {
		return super.getMaxEnchantability(level) + 50;
	}
}
