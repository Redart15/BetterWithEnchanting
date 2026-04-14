package googy.betterwithenchanting.item;

import googy.betterwithenchanting.BetterWithEnchanting;
import net.minecraft.core.data.tag.Tag;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.tag.ItemTags;

import java.lang.reflect.Field;

public class BetterWithEnchantingTags {
	public static final Tag<Item> ENCHANT_WEAPON = Tag.of("enchant_weapon");
	public static final Tag<Item> ENCHANT_RANGED = Tag.of("enchant_ranged");
	public static final Tag<Item> ENCHANT_MEELE = Tag.of("enchant_meele");
	public static final Tag<Item> ENCHANT_DIGGER = Tag.of("enchant_digger");
	public static final Tag<Item> ENCHANT_TOOL = Tag.of("enchant_tool");
	public static final Tag<Item> ENCHANT_ARMOR = Tag.of("enchant_armor");

	static {
		for (Field field : BetterWithEnchantingTags.class.getDeclaredFields()) {
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

	private BetterWithEnchantingTags(){}
}
