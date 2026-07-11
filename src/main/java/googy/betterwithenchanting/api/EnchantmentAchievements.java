package googy.betterwithenchanting.api;

import net.minecraft.client.gui.achievements.data.AchievementPage;
import net.minecraft.client.gui.achievements.data.AchievementPages;
import net.minecraft.core.achievement.Achievement;
import net.minecraft.core.achievement.Achievements;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.Items;
import net.minecraft.core.util.collection.NamespaceID;
import net.minecraft.core.util.helper.DyeColor;

import static googy.betterwithenchanting.BetterWithEnchanting.*;

public class EnchantmentAchievements {
	private static final AchievementPage OVERWORLD_PAGE = AchievementPages.overworldPage;
	private static final AchievementPage NETHER_PAGE = AchievementPages.netherPage;


	public static final Achievement CRAFT_ENCHANTER;
	public static final Achievement ENCHANT_ITEM;
	public static final Achievement FULL_ENCHANTED;
	public static final Achievement ENCHANTED_FOOD;
	public static final Achievement HIGH_LEVEL_ENCHANT;
	public static final Achievement SCORE;
	public static final Achievement LOST_KNOWLEDGE;

	static {

		ItemStack enchantedItem = new ItemStack(Items.TOOL_AXE_GOLD);
		EnchantmentContainer.rawAddEnchantment(enchantedItem, Enchantments.CRIT.getDefaultStack());
		ItemStack highEnchanted = new ItemStack(Items.TOOL_SWORD_DIAMOND);
		EnchantmentContainer.rawAddEnchantment(highEnchanted, Enchantments.CRIT.getDefaultStack());
		ItemStack enchantedBread = new ItemStack(Items.FOOD_APPLE);
		EnchantmentContainer.rawAddEnchantment(enchantedBread, Enchantments.NOURISHMENT.getDefaultStack());
		ItemStack scoreBottle = new ItemStack(SCORE_BOTTLE);
		EnchantmentContainer.rawAddEnchantment(scoreBottle, Enchantments.BOTTLED_SCORE.getDefaultStack());
		ItemStack book = new ItemStack(Items.BOOK);
		EnchantmentContainer.rawAddEnchantment(book, Enchantments.BOTTLED_SCORE.getDefaultStack());
		ItemStack lapiz = new ItemStack(Items.DYE, 1, DyeColor.BLUE.itemMeta);

		CRAFT_ENCHANTER = new Achievement(
			NamespaceID.fromPool(MOD_ID, "craft_enchantmenttable"),
			"craft.enchantmenttable", ENCHANTMENT_TABLE, Achievements.GET_DIAMONDS
		).registerAchievement();

		ENCHANT_ITEM = new Achievement(
			NamespaceID.fromPool(MOD_ID, "enchant_item"),
			"enchant.item", enchantedItem, CRAFT_ENCHANTER
		).registerAchievement();

		FULL_ENCHANTED = new Achievement(
			NamespaceID.fromPool(MOD_ID, "enchant_item_full"),
			"enchant.item.full", lapiz, ENCHANT_ITEM
		).registerAchievement();

		HIGH_LEVEL_ENCHANT = new Achievement(
			NamespaceID.fromPool(MOD_ID, "enchant_item_high_level"),
			"enchant.item.high.level", highEnchanted, ENCHANT_ITEM
		).setType(Achievement.TYPE_SPECIAL).registerAchievement();

		ENCHANTED_FOOD = new Achievement(
			NamespaceID.fromPool(MOD_ID, "enchant_food"),
			"enchant.food", enchantedBread, ENCHANT_ITEM
		).registerAchievement();


		SCORE = new Achievement(
			NamespaceID.fromPool(MOD_ID, "score"),
			"score", scoreBottle, null
		).registerAchievement();

		LOST_KNOWLEDGE = new Achievement(
			NamespaceID.fromPool(MOD_ID, "lost_knowlegde"),
			"lost.knowlegde", book, null
		).setType(Achievement.TYPE_SECRET).registerAchievement();

	}


	public static void init(){
		int tableEnchantX = -4;
		int tableEnchantY = 2;
		OVERWORLD_PAGE.addAchievement(EnchantmentAchievements.CRAFT_ENCHANTER, tableEnchantX, tableEnchantY);
		int itemEnchantX = tableEnchantX - 1;
		int itemEnchantY = tableEnchantY - 2;
		OVERWORLD_PAGE.addAchievement(EnchantmentAchievements.ENCHANT_ITEM, itemEnchantX, itemEnchantY);
		OVERWORLD_PAGE.addAchievement(EnchantmentAchievements.ENCHANT_ITEM, itemEnchantX, itemEnchantY);
		OVERWORLD_PAGE.addAchievement(EnchantmentAchievements.FULL_ENCHANTED, itemEnchantX + 2, itemEnchantY - 1);
		OVERWORLD_PAGE.addAchievement(EnchantmentAchievements.HIGH_LEVEL_ENCHANT, itemEnchantX - 1, itemEnchantY - 3);
		OVERWORLD_PAGE.addAchievement(EnchantmentAchievements.ENCHANTED_FOOD, itemEnchantX - 2, itemEnchantY);
		OVERWORLD_PAGE.addAchievement(EnchantmentAchievements.SCORE, 11, 3);
		OVERWORLD_PAGE.addAchievement(EnchantmentAchievements.LOST_KNOWLEDGE, 9, 5);
	}

	private EnchantmentAchievements(){}
}
