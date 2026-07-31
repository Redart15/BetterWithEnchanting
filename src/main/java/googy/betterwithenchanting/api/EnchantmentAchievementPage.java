package googy.betterwithenchanting.api;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.achievements.data.AchievementPage;
import net.minecraft.client.gui.achievements.data.AchievementPages;

@Environment(EnvType.CLIENT)
public class EnchantmentAchievementPage {
	private static final AchievementPage OVERWORLD_PAGE = AchievementPages.overworldPage;


	public static void init() {
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
}
