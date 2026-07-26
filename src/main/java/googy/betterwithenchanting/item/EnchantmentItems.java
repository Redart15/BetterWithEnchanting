package googy.betterwithenchanting.item;


import net.minecraft.core.data.tag.Tag;
import net.minecraft.core.item.Item;

import net.minecraft.core.item.tag.ItemTags;

import static googy.betterwithenchanting.BetterWithEnchanting.*;

public class EnchantmentItems {
	public static Item SCORE_BOTTLE;
	public static Item ENCHANTED_BOOK;

	private static String formatTranslationKey(String key) {
		return String.format("%s.%s", MOD_ID, key);
	}

	private static String formatName(String name) {
		return String.format("%s:item/%s", MOD_ID, name);
	}

	public static void afterItemInit() {
		int startingId = ITEM_ID;
		SCORE_BOTTLE = new ItemEnchantmentBottle(
			formatTranslationKey("bottle.score"),
			formatName("bottle_score"),
			startingId++
		).withTags(new Tag[]{ItemTags.NOT_IN_CREATIVE_MENU});

		ENCHANTED_BOOK = new ItemEnchantedBook(
			formatTranslationKey("book.enchanted"),
			formatName("book_enchanted"),
			startingId++
		)
			.withTags(new Tag[]{ItemTags.NOT_IN_CREATIVE_MENU})
			.setMaxStackSize(1);

	}
}
