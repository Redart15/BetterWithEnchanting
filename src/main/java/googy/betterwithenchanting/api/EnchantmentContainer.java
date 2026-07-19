package googy.betterwithenchanting.api;

import com.mojang.nbt.tags.CompoundTag;
import com.mojang.nbt.tags.ListTag;
import com.mojang.nbt.tags.Tag;
import net.minecraft.core.WeightedRandomBag;
import net.minecraft.core.item.ItemFood;
import net.minecraft.core.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static googy.betterwithenchanting.BetterWithEnchanting.*;

public class EnchantmentContainer {
	public static final String ENCHANTMENT_DATA_KEY = "enchantmentData";
	public static final String ENCHANTMENT_LIST_KEY = "enchantments";
	public static final String ENCHANTMENT_DATA_LIST = "enchantmentDataList";

	private EnchantmentContainer() {
	}

	public static void addEnchantments(@NotNull ItemStack stack, @NotNull List<EnchantmentStack> enchantments) {
		for (EnchantmentStack enchantment : enchantments) {
			if (EnchantmentContainer.contains(stack, enchantment.getEnchantment())) {
				continue;
			}
			EnchantmentContainer.rawAddEnchantment(stack, enchantment);
		}
	}

	public static void addEnchantments(@NotNull CompoundTag tag, @NotNull List<EnchantmentStack> enchantments) {
		for (EnchantmentStack enchantment : enchantments) {
			if (EnchantmentContainer.contains(tag, enchantment.getEnchantment())) {
				continue;
			}
			EnchantmentContainer.rawAddEnchantment(tag, enchantment);
		}
	}

	/// The caller guarantees that the enchantment can be applied to the item.
	/// If such a guarantee can not be provided call EnchantmentContain::addEnchantment instead.
	public static void rawAddEnchantment(@NotNull ItemStack stack, @NotNull EnchantmentStack enchantmentStack) {
		CompoundTag enchantTag = enchantmentStack.writeNBT(new CompoundTag());
		CompoundTag enchantData = stack.getData().getCompound(ENCHANTMENT_DATA_KEY);
		ListTag enchantList = enchantData.getList(ENCHANTMENT_LIST_KEY);
		enchantList.addTag(enchantTag);
		CompoundTag enchantmentsTag = new CompoundTag();
		enchantmentsTag.putList(ENCHANTMENT_LIST_KEY, enchantList);
		stack.getData().putCompound(ENCHANTMENT_DATA_KEY, enchantmentsTag);
	}

	public static void rawAddEnchantment(@NotNull CompoundTag tag, @NotNull EnchantmentStack enchantmentStack) {
		CompoundTag enchantTag = enchantmentStack.writeNBT(new CompoundTag());
		CompoundTag enchantData = tag.getCompound(ENCHANTMENT_DATA_KEY);
		ListTag enchantList = enchantData.getList(ENCHANTMENT_LIST_KEY);
		enchantList.addTag(enchantTag);
		CompoundTag enchantmentsTag = new CompoundTag();
		enchantmentsTag.putList(ENCHANTMENT_LIST_KEY, enchantList);
		tag.putCompound(ENCHANTMENT_DATA_KEY, enchantmentsTag);
	}


	/// The function is responsible for checking if a given enchantment applies to the item or not, before calling the
	/// raw version.
	public static void addEnchantment(@NotNull ItemStack stack, @NotNull EnchantmentStack enchantmentStack) {
		if (EnchantmentContainer.contains(stack, enchantmentStack.getEnchantment()) || !enchantmentStack.canEnchant(stack)) {
			return;
		}
		EnchantmentContainer.rawAddEnchantment(stack, enchantmentStack);
	}

	public static @Nullable EnchantmentStack removeEnchantment(@NotNull ItemStack itemStack, @NotNull Enchantment enchantment) {
		if (!EnchantmentContainer.contains(itemStack, enchantment)) {
			return null;
		}
		CompoundTag enchantData = itemStack.getData().getCompound(ENCHANTMENT_DATA_KEY);
		ListTag enchantList = enchantData.getList(ENCHANTMENT_LIST_KEY);
		Iterator<Tag<?>> it = enchantList.iterator();
		while (it.hasNext()) {
			CompoundTag enchantTag = (CompoundTag) it.next();
			EnchantmentStack enchantmentStack = new EnchantmentStack(enchantTag);
			if (enchantmentStack.getEnchantment().equals(enchantment)) {
				it.remove();
				return enchantmentStack;
			}
		}
		return null;
	}

	public static int removeAllEnchantment(@NotNull ItemStack itemStack) {
		CompoundTag enchantData = itemStack.getData().getCompound(ENCHANTMENT_DATA_KEY);
		int count = enchantData.getList(ENCHANTMENT_LIST_KEY).tagCount();
		enchantData.getValue().remove(ENCHANTMENT_LIST_KEY);
		return count;
	}

	public static void increaseLevel(@NotNull ItemStack itemStack, @NotNull Enchantment enchantment, int level) {
		if (!EnchantmentContainer.contains(itemStack, enchantment)) {
			return;
		}
		CompoundTag enchantData = itemStack.getData().getCompound(ENCHANTMENT_DATA_KEY);
		ListTag enchantList = enchantData.getList(ENCHANTMENT_LIST_KEY);
		for (int i = 0; i < enchantList.tagCount(); i++) {
			CompoundTag enchantTag = (CompoundTag) enchantList.tagAt(i);
			EnchantmentStack enchantmentStack = new EnchantmentStack(enchantTag);
			if (enchantment.equals(enchantmentStack.getEnchantment())) {
				enchantmentStack.setLevel(enchantmentStack.getLevel() + level);
				enchantmentStack.writeNBT(enchantTag);
				return;
			}
		}
	}

	public static void setLevel(@NotNull ItemStack itemStack, @NotNull Enchantment enchantment, int level) {
		if (!EnchantmentContainer.contains(itemStack, enchantment)) {
			return;
		}
		CompoundTag enchantData = itemStack.getData().getCompound(ENCHANTMENT_DATA_KEY);
		ListTag enchantList = enchantData.getList(ENCHANTMENT_LIST_KEY);
		for (int i = 0; i < enchantList.tagCount(); i++) {
			CompoundTag enchantTag = (CompoundTag) enchantList.tagAt(i);
			EnchantmentStack enchantmentStack = new EnchantmentStack(enchantTag);
			if (enchantment.equals(enchantmentStack.getEnchantment())) {
				enchantmentStack.setLevel(level);
				enchantmentStack.writeNBT(enchantTag);
				return;
			}
		}
	}

	public static boolean hasEnchantments(@NotNull ItemStack stack) {
		CompoundTag enchantData = stack.getData().getCompound(ENCHANTMENT_DATA_KEY);
		ListTag enchantList = enchantData.getList(ENCHANTMENT_LIST_KEY);
		return enchantList.tagCount() > 0;
	}

	public static List<EnchantmentStack> getEnchantments(@NotNull ItemStack stack) {
		return getEnchantments(stack.getData());
	}

	public static List<EnchantmentStack> getEnchantments(@NotNull ItemStack stack, int option) {
		ListTag listTag = stack.getData().getList(ENCHANTMENT_DATA_LIST);
		if(listTag.tagCount() <= option){
			return new ArrayList<>();
		}
		return getEnchantments((CompoundTag) listTag.tagAt(option));
	}

	private static List<EnchantmentStack> getEnchantments(@NotNull CompoundTag stackData) {
		List<EnchantmentStack> enchantments = new ArrayList<>();
		CompoundTag enchantData = stackData.getCompound(ENCHANTMENT_DATA_KEY);
		ListTag enchantList = enchantData.getList(ENCHANTMENT_LIST_KEY);
		for (int i = 0; i < enchantList.tagCount(); i++) {
			CompoundTag enchantTag = (CompoundTag) enchantList.tagAt(i);
			enchantments.add(new EnchantmentStack(enchantTag));
		}
		return enchantments;
	}

	public static boolean contains(@NotNull ItemStack stack, @NotNull Enchantment enchantment) {
		CompoundTag enchantData = stack.getData().getCompound(ENCHANTMENT_DATA_KEY);
		ListTag enchantList = enchantData.getList(ENCHANTMENT_LIST_KEY);
		for (int i = 0; i < enchantList.tagCount(); i++) {
			CompoundTag enchantTag = (CompoundTag) enchantList.tagAt(i);
			EnchantmentStack enchantmentStack = new EnchantmentStack(enchantTag);
			if (enchantment.equals(enchantmentStack.getEnchantment())) {
				return true;
			}
		}
		return false;
	}

	public static boolean contains(@NotNull CompoundTag tag, @NotNull Enchantment enchantment) {
		CompoundTag enchantData = tag.getCompound(ENCHANTMENT_DATA_KEY);
		ListTag enchantList = enchantData.getList(ENCHANTMENT_LIST_KEY);
		for (int i = 0; i < enchantList.tagCount(); i++) {
			CompoundTag enchantTag = (CompoundTag) enchantList.tagAt(i);
			EnchantmentStack enchantmentStack = new EnchantmentStack(enchantTag);
			if (enchantment.equals(enchantmentStack.getEnchantment())) {
				return true;
			}
		}
		return false;
	}

	public static @Nullable EnchantmentStack getEnchantmentStack(@NotNull ItemStack stack, @NotNull Enchantment enchantment) {
		CompoundTag enchantData = stack.getData().getCompound(ENCHANTMENT_DATA_KEY);
		ListTag enchantList = enchantData.getList(ENCHANTMENT_LIST_KEY);
		for (int i = 0; i < enchantList.tagCount(); i++) {
			CompoundTag enchantTag = (CompoundTag) enchantList.tagAt(i);
			EnchantmentStack enchantmentStack = new EnchantmentStack(enchantTag);
			if (enchantment.equals(enchantmentStack.getEnchantment())) {
				return enchantmentStack;
			}
		}
		return null;
	}

	public static int getLevel(@Nullable ItemStack stack, @NotNull Enchantment enchantment) {
		if (stack == null || stack.stackSize <= 0 && !(stack.getItem() instanceof ItemFood)) {
			return 0;
		}
		EnchantmentStack data = EnchantmentContainer.getEnchantmentStack(stack, enchantment);
		return data == null ? 0 : Math.max(data.getLevel(), 0);
	}

	private static WeightedRandomBag<EnchantmentStack> getPossible(@NotNull ItemStack stack, int enchantability) {
		WeightedRandomBag<EnchantmentStack> bag = new WeightedRandomBag<>();
		for (Enchantment enchantment : Enchantments.getInstance()) {
			if (enchantment == null || !enchantment.canEnchant(stack) || enchantment.hidden()) {
				continue;
			}
			for (int level = enchantment.minLevel(); level <= enchantment.maxLevel(); level++) {
				if (enchantability >= enchantment.getMinEnchantability(level) && enchantability <= enchantment.getMaxEnchantability(level)) {
					bag.addEntry(new EnchantmentStack(enchantment, level), enchantment.getWeight(level));
				}
			}
		}
		return bag;
	}

	public static boolean hasApplicable(@NotNull ItemStack stack) {
		for (Enchantment enchantment : Enchantments.getInstance()) {
			if (enchantment != null && enchantment.canEnchant(stack) && !enchantment.hidden()) {
				return true;
			}
		}
		return false;
	}

	public static List<EnchantmentStack> generateEnchantmentsList(@NotNull Random random, @NotNull ItemStack itemStack, int cost) {
		int enchantability = EnchantmentContainer.calcEnchantability(random, cost);
		List<EnchantmentStack> enchantmentResults = new ArrayList<>();
		WeightedRandomBag<EnchantmentStack> enchantmentPool = EnchantmentContainer.getPossible(itemStack, enchantability);
		if (enchantmentPool.getEntries().isEmpty()) {
			return enchantmentResults;
		}

		// guaranteed
		Set<EnchantmentStack> result = new HashSet<>();
		EnchantmentStack addStack = enchantmentPool.getRandom(random);
		result.add(addStack);
		int maxEnchantmentsCycles = 5;
		int current = enchantability;
		while (maxEnchantmentsCycles-- > 0 && random.nextInt(50) <= current) {
			addStack = enchantmentPool.getRandom(random);
			if (EnchantmentContainer.adjustsLevel(result, addStack)) {
				continue;
			}
			result.add(addStack);
			current = (int) Math.ceil(current / 1.2f);
		}
		return new ArrayList<>(result);
	}

	public static boolean adjustsLevel(Set<EnchantmentStack> optionList, EnchantmentStack addStack) {
		for (EnchantmentStack appliedStack : optionList) {
			if (appliedStack.getEnchantment().equals(addStack.getEnchantment())) {
				int level = Math.min(appliedStack.maxLevel(), Math.max(addStack.getLevel(), appliedStack.getLevel()) + 1);
				appliedStack.setLevel(level);
				return true;
			}
		}
		return false;
	}

	private static int calcEnchantability(@NotNull Random random, double cost) {
		// Normalize cost into percentage
		double costPercentage = cost / MAX_ENCHANTMENT_COST;

		// Compute base enchantability
		int randEnchantability = 1 + random.nextInt(DEFAULT_ITEM_ENCHANTABILITY / 4 + 1) + random.nextInt(DEFAULT_ITEM_ENCHANTABILITY / 4 + 1);
		int k = (int) (costPercentage * (30 - 1) + 1) + randEnchantability;

		// Apply random bonus (±15%)
		float randBonusPercent = 1 + (random.nextFloat() + random.nextFloat() - 1) * 0.15f;
		return Math.max(1, Math.round(k * randBonusPercent));
	}

	public static int calcDeterministicEnchantability(double cost, boolean bestCase) {
		// Normalize cost into percentage
		double costPercentage = cost / MAX_ENCHANTMENT_COST;

		// Compute base enchantability
		int extra =  bestCase ? DEFAULT_ITEM_ENCHANTABILITY / 4 + 1 : 0;
		int detEnchantability = 1 + extra + extra;
		int k = (int) (costPercentage * (30 - 1) + 1) + detEnchantability;

		// Apply random bonus (±15%)
		float bonusPercent = bestCase ? 1.0f : -1.0f;
		float randBonusPercent = 1 + bonusPercent * 0.15f;
		return Math.max(1, Math.round(k * randBonusPercent));
	}

	public static int calcCostFromEnchantability(double enchatability, boolean isMax){
		return isMax ? calcMaxCostFromEnchantability(enchatability) : calcMinCostFromEnchantability(enchatability);
	}

	public static int calcMinCostFromEnchantability(double enchantability) {
		double minCost = ((enchantability / 1.15F) - 1.0F) * MAX_ENCHANTMENT_COST;
		return (int)Math.floor(minCost / 29.0F);
	}

	public static int calcMaxCostFromEnchantability(double enchantability) {
		double maxCost = ((enchantability / 0.85F) - DEFAULT_ITEM_ENCHANTABILITY / 2.0F - 4.0f) * MAX_ENCHANTMENT_COST;
		return (int)Math.floor(maxCost / 29.0F);
	}

	public static String prettyPrint(@Nullable ItemStack itemStack) {
		if (itemStack == null) {
			return "";
		}
		StringBuilder resultString = new StringBuilder();
		CompoundTag enchantData = itemStack.getData().getCompound(ENCHANTMENT_DATA_KEY);
		ListTag enchantList = enchantData.getList(ENCHANTMENT_LIST_KEY);
		for (int i = 0; i < enchantList.tagCount(); i++) {
			CompoundTag enchantTag = (CompoundTag) enchantList.tagAt(i);
			EnchantmentStack enchantmentStack = new EnchantmentStack(enchantTag);
			resultString.append(enchantmentStack.prettyToString());
			if (i + 1 < enchantList.tagCount()) {
				resultString.append(",");
			}
		}
		return resultString.toString();
	}
}
