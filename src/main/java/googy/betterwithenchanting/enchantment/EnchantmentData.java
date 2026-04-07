package googy.betterwithenchanting.enchantment;

public class EnchantmentData {
	public final Enchantment enchantment;
	public final int level;

	public EnchantmentData(Enchantment enchantment, int level) {
		this.enchantment = enchantment;
		this.level = level;
	}

	public EnchantmentData(int id, int level) {
		this(Enchantments.getById(id), level);
	}
}
