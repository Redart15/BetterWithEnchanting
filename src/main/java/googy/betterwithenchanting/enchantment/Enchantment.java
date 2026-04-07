package googy.betterwithenchanting.enchantment;

import googy.betterwithenchanting.BetterWithEnchanting;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.lang.I18n;

public class Enchantment {
	public int id;
	public String key;

	public final Rarity rarity;
	public final EnchantmentTarget target;


	public Enchantment(String name, int id, Rarity weight, EnchantmentTarget target) {
		this.id = id;
		Enchantments.enchantmentList[id] = this;
		setKey(name);
		this.rarity = weight;
		this.target = target;
	}

	public Enchantment setKey(String s) {
		key = "enchantment." + BetterWithEnchanting.MOD_ID + "." + s;
		Enchantments.nameToIdMap.put(key, id);
		return this;
	}

	public String getName() {
		return I18n.getInstance().translateKey(key + ".name");
	}

	public boolean canEnchant(Item item) {
		return target.canEnchant(item);
	}

	public boolean canEnchant(ItemStack stack) {
		if (stack == null || stack.getItem() == null) return false;

		return canEnchant(stack.getItem());
	}


	public int getMinLevel() {
		return 1;
	}

	public int getMaxLevel() {
		return 1;
	}

	public int getMinEnchantability(int level) {
		return 1 + level * 10;
	}

	public int getMaxEnchantability(int level) {
		return this.getMinEnchantability(level) + 5;
	}

	public void onTargetDamaged(Player user, Entity target, int level) {

	}

	public void onUserDamaged(Player user, Entity attacker, int level) {

	}

	public static enum Rarity {

		COMMON(10),
		UNCOMMON(5),
		RARE(2),
		VERY_RARE(1);

		private final int weight;

		private Rarity(int weight) {
			this.weight = weight;
		}

		public int getWeight() {
			return this.weight;
		}
	}

}
