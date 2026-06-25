package googy.betterwithenchanting.mixins;

import com.mojang.nbt.tags.CompoundTag;
import googy.betterwithenchanting.BetterWithEnchanting;
import googy.betterwithenchanting.api.EnchantmentContainer;
import googy.betterwithenchanting.api.EnchantmentStack;
import googy.betterwithenchanting.api.Enchantments;
import googy.betterwithenchanting.mixins.mixin.accessor.ConsumedFoodAccessor;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.WeightedRandomBag;
import net.minecraft.core.WeightedRandomLootObject;
import net.minecraft.core.data.registry.Registries;
import net.minecraft.core.data.registry.recipe.entry.RecipeEntryBlastFurnace;
import net.minecraft.core.data.registry.recipe.entry.RecipeEntryFurnace;
import net.minecraft.core.data.registry.recipe.entry.RecipeEntryTrommel;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.enums.EnumDropCause;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.Items;
import net.minecraft.core.lang.I18n;
import net.minecraft.core.net.command.TextFormatting;
import net.minecraft.core.world.World;
import net.minecraft.core.world.pos.TilePosc;

import java.util.*;
import java.util.function.UnaryOperator;

public class MixinsHelperLogic {
	protected static WeightedRandomBag<WeightedRandomLootObject> fortuneBag = new WeightedRandomBag<>();

	static {
		/// fortune bag and its filling
		// trash
		fortuneBag.addEntry(new WeightedRandomLootObject(Items.QUARTZ.getDefaultStack(), 1, 2), 128);
		fortuneBag.addEntry(new WeightedRandomLootObject(Items.OLIVINE.getDefaultStack()), 96);
		// shiny (worth finding)
		fortuneBag.addEntry(new WeightedRandomLootObject(Items.DUST_REDSTONE.getDefaultStack(), 2, 3), 64);
		// rare
		fortuneBag.addEntry(new WeightedRandomLootObject(Items.ORE_RAW_GOLD.getDefaultStack(), 1, 3), 32);
		fortuneBag.addEntry(new WeightedRandomLootObject(Items.ORE_RAW_IRON.getDefaultStack(), 1, 3), 32);
		// ultrarare (jackpot)
		fortuneBag.addEntry(new WeightedRandomLootObject(Items.DIAMOND.getDefaultStack()), 16);
		// bizzare
		fortuneBag.addEntry(new WeightedRandomLootObject(Items.DUST_GLOWSTONE.getDefaultStack()), 8);
	}

	private MixinsHelperLogic() {
	}

	public static void devLog(String message) {
		if (FabricLoader.getInstance().isDevelopmentEnvironment()) {
			BetterWithEnchanting.LOG.info(message);
		}
	}

	public static void getEnchantmentText(ItemStack itemStack, StringBuilder toolTip) {
		List<EnchantmentStack> enchantmentsData = EnchantmentContainer.getEnchantments(itemStack);
		enchantmentsData.sort(Comparator.comparing(e -> e.getEnchantment().id()));
		for (EnchantmentStack enchantmentStack : enchantmentsData) {
			boolean isNull = enchantmentStack.getEnchantment() == null;
			boolean noLevel = isNull || enchantmentStack.minLevel() == enchantmentStack.maxLevel();
			String key = isNull ? "disabled" : enchantmentStack.getTranslationKey() + ".name";
			String enchantLevel = noLevel ? "" : String.valueOf(enchantmentStack.getLevel());
			String enchantName = TextFormatting.formatted(I18n.getInstance().translateKey(key), TextFormatting.CYAN);
			enchantLevel = TextFormatting.formatted(enchantLevel, TextFormatting.CYAN);
			toolTip.append(enchantName).append(" ").append(enchantLevel).append("\n");
		}
	}

	public static double log(double value, double base) {
		return Math.log(value) / Math.log(base);
	}

	protected static Random random = new Random();

	public static void applyDiscovery(World world, TilePosc tilePosc, ItemStack stack) {
		int level = EnchantmentContainer.getLevel(stack, Enchantments.CATALYST);
		if (level <= 0 || random.nextInt(128) > 1) {
			return;
		}
		for (int i = level; i > 0; i--) {
			world.dropItem(tilePosc, new ItemStack(Items.DYE, 1, 4));
		}
	}

	public static ItemStack applyDiscovery(Player player) {
		if(player.getHeldItem() == null){
			return null;
		}
		ItemStack held = player.getHeldItem();
		int level = EnchantmentContainer.getLevel(held, Enchantments.CATALYST);
		if (level <= 0 || random.nextInt(128) > 1) {
			return null;
		}
		return new ItemStack(Items.DYE, level, 4);
	}

	public static void applyFortune(World world, TilePosc tilePosc, ItemStack stack) {
		int level = EnchantmentContainer.getLevel(stack, Enchantments.FORTUNE);
		if (level <= 0 || random.nextInt(128) >= (1 << (level - 1))) {
			return;
		}
		world.dropItem(tilePosc, fortuneBag.getRandom(random).getItemStack(random));
	}

	public static ItemStack applyFortune(Player player) {
		if(player.getHeldItem() == null){
			return null;
		}
		ItemStack held = player.getHeldItem();
		int level = EnchantmentContainer.getLevel(held, Enchantments.FORTUNE);
		if(level <= 0){
			return null;
		}
		return fortuneBag.getRandom(random).getItemStack(random);
	}

	public static void applyInsight(Player player, ItemStack stack, int defaultScore) {
		int level = EnchantmentContainer.getLevel(stack, Enchantments.INSIGHT);
		if (level <= 0) {
			player.score += defaultScore;// to give more excess to xp
		} else {
			player.score += (int) Math.floor((defaultScore + 7) * Math.pow(level, 0.85));
		}
	}

	public static void applyInsight(Player player, int defaultScore) {
		if(player.getHeldItem() == null){
			return;
		}
		ItemStack held = player.getHeldItem();
		int level = EnchantmentContainer.getLevel(held, Enchantments.INSIGHT);
		if (level <= 0) {
			player.score += defaultScore;// to give more excess to xp
		} else {
			player.score += (int) Math.floor((defaultScore + 7) * Math.pow(level, 0.85));
		}
	}

	public static ItemStack[] applyMoltenAndScevange(EnumDropCause dropCause, Player player, ItemStack[] drops) {
		if (player == null) {
			return drops;
		}
		ItemStack heldItem = player.getHeldItem();
		int molten = EnchantmentContainer.getLevel(heldItem, Enchantments.SEARING);
		int scavenge = EnchantmentContainer.getLevel(heldItem, Enchantments.SCAVENGE);
		if (dropCause == EnumDropCause.PROPER_TOOL && (molten > 0 || scavenge > 0)) {
			List<ItemStack> results = new ArrayList<>();
			if (molten > 0) {
				results.addAll(Arrays.asList(processItem(player, drops, MixinsHelperLogic::matchSmeltingRecipes)));
			}
			if (scavenge > 0 && random.nextBoolean()) {
				results.addAll(Arrays.asList(processItem(player, drops, MixinsHelperLogic::matchTrommelRecipes)));
			}
			return results.toArray(new ItemStack[]{});
		}
		return drops;
	}

	public static ItemStack[] processItem(Player player, ItemStack[] drops, UnaryOperator<ItemStack> processor) {
		ItemStack heldItem = player.getHeldItem();
		if (drops == null) {
			return new ItemStack[0];
		}
		if (heldItem == null || drops.length == 0) {
			return drops;
		}
		int durabilityDamage = 0;
		int durabilityLeft = heldItem.getMetadata();
		List<ItemStack> results = new ArrayList<>();
		for (ItemStack currentDrop : drops) {
			if (durabilityLeft > durabilityDamage) {
				ItemStack result = processor.apply(currentDrop);
				if (result.itemID != currentDrop.itemID) {
					durabilityDamage += result.stackSize;
				}
				results.add(result);
			} else {
				results.add(currentDrop);
			}
		}
		return results.toArray(new ItemStack[0]);
	}

	private static ItemStack matchSmeltingRecipes(ItemStack currentDrop) {
		for (RecipeEntryBlastFurnace recipeEntryBase : Registries.RECIPES.getAllBlastFurnaceRecipes()) {
			if (recipeEntryBase != null && recipeEntryBase.matches(currentDrop, null)) {
				return recipeEntryBase.getOutput().copy();
			}
		}
		for (RecipeEntryFurnace recipeEntryBase : Registries.RECIPES.getAllFurnaceRecipes()) {
			if (recipeEntryBase != null && recipeEntryBase.matches(currentDrop)) {
				return recipeEntryBase.getOutput().copy();
			}
		}
		return currentDrop;
	}

	private static ItemStack matchTrommelRecipes(ItemStack currentDrop) {
		for (RecipeEntryTrommel recipe : Registries.RECIPES.getAllTrommelRecipes()) {
			if (recipe.getInput().matches(currentDrop)) {
				return ((recipe.getOutput()).getRandom(random)).getItemStack();
			}
		}
		return currentDrop;
	}

	private static final String COUNT_EAT = "timesEaten";

	private static byte getCount(CompoundTag tag) {
		if (tag.containsKey(COUNT_EAT)) {
			return tag.getByte(COUNT_EAT);
		} else {
			tag.putByte(COUNT_EAT, (byte) 0);
			return 0;
		}
	}

	private static void increaseCount(CompoundTag tag, byte eatCount) {
		tag.putByte(COUNT_EAT, (byte) (eatCount + 1));
	}

	public static boolean applyLasting(ItemStack stack) {
		int lastingLvL = EnchantmentContainer.getLevel(stack, Enchantments.LASTING);
		if (lastingLvL <= 0) {
			return false;
		}
		CompoundTag tag = stack.getData();
		byte eatCount = getCount(tag);
		int value = random.nextInt(5 + lastingLvL);
		if (value > eatCount) {
			increaseCount(tag, eatCount);
			return true;
		}
		return false;
	}

	public static void applyScore(Player player, ItemStack itemStack) {
		int level = EnchantmentContainer.getLevel(itemStack, Enchantments.BOTTLED_SCORE);
		if (level >= 0) {
			player.score += level * 4000;
			EnchantmentContainer.removeEnchantment(itemStack, Enchantments.BOTTLED_SCORE);
		}
	}

	public static int calcAdditionalHealing(ConsumedFoodAccessor asThis) {
		ItemStack stack = asThis.getStack();
		int healing = asThis.getFoodItem().getHealAmount(stack);
		int lvl = EnchantmentContainer.getLevel(stack, Enchantments.FILLING);
		return getAdditionalHealing(lvl, healing);
	}

	public static int getAdditionalHealing(int lvl, int healing) {
		if (lvl <= 0 || healing <= 0) {
			return 0;
		}
		if (healing < 4) {
			return lvl;
		}
		return (int) Math.floor(healing * 0.4f * lvl);
	}

	public static int calcAdditionalDuration(ConsumedFoodAccessor asThis) {
		ItemStack stack = asThis.getStack();
		int duration = asThis.getFoodItem().getHealAmount(stack);
		int lvl = EnchantmentContainer.getLevel(stack, Enchantments.NOURISHMENT);
		if (lvl <= 0 || duration <= 0) {
			return 0;
		}
		if (duration == 1 || duration == 2) {
			return 1;
		}
		if (duration == 3) {
			return lvl == 1 ? 1 : 2;
		}
		if (duration == 4) {
			return lvl;
		}
		return (int) Math.floor(duration * 0.2f * lvl);
	}

}
