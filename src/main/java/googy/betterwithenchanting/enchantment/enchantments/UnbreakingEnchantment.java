package googy.betterwithenchanting.enchantment.enchantments;

import googy.betterwithenchanting.enchantment.Enchantment;
import googy.betterwithenchanting.enchantment.EnchantmentTarget;
import net.minecraft.core.item.ItemArmor;
import net.minecraft.core.item.ItemStack;

import java.util.Random;

public class UnbreakingEnchantment extends Enchantment {
	static Random random = new Random();

	public UnbreakingEnchantment(String name, int id, Rarity weight, EnchantmentTarget target) {
		super(name, id, weight, target);
	}

	@Override
	public int getMinLevel() {
		return 1;
	}

	@Override
	public int getMaxLevel() {
		return 3;
	}

	@Override
	public int getMinEnchantability(int level) {
		return 5 + (level - 1) * 8;
	}

	@Override
	public int getMaxEnchantability(int level) {
		return super.getMinEnchantability(level) + 50;
	}

	public static boolean shouldNegateDamage(ItemStack stack, int level) {
		if (stack.getItem() instanceof ItemArmor && random.nextFloat() < 0.6f) return true;
		return random.nextInt(level) > 0;
	}
}
