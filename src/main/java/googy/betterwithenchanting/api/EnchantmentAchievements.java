package googy.betterwithenchanting.api;

import googy.betterwithenchanting.block.EnchantmentBlocks;
import net.minecraft.client.gui.achievements.data.AchievementPage;
import net.minecraft.client.gui.achievements.data.AchievementPages;
import net.minecraft.core.achievement.Achievement;
import net.minecraft.core.achievement.Achievements;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.Items;
import net.minecraft.core.util.collection.NamespaceID;
import net.minecraft.core.util.helper.DyeColor;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static googy.betterwithenchanting.BetterWithEnchanting.*;

public class EnchantmentAchievements {
	private static final AchievementPage OVERWORLD_PAGE = AchievementPages.overworldPage;

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
		ItemStack book = new ItemStack(Items.BOOK);
		EnchantmentContainer.rawAddEnchantment(book, Enchantments.INSIGHT.getDefaultStack());
		ItemStack lapiz = new ItemStack(Items.DYE, 64, DyeColor.BLUE.itemMeta);

		CRAFT_ENCHANTER = new Achievement(
			NamespaceID.fromPool(MOD_ID, "craft_enchantmenttable"),
			"craft.enchantmenttable", EnchantmentBlocks.ENCHANTMENT_TABLE, Achievements.GET_DIAMONDS
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
			"score", Items.FOOD_APPLE_GOLD, null
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

    public static void applyHighEnchant(Player player, List<EnchantmentStack> stacks) {
        if (player.getStat(HIGH_LEVEL_ENCHANT) != 0) {
            return;
        }
        for (EnchantmentStack stack : stacks) {
            Enchantment enchantment = stack.getEnchantment();
            int level = stack.getLevel();
            int minScore = EnchantmentContainer.calcCostFromEnchantability(enchantment.getMinEnchantability(level), false);
            if (minScore > MAX_ENCHANTMENT_COST && level == enchantment.maxLevel()) {
				player.triggerAchievement(HIGH_LEVEL_ENCHANT);
                break;
            }
        }
    }

    public static void applyFullEnchant(Player player, @NotNull ItemStack enchantItem, List<EnchantmentStack> stacks) {
        if(player.getStat(FULL_ENCHANTED) == 0){
            int count = 0;
            ItemStack controll = new ItemStack(enchantItem.getItem());
            for (Enchantment enchantment : Enchantments.getInstance()) {
                if (enchantment.canEnchant(controll)) {
                    count++;
                }
            }
            if(count == stacks.size() && count > 2){
				player.triggerAchievement(FULL_ENCHANTED);
            }
        }
    }
}
