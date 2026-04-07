package googy.betterwithenchanting.enchantment;

import googy.betterwithenchanting.enchantment.enchantments.*;
import net.minecraft.core.item.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Enchantments {
	public static final Map<String, Integer> nameToIdMap = new HashMap<String, Integer>();

	public static Enchantment[] enchantmentList = new Enchantment[128];

	public static Enchantment getById(int i) {
		return enchantmentList[i];
	}

	public static List<Enchantment> getPossible(Item item) {
		List<Enchantment> enchantments = new ArrayList<>();

		for (Enchantment enchant : enchantmentList) {
			if (enchant == null) continue;
			if (enchant.canEnchant(item)) enchantments.add(enchant);
		}

		return enchantments;
	}

	public static Enchantment haste = new HasteEnchantment("haste", 1, Enchantment.Rarity.COMMON, EnchantmentTarget.DIGGER);
	public static Enchantment unbreaking = new UnbreakingEnchantment("unbreaking", 2, Enchantment.Rarity.COMMON, EnchantmentTarget.BREAKABLE);
	public static Enchantment quickstrike = new QuickstrikeEnchantment("quickswing", 3, Enchantment.Rarity.RARE, EnchantmentTarget.TOOL);
	public static Enchantment flame = new FlameEnchantment("flame", 5, Enchantment.Rarity.RARE, EnchantmentTarget.WEAPON);
	public static Enchantment bait = new BaitEnchantment("bait", 8, Enchantment.Rarity.RARE, EnchantmentTarget.FISHING_ROD);


}
