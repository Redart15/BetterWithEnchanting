package googy.betterwithenchanting.api;

import com.mojang.nbt.tags.CompoundTag;
import com.mojang.nbt.tags.ListTag;
import com.mojang.nbt.tags.Tag;
import net.minecraft.core.WeightedRandomBag;
import net.minecraft.core.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static googy.betterwithenchanting.BetterWithEnchanting.*;

public class EnchantmentContainer {
	public static final String ENCHANTMENT_DATA_KEY = "enchantmentData";
	public static final String ENCHANTMENT_LIST_KEY = "enchantments";

	private EnchantmentContainer() {
	}

	public static int calcEnchantCost(int enchantOption, int bookshelfs) {
		double percentage = (bookshelfs + START_COST_OFFSET) / (15.0 + START_COST_OFFSET);
		percentage *= (enchantOption + 1) / 3.0;
		return (int) Math.ceil(MAX_ENCHANTMENT_COST * percentage);
	}

	public static void addEnchantments(ItemStack stack, List<EnchantmentStack> enchantments) {
		for (EnchantmentStack enchantment : enchantments) {
			addEnchantment(stack, enchantment);
		}
	}

	public static void addEnchantment(ItemStack stack, EnchantmentStack enchantmentStack) {
		if (contains(stack, enchantmentStack.getEnchantment())) {
			return;
		}
		CompoundTag enchantTag = enchantmentStack.writeNBT(new CompoundTag());
		CompoundTag enchantData = stack.getData().getCompound(ENCHANTMENT_DATA_KEY);
		ListTag enchantList =  enchantData.getList(ENCHANTMENT_LIST_KEY);
		enchantList.addTag(enchantTag);
		CompoundTag enchantmentsTag = new CompoundTag();
		enchantmentsTag.putList(ENCHANTMENT_LIST_KEY, enchantList);
		stack.getData().putCompound(ENCHANTMENT_DATA_KEY, enchantmentsTag);
	}

	public static EnchantmentStack removeEnchantment(ItemStack itemStack, Enchantment enchantment) {
		if (!contains(itemStack, enchantment)) {
			return null;
		}
		CompoundTag enchantData = itemStack.getData().getCompound(ENCHANTMENT_DATA_KEY);
		ListTag enchantList =  enchantData.getList(ENCHANTMENT_LIST_KEY);
		Iterator<Tag<?>> it = enchantList.iterator();
		while(it.hasNext()){
			CompoundTag enchantTag = (CompoundTag) it.next();
			EnchantmentStack enchantmentStack = new EnchantmentStack(enchantTag);
			if(enchantmentStack.getEnchantment().equals(enchantment)){
				it.remove();
				return enchantmentStack;
			}
		}
		return null;
	}

	public static int removeAllEnchantment(ItemStack itemStack) {
		CompoundTag enchantData = itemStack.getData().getCompound(ENCHANTMENT_DATA_KEY);
		int count =  enchantData.getList(ENCHANTMENT_LIST_KEY).tagCount();
		enchantData.getValue().remove(ENCHANTMENT_LIST_KEY);
		return count;
	}

	public static void increaseLevel(ItemStack itemStack, Enchantment enchantment, int level) {
		if (!contains(itemStack, enchantment)) {
			return;
		}
		CompoundTag enchantData = itemStack.getData().getCompound(ENCHANTMENT_DATA_KEY);
		ListTag enchantList =  enchantData.getList(ENCHANTMENT_LIST_KEY);
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

	public static boolean hasEnchantments(ItemStack stack){
		if (stack == null) {
			return false;
		}
		CompoundTag enchantData = stack.getData().getCompound(ENCHANTMENT_DATA_KEY);
		ListTag enchantList =  enchantData.getList(ENCHANTMENT_LIST_KEY);
		return enchantList.tagCount() > 0;
	}

	public static List<EnchantmentStack> getEnchantments(ItemStack stack) {
		List<EnchantmentStack> enchantments = new ArrayList<>();
		if (stack == null) {
			return enchantments;
		}
		CompoundTag enchantData = stack.getData().getCompound(ENCHANTMENT_DATA_KEY);
		ListTag enchantList =  enchantData.getList(ENCHANTMENT_LIST_KEY);
		for (int i = 0; i < enchantList.tagCount(); i++) {
			CompoundTag enchantTag = (CompoundTag) enchantList.tagAt(i);
			enchantments.add(new EnchantmentStack(enchantTag));
		}
		return enchantments;
	}

	public static boolean contains(ItemStack stack, Enchantment enchantment) {
		CompoundTag enchantData = stack.getData().getCompound(ENCHANTMENT_DATA_KEY);
		ListTag enchantList =  enchantData.getList(ENCHANTMENT_LIST_KEY);
		for (int i = 0; i < enchantList.tagCount(); i++) {
			CompoundTag enchantTag = (CompoundTag) enchantList.tagAt(i);
			EnchantmentStack enchantmentStack = new EnchantmentStack(enchantTag);
			if (enchantment.equals(enchantmentStack.getEnchantment())) {
				return true;
			}
		}
		return false;
	}

	public static @Nullable EnchantmentStack getEnchantmentStack(@NotNull ItemStack stack, Enchantment enchantment) {
		CompoundTag enchantData = stack.getData().getCompound(ENCHANTMENT_DATA_KEY);
		ListTag enchantList =  enchantData.getList(ENCHANTMENT_LIST_KEY);
		for (int i = 0; i < enchantList.tagCount(); i++) {
			CompoundTag enchantTag = (CompoundTag) enchantList.tagAt(i);
			EnchantmentStack enchantmentStack = new EnchantmentStack(enchantTag);
			if (enchantment.equals(enchantmentStack.getEnchantment())) {
				return enchantmentStack;
			}
		}
		return null;
	}

	public static int getLevel(ItemStack stack, Enchantment enchantment) {
		if (stack == null || stack.stackSize <= 0) {
			return 0;
		}
		EnchantmentStack data = getEnchantmentStack(stack, enchantment);
		return data == null ? 0 : Math.max(data.getLevel(), 0);
	}

	public static WeightedRandomBag<EnchantmentStack> getPossible(ItemStack stack, int enchantability) {
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

	public static boolean hasApplicable(ItemStack stack) {
		for (Enchantment enchantment : Enchantments.getInstance()) {
			if (enchantment != null && enchantment.canEnchant(stack) && !enchantment.hidden()) {
				return true;
			}
		}
		return false;
	}

	public static List<EnchantmentStack> generateEnchantmentsList(Random random, ItemStack itemStack, int cost) {
		int enchantability = calcEnchantability(random, cost);
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
		fillEnchantmentList(random, maxEnchantmentsCycles, current, enchantmentPool, result);
		return new ArrayList<>(result);
	}

	private static void fillEnchantmentList(Random random, int maxEnchantmentsCycles, int current, WeightedRandomBag<EnchantmentStack> enchantmentPool, Set<EnchantmentStack> result) {
		EnchantmentStack addStack;
		while (maxEnchantmentsCycles-- > 0
			&& random.nextInt(50) <= current
		) {
			addStack = enchantmentPool.getRandom(random);
			boolean decrement = true;
			boolean add = true;
			for(EnchantmentStack stack: result){
				if(stack.getEnchantment().equals(addStack.getEnchantment())){
					add = false;
					int level = addStack.getLevel() + stack.getLevel();
					if(level > stack.maxLevel()){
						level = stack.maxLevel();
						decrement = false;
					}
					stack.setLevel(level);
					break;
				}
			}
			if(add){
				result.add(addStack);
			}
			if(decrement){
				/// the original made it way harder to get multiple enchantments, 1.2 softens it up quite a bit
//				current >>= 1;
				current = (int)Math.floor(current / 1.2f);
			}
		}
	}

	private static int calcEnchantability(Random random, double cost) {
		// Normalize cost into percentage
		double costPercentage = cost / MAX_ENCHANTMENT_COST;

		// Compute base enchantability
		int randEnchantability = 1 + random.nextInt(DEFAULT_ITEM_ENCHANTABILITY / 4 + 1) + random.nextInt(DEFAULT_ITEM_ENCHANTABILITY / 4 + 1);
		int k = (int) (costPercentage * (30 - 1) + 1) + randEnchantability;

		// Apply random bonus (±15%)
		float randBonusPercent = 1 + (random.nextFloat() + random.nextFloat() - 1) * 0.15f;
		return Math.max(1, Math.round(k * randBonusPercent));
	}
}
