package googy.betterwithenchanting.utils;

import com.mojang.nbt.tags.CompoundTag;
import com.mojang.nbt.tags.ListTag;
import googy.betterwithenchanting.BetterWithEnchanting;
import googy.betterwithenchanting.enchantment.Enchantment;
import googy.betterwithenchanting.enchantment.EnchantmentData;
import googy.betterwithenchanting.enchantment.Enchantments;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemStack;

import java.util.*;

public class EnchantmentUtils {
	public static final String ENCHANTMENT_DATA_KEY = "enchantmentData";
	public static final String ENCHANTMENT_LIST_KEY = "enchantments";
	private static final String ID_KEY = "id";
	private static final String LEVEL_KEY = "lvl";

	public static CompoundTag createEnchantmentTag(Enchantment enchantment, int level) {
		CompoundTag tag = new CompoundTag();
		tag.putShort(ID_KEY, (byte) enchantment.id);
		tag.putShort(LEVEL_KEY, (byte) level);
		return tag;
	}

	public static CompoundTag createEnchantmentTag(EnchantmentData data) {
		return createEnchantmentTag(data.enchantment, data.level);
	}

	public static CompoundTag createEnchantmentsTag(List<EnchantmentData> enchantments) {
		ListTag enchantList = new ListTag();

		for (EnchantmentData data : enchantments) {
			CompoundTag enchantTag = createEnchantmentTag(data);
			enchantList.addTag(enchantTag);
		}

		CompoundTag enchantmentsTag = new CompoundTag();
		enchantmentsTag.putList(ENCHANTMENT_LIST_KEY, enchantList);

		return enchantmentsTag;
	}

	public static void setEnchantmentsTag(ItemStack stack, List<EnchantmentData> enchantments) {
		CompoundTag enchantmentsTag = createEnchantmentsTag(enchantments);
		stack.getData().putCompound(ENCHANTMENT_DATA_KEY, enchantmentsTag);
	}

	public static void addEnchantment(ItemStack stack, Enchantment enchantment, int level) {
		if (containsEnchantment(stack, enchantment))
			return;

		CompoundTag enchantTag = createEnchantmentTag(enchantment, level);

		ListTag enchantmentList = getEnchantmentsList(stack);
		enchantmentList.addTag(enchantTag);

		CompoundTag enchantmentsTag = new CompoundTag();
		enchantmentsTag.putList(ENCHANTMENT_LIST_KEY, enchantmentList);

		stack.getData().putCompound(ENCHANTMENT_DATA_KEY, enchantmentsTag);
	}

	public static void addEnchantment(ItemStack stack, EnchantmentData enchantmentData) {
		addEnchantment(stack, enchantmentData.enchantment, enchantmentData.level);
	}


	public static void addEnchantments(ItemStack stack, List<EnchantmentData> enchantments) {
		for (EnchantmentData enchantment : enchantments)
			addEnchantment(stack, enchantment);
	}

	public static boolean containsEnchantment(ItemStack stack, Enchantment enchantment) {
		ListTag enchantList = getEnchantmentsList(stack);

		for (int i = 0; i < enchantList.tagCount(); i++) {
			CompoundTag enchantTag = (CompoundTag) enchantList.tagAt(i);
			int id = enchantTag.getShort(ID_KEY);

			if (id == enchantment.id) return true;
		}

		return false;
	}

	public static ListTag getEnchantmentsList(ItemStack stack) {
		CompoundTag enchantData = stack.getData().getCompound(ENCHANTMENT_DATA_KEY);
		return enchantData.getList(ENCHANTMENT_LIST_KEY);
	}

	public static List<EnchantmentData> getEnchantments(ItemStack stack) {
		List<EnchantmentData> enchantments = new ArrayList<>();
		if(stack == null){
			return enchantments;
		}
		ListTag enchantList = getEnchantmentsList(stack);

		for (int i = 0; i < enchantList.tagCount(); i++) {
			CompoundTag enchantTag = (CompoundTag) enchantList.tagAt(i);
			int id = enchantTag.getShort(ID_KEY);
			int level = enchantTag.getShort(LEVEL_KEY);

			enchantments.add(new EnchantmentData(id, level));
		}

		return enchantments;
	}

	public static int getLevel(ItemStack stack, Enchantment enchantment) {
		if (stack == null || stack.getItem() == null || stack.stackSize <= 0)
			return 0;

		ListTag enchantList = getEnchantmentsList(stack);

		for (int i = 0; i < enchantList.tagCount(); i++) {
			CompoundTag enchantTag = (CompoundTag) enchantList.tagAt(i);

			if (enchantment.id == enchantTag.getShort(ID_KEY))
				return enchantTag.getShort(LEVEL_KEY);
		}

		return 0;
	}

	public static List<EnchantmentData> getPossible(ItemStack stack, int enchantability) {
		List<EnchantmentData> list = new ArrayList<>();
		Item item = stack.getItem();

		for (Enchantment enchantment : Enchantments.enchantmentList) {
			if (enchantment == null || !enchantment.canEnchant(item)) continue;

			for (int level = enchantment.getMinLevel(); level <= enchantment.getMaxLevel(); level++) {
				if (enchantability >= enchantment.getMinEnchantability(level) && enchantability <= enchantment.getMaxEnchantability(level)) {
					list.add(new EnchantmentData(enchantment, level));
				}
			}
		}


		return list;
	}

	public static List<EnchantmentData> generateEnchantmentsList(Random random, ItemStack stack, int cost) {
		List<EnchantmentData> enchantments = new ArrayList<>();
		double costPercentage = (double) cost / BetterWithEnchanting.config.getInt("max_enchantment_cost");

		int itemEnchantability = BetterWithEnchanting.config.getInt("default_item_enchantability");

		int rand_enchantability = 1 + random.nextInt(itemEnchantability / 4 + 1) + random.nextInt(itemEnchantability / 4 + 1);
		int k = (int) (costPercentage * (30 - 1) + 1) + rand_enchantability;
		float rand_bonus_percent = 1 + (random.nextFloat() + random.nextFloat() - 1) * 0.15f;

		int enchantability = Math.round(k * rand_bonus_percent);
		if (enchantability < 0) enchantability = 1;

		List<EnchantmentData> possibleEnchantments = getPossible(stack, enchantability);
		if (possibleEnchantments.isEmpty()) return null;

		EnchantmentData randomEnchantment = possibleEnchantments.get(random.nextInt(possibleEnchantments.size()));
		if (randomEnchantment != null) {
			enchantments.add(randomEnchantment);
			possibleEnchantments.remove(randomEnchantment);
		}

		for (int i = enchantability; random.nextInt(50) <= i; i >>= 1) {
			if (possibleEnchantments.isEmpty()) continue;

			randomEnchantment = possibleEnchantments.get(random.nextInt(possibleEnchantments.size()));
			if (randomEnchantment != null) {
				enchantments.add(randomEnchantment);
				possibleEnchantments.remove(randomEnchantment);
			}
		}

		return enchantments;
	}

	public static int calcEnchantmentCost(int enchantOption, int bookshelfs) {
		double percentage = (bookshelfs + BetterWithEnchanting.START_COST_OFFSET) / (15.0 + BetterWithEnchanting.START_COST_OFFSET);
		percentage *= (enchantOption + 1) / 3.0;

		return (int) (BetterWithEnchanting.config.getInt("max_enchantment_cost") * percentage);
	}

}
