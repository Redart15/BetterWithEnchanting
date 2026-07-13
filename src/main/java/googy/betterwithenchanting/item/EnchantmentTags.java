package googy.betterwithenchanting.item;

import googy.betterwithenchanting.BetterWithEnchanting;
import net.minecraft.core.data.tag.Tag;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.Items;
import net.minecraft.core.item.tag.ItemTags;

import java.lang.reflect.Field;

public class EnchantmentTags {
	public static final Tag<Item> ENCHANT_WEAPON = Tag.of("enchant_weapon");
	public static final Tag<Item> ENCHANT_RANGED = Tag.of("enchant_ranged");
	public static final Tag<Item> ENCHANT_MELEE = Tag.of("enchant_melee");
	// TOOLS
	public static final Tag<Item> ENCHANT_TOOL = Tag.of("enchant_tool");
	public static final Tag<Item> ENCHANT_PICKAXE = Tag.of("enchant_pickaxe");
	public static final Tag<Item> ENCHANT_AXE = Tag.of("enchant_axe");
	public static final Tag<Item> ENCHANT_SHOVEL = Tag.of("enchant_shovel");
	public static final Tag<Item> ENCHANT_HOE = Tag.of("enchant_hoe");
	public static final Tag<Item> ENCHANT_ROD = Tag.of("enchant_rod");
	public static final Tag<Item> ENCHANT_SHEARS = Tag.of("enchant_shear");
	// condumables
	public static final Tag<Item> ENCHANT_CONSUMABLES = Tag.of("enchant_consumables");

	// prohibit enchanting
	public static final Tag<Item> UNECHANT = Tag.of("unechant");
	private static boolean init = false;

	static {
		for (Field field : EnchantmentTags.class.getDeclaredFields()) {
			if (!field.getType().equals(Tag.class)) continue;
			try {
				@SuppressWarnings("unchecked")
				Tag<Item> tag = (Tag<Item>) field.get(null);
				ItemTags.TAG_LIST.add(tag);
			} catch (Exception e) {
				BetterWithEnchanting.LOG.error("Failed to add tag '{}'!", field.getName(), e);
			}
		}
	}

	@SafeVarargs
	public static Tag<Item>[] tags(Tag<Item>... tags) {
		return tags;
	}

	private EnchantmentTags(){}

	public static void init(){
		if(init){
			return;
		}
		init = true;
		Items.ARMOR_QUIVER.withTags(tags(UNECHANT));
		Items.ARMOR_QUIVER_GOLD.withTags(tags(UNECHANT));
		Items.PAINTBRUSH.withTags(tags(UNECHANT));
	}
}
