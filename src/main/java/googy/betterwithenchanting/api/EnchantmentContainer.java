package googy.betterwithenchanting.api;

import com.mojang.nbt.tags.CompoundTag;
import com.mojang.nbt.tags.ListTag;
import net.minecraft.core.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static googy.betterwithenchanting.BetterWithEnchanting.*;

public class EnchantmentContainer {
	public static final String ENCHANTMENT_DATA_KEY = "enchantmentData";
	public static final String ENCHANTMENT_LIST_KEY = "enchantments";
	private static final String ID_KEY = "id";
	private static final String LEVEL_KEY = "lvl";

	public static CompoundTag createContainer(List<EnchantmentData> enchantments) {
		ListTag enchantList = new ListTag();
		for (EnchantmentData data : enchantments) {
			CompoundTag enchantTag = createEnchantTag(data.enchantment, data.level);
			enchantList.addTag(enchantTag);
		}
		CompoundTag enchantmentsTag = new CompoundTag();
		enchantmentsTag.putList(ENCHANTMENT_LIST_KEY, enchantList);
		return enchantmentsTag;
	}

	private static @NotNull CompoundTag createEnchantTag(Enchantment enchantment, int level) {
		CompoundTag enchantTag = new CompoundTag();
		enchantTag.putString(ID_KEY, enchantment.id());
		enchantTag.putShort(LEVEL_KEY, (byte) level);
		return enchantTag;
	}

	public static int calcEnchantCost(int enchantOption, int bookshelfs) {
		double percentage = (bookshelfs + START_COST_OFFSET) / (15.0 + START_COST_OFFSET);
		percentage *= (enchantOption + 1) / 3.0;
		return (int) Math.ceil(MAX_ENCHANTMENT_COST * percentage);
	}

	public static void addEnchantment(ItemStack stack, EnchantmentData enchantmentData) {
		Enchantment enchantment = enchantmentData.enchantment;
		int level = enchantmentData.level;
		if (contains(stack, enchantment)) {
			return;
		}
		CompoundTag enchantTag = createEnchantTag(enchantment, level);
		ListTag enchantList = getEnchantmentsList(stack);
		enchantList.addTag(enchantTag);
		CompoundTag enchantmentsTag = new CompoundTag();
		enchantmentsTag.putList(ENCHANTMENT_LIST_KEY, enchantList);
		stack.getData().putCompound(ENCHANTMENT_DATA_KEY, enchantmentsTag);
	}

	public static void addEnchantments(ItemStack stack, List<EnchantmentData> enchantments) {
		for (EnchantmentData enchantment : enchantments) {
			addEnchantment(stack, enchantment);
		}
	}

	public static List<EnchantmentData> getEnchantments(ItemStack stack) {
		List<EnchantmentData> enchantments = new ArrayList<>();
		if(stack == null){
			return enchantments;
		}
		ListTag enchantList = getEnchantmentsList(stack);
		for (int i = 0; i < enchantList.tagCount(); i++) {
			CompoundTag enchantTag = (CompoundTag) enchantList.tagAt(i);
			String id = enchantTag.getString(ID_KEY);
			int level = enchantTag.getShort(LEVEL_KEY);
			enchantments.add(new EnchantmentData(Enchantments.getInstance().getItem(id), level));
		}
		return enchantments;
	}

	public static ListTag getEnchantmentsList(ItemStack stack) {
		CompoundTag enchantData = stack.getData().getCompound(ENCHANTMENT_DATA_KEY);
		return enchantData.getList(ENCHANTMENT_LIST_KEY);
	}

	public static boolean contains(ItemStack stack, Enchantment enchantment) {
		ListTag enchantList = getEnchantmentsList(stack);
		for (int i = 0; i < enchantList.tagCount(); i++) {
			CompoundTag enchantTag = (CompoundTag) enchantList.tagAt(i);
			String id = enchantTag.getString(ID_KEY);
			if (enchantment.id().equals(id)) {
				return true;
			}
		}
		return false;
	}

	public static @Nullable EnchantmentData getEnchantmentData(@NotNull ItemStack stack, Enchantment enchantment) {
		ListTag enchantList = getEnchantmentsList(stack);
		for (int i = 0; i < enchantList.tagCount(); i++) {
			CompoundTag enchantTag = (CompoundTag) enchantList.tagAt(i);
			String id = enchantTag.getString(ID_KEY);
			if (enchantment.id().equals(id)) {
				int level = enchantTag.getShort(LEVEL_KEY);
				return new EnchantmentData(Enchantments.getInstance().getItem(id), level);
			}
		}
		return null;
	}

	public static int getLevel(ItemStack stack, Enchantment enchantment) {
		if (stack == null || stack.stackSize <= 0) {
			return 0;
		}
		EnchantmentData data = getEnchantmentData(stack, enchantment);
		return data == null ? 0 : Math.max(data.level, 0);
	}

	public static List<EnchantmentData> getPossible(ItemStack stack, int enchantability) {
		List<EnchantmentData> list = new ArrayList<>();
		for (Enchantment enchantment : Enchantments.getInstance()) {
			if (enchantment == null || !enchantment.canEnchant(stack)) {
				continue;
			}
			for (int level = enchantment.minLevel(); level <= enchantment.maxLevel(); level++) {
				if (enchantability >= enchantment.getMinEnchantability(level) && enchantability <= enchantment.getMaxEnchantability(level)) {
					list.add(new EnchantmentData(enchantment, level));
				}
			}
		}
		return list;
	}

	public static List<EnchantmentData> generateEnchantmentsList(Random random, ItemStack stack, int cost) {
		// Normalize cost into percentage
		double costPercentage = (double) cost / MAX_ENCHANTMENT_COST;

		// Compute base enchantability
		int randEnchantability = 1 + random.nextInt(DEFAULT_ITEM_ENCHANTABILITY / 4 + 1) + random.nextInt(DEFAULT_ITEM_ENCHANTABILITY / 4 + 1);
		int k = (int) (costPercentage * (30 - 1) + 1) + randEnchantability;

		// Apply random bonus (±15%)
		float randBonusPercent = 1 + (random.nextFloat() + random.nextFloat() - 1) * 0.15f;
		int enchantability = Math.max(1, Math.round(k * randBonusPercent));

		List<EnchantmentData> enchantmentResults = new ArrayList<>();
		List<EnchantmentData> enchantmentPool = getPossible(stack, enchantability);
		if (enchantmentPool.isEmpty()) {
			return enchantmentResults;
		}
		enchantmentResults.add(enchantmentPool.remove(random.nextInt(enchantmentPool.size())));
		int current = enchantability;
		while(!enchantmentPool.isEmpty() && random.nextInt(50) <= current){
			enchantmentResults.add(enchantmentPool.remove(random.nextInt(enchantmentPool.size())));
			current >>= 1;
		}
		return enchantmentResults;
	}

	public static class EnchantmentData {
		public final Enchantment enchantment;
		public final int level;
		public EnchantmentData(Enchantment enchantment, int level) {
			this.enchantment = enchantment;
			this.level = level;
		}
	}
}
