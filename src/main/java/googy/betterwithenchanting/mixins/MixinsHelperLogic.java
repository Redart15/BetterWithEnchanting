package googy.betterwithenchanting.mixins;

import com.mojang.nbt.tags.CompoundTag;
import googy.betterwithenchanting.BetterWithEnchanting;
import googy.betterwithenchanting.api.EnchantmentContainer;
import googy.betterwithenchanting.api.EnchantmentStack;
import googy.betterwithenchanting.api.Enchantments;
import googy.betterwithenchanting.block.EnchantmentBlocks;
import googy.betterwithenchanting.mixins.interfaces.EnchantmentCannonball;
import googy.betterwithenchanting.mixins.mixin.accessor.BlockLogicLeavesBaseAccessor;
import googy.betterwithenchanting.mixins.mixin.accessor.ConsumedFoodAccessor;
import googy.betterwithenchanting.mixins.mixin.accessor.ProjectileAccessor;
import googy.betterwithenchanting.util.PlayerUtil;
import it.unimi.dsi.fastutil.objects.ObjectIntImmutablePair;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.WeightedRandomBag;
import net.minecraft.core.WeightedRandomLootObject;
import net.minecraft.core.block.*;
import net.minecraft.core.block.tag.BlockTags;
import net.minecraft.core.data.registry.Registries;
import net.minecraft.core.data.registry.recipe.entry.RecipeEntryBlastFurnace;
import net.minecraft.core.data.registry.recipe.entry.RecipeEntryFurnace;
import net.minecraft.core.data.registry.recipe.entry.RecipeEntryTrommel;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.Mob;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.entity.projectile.ProjectileCannonball;
import net.minecraft.core.enums.EnumDropCause;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.Items;
import net.minecraft.core.lang.I18n;
import net.minecraft.core.net.command.TextFormatting;
import net.minecraft.core.util.helper.Direction;
import net.minecraft.core.util.helper.DyeColor;
import net.minecraft.core.world.World;
import net.minecraft.core.world.chunk.ChunkPosition;
import net.minecraft.core.world.pos.TilePos;
import net.minecraft.core.world.pos.TilePosc;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;

import java.util.*;
import java.util.function.UnaryOperator;

public class MixinsHelperLogic {
	private MixinsHelperLogic() {}

	protected static WeightedRandomBag<WeightedRandomLootObject> fortuneBag = new WeightedRandomBag<>();
	protected static WeightedRandomBag<ObjectIntImmutablePair<Block<?>>> blockdata = new WeightedRandomBag<>();

	static {
		// fortune bag and its filling
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

	static {
		int rarity = 20;
		for(DyeColor color : DyeColor.values()){
			blockdata.addEntry(new ObjectIntImmutablePair<>(Blocks.BOOKSHELF_PLANKS_OAK, color.itemMeta), 1);
			for(int i = 0; i < 4; i++){
				int metadata = (color.blockMeta << 2 + i) & 0b0111_1111;
				double weight = 12f/(25.0f * rarity *(i + 1));
				blockdata.addEntry(new ObjectIntImmutablePair<>(EnchantmentBlocks.ENCHANTED_BOOKSHELF_ACTIVE, metadata), weight);
			}
		}
	}

	public static ObjectIntImmutablePair<Block<?>> getRandomBlockData(Random random){
		return blockdata.getRandom(random);
	}

	public static void devLog(String message) {
		if (FabricLoader.getInstance().isDevelopmentEnvironment()) {
			BetterWithEnchanting.LOG.info(message);
		}
	}

	public static void getEnchantmentText(ItemStack itemStack, StringBuilder toolTip) {
		List<EnchantmentStack> enchantmentsData = EnchantmentContainer.getEnchantments(itemStack);
		enchantmentsData.removeIf(e -> e == null || e.getEnchantment() == null);
		enchantmentsData.sort(Comparator.comparing(e -> e.getEnchantment().id()));
		for (EnchantmentStack enchantmentStack : enchantmentsData) {
			boolean isNull = enchantmentStack.getEnchantment() == null;
			boolean noLevel = isNull || enchantmentStack.minLevel() == enchantmentStack.maxLevel();
			String key = isNull ? "disabled.name" : enchantmentStack.getTranslationKey() + ".name";
			String enchantLevel = noLevel ? "" : String.valueOf(enchantmentStack.getLevel());
			String enchantName = TextFormatting.formatted(I18n.getInstance().translateKey(key), TextFormatting.CYAN);
			enchantLevel = TextFormatting.formatted(enchantLevel, TextFormatting.CYAN);
			toolTip.append(enchantName).append(" ").append(enchantLevel).append("\n");
		}
	}

	public static String getEnchantmentText(@NotNull ItemStack selfStack, int option) {
		StringBuilder builder = new StringBuilder();
		List<EnchantmentStack> enchantmentsData = EnchantmentContainer.getEnchantments(selfStack, option);
		enchantmentsData.removeIf(e -> e == null || e.getEnchantment() == null);
		enchantmentsData.sort(Comparator.comparing(e -> e.getEnchantment().id()));
		for (int i = 0; i < enchantmentsData.size(); i++) {
			EnchantmentStack enchantmentStack = enchantmentsData.get(i);
			boolean hasLevels = enchantmentStack.minLevel() == enchantmentStack.maxLevel();
			String name = I18n.getInstance().translateKey(enchantmentStack.getEnchantment().translationKeyName());
			String lvl = hasLevels ? "" : String.valueOf(enchantmentStack.getLevel());
			builder.append(name).append(" ").append(lvl);
			if(i + 1 < enchantmentsData.size()){
				builder.append(",\n");
			}
		}
		return builder.toString();
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
		if (player.getHeldItem() == null) {
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
		if (player.getHeldItem() == null) {
			return null;
		}
		ItemStack held = player.getHeldItem();
		int level = EnchantmentContainer.getLevel(held, Enchantments.FORTUNE);
		if (level <= 0) {
			return null;
		}
		return fortuneBag.getRandom(random).getItemStack(random);
	}

	public static void applyInsight(Player player, ItemStack stack, int defaultScore) {
		int level = EnchantmentContainer.getLevel(stack, Enchantments.INSIGHT);
		if (level <= 0) {
			PlayerUtil.addScore(player, defaultScore); // to give more excess to xp
		} else {
			PlayerUtil.addScore(player, (int) Math.floor((defaultScore + 7) * Math.pow(level, 0.85)));
		}
	}

	public static void applyInsight(Player player, int defaultScore) {
		if (player.getHeldItem() == null) {
			return;
		}
		ItemStack held = player.getHeldItem();
		int level = EnchantmentContainer.getLevel(held, Enchantments.INSIGHT);
		if (level <= 0) {
			PlayerUtil.addScore(player, defaultScore); // to give more excess to xp
		} else {
			PlayerUtil.addScore(player, (int) Math.floor((defaultScore + 7) * Math.pow(level, 0.85)));
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

	@Deprecated(since = "1.2.0", forRemoval = true)
	public static void applyScore(Player player, ItemStack itemStack) {
		int level = EnchantmentContainer.getLevel(itemStack, Enchantments.BOTTLED_SCORE);
		if (level >= 0) {
			PlayerUtil.addScore(player, level * 400);
		}
	}

	public static void applyInsight(Mob mob, ItemStack itemStack) {
		if(mob instanceof Player player) {
			int level = EnchantmentContainer.getLevel(itemStack, Enchantments.INSIGHT);
			if (level >= 0) {
				PlayerUtil.addScore(player, level * 400);
			}
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


	public static void applyForaging(World world, TilePosc tilePos, ItemStack stack, BlockLogic logic) {
		int level = EnchantmentContainer.getLevel(stack, Enchantments.FORAGING);
		if (level <= 0 && !(logic instanceof BlockLogicLeavesBase)) {
			return;
		}
		ItemStack additionalDrops = null;
		if (logic instanceof BlockLogicLeavesCherryFlowering) {
			additionalDrops = new ItemStack(Items.FOOD_CHERRY, 1);
		}
		if (logic instanceof BlockLogicLeavesCacao) {
			additionalDrops = new ItemStack(Items.DYE, 1, DyeColor.BROWN.itemMeta);
		}
		ItemStack sapling = ((BlockLogicLeavesBaseAccessor) logic).callGetSapling().asItem().getDefaultStack();
		ItemStack sticks = new ItemStack(Items.STICK, 1);
		ItemStack leaves = new ItemStack(logic.asItem(), 1);
		for (int i = 0; i < level; i++) {
			dropItems(world, tilePos, additionalDrops, sapling, leaves, sticks);
		}
	}

	private static void dropItems(World world, TilePosc tilePos, ItemStack additionalDrops, ItemStack sapling, ItemStack leaves, ItemStack sticks) {
		int id = random.nextInt(30);
		if (id > 25 && additionalDrops != null) {
			world.dropItem(tilePos, additionalDrops);
			return;
		}
		if (id > 15) {
			world.dropItem(tilePos, sapling);
			return;
		}
		if (id > 10) {
			world.dropItem(tilePos, leaves);
			return;
		}
		world.dropItem(tilePos, sticks);
	}

	public static void setExplosive(ProjectileCannonball cannonball, ItemStack itemStack) {
		int level = EnchantmentContainer.getLevel(itemStack, Enchantments.EXPLOSIVE);
		if (level > 0 && cannonball instanceof EnchantmentCannonball iEnchantment) {
			iEnchantment.enchanting$writeExplosive((byte) level);
		}
	}

	public static void setIncendiary(ProjectileCannonball cannonball, ItemStack itemStack) {
		boolean hasEnchantment = EnchantmentContainer.contains(itemStack, Enchantments.INCENDIARY);
		if (hasEnchantment && cannonball instanceof EnchantmentCannonball iEnchantment) {
			iEnchantment.enchanting$writeIncendiary();
		}
	}

	public static void setVolatile(ProjectileCannonball cannonball, ItemStack itemStack) {
		boolean hasEnchantment = EnchantmentContainer.contains(itemStack, Enchantments.VOLATILE);
		if (hasEnchantment && cannonball instanceof EnchantmentCannonball iEnchantment) {
			iEnchantment.enchanting$writeVolatile();
		}
	}

	public static void setPrecise(ProjectileCannonball cannonball, ItemStack itemStack) {
		int level = EnchantmentContainer.getLevel(itemStack, Enchantments.PRECISE);
		if (level > 0 && cannonball instanceof EnchantmentCannonball iEnchantment) {
			iEnchantment.enchanting$writeprecise();
			ProjectileAccessor projectile = (ProjectileAccessor) cannonball;
			float speed = projectile.getDefaultProjectileSpeed() + (0.99f - projectile.getDefaultProjectileSpeed()) * ((float) level / Enchantments.PRECISE.maxLevel());
			projectile.setDefaultProjectileSpeed(speed);
		}
	}

	public static void setPower(ProjectileCannonball cannonball, ItemStack itemStack) {
		int level = EnchantmentContainer.getLevel(itemStack, Enchantments.POWER);
		if (level > 0) {
			cannonball.xd = cannonball.xd * (1.0f + level * 0.3);
			cannonball.yd = cannonball.yd * (1.0f + level * 0.3);
			cannonball.zd = cannonball.zd * (1.0f + level * 0.3);
		}
	}

	public static void createFire(World world, TilePosc tilePosc, float explosionSize) {
		MixinsHelperLogic.createFireIntern(world, tilePosc.x(), tilePosc.y(), tilePosc.z(), explosionSize);
	}

	public static void createFire(World world, double cx, double cy, double cz, float explosionSize) {
		MixinsHelperLogic.createFireIntern(world, (int) Math.round(cx), (int) Math.round(cy), (int) Math.round(cz), explosionSize);
	}

	private static void createFireIntern(World world, int cx, int cy, int cz, float explosionSize) {
		class Cache { private static final @NotNull TilePos pos = new TilePos();}
		int size = (int) Math.ceil(explosionSize * (0.7F + random.nextFloat() * 0.6F));
		int r = size * size;
		TilePos tilePos = null;
		Set<ChunkPosition> visited = new HashSet<>();
		Deque<ChunkPosition> queue = new ArrayDeque<>();
		queue.add(new ChunkPosition(cx, cy, cz));
		while (!queue.isEmpty()) {
			ChunkPosition next = queue.pop();
			for (Direction direction : Direction.ID_MAP) {
				int nx = next.x + direction.offsetX();
				int ny = next.y + direction.offsetY();
				int nz = next.z + direction.offsetZ();
				int x = nx - cx;
				int y = ny - cy;
				int z = nz - cz;
				ChunkPosition newPack = new ChunkPosition(nx, ny, nz);
				if (x * x + y * y + z * z > r || visited.contains(newPack)) {
					continue;
				}
				visited.add(newPack);
				queue.add(newPack);
				Block<?> underBlock = world.getBlockType(Cache.pos.set(nx, ny - 1, nz));
				tilePos = Cache.pos.set(nx, ny, nz);
				Block<?> block = world.getBlockType(tilePos);
				if ((block == Blocks.AIR) && (Blocks.solid[underBlock.id()]) && random.nextInt(3) == 0) {
					createFireAtLocation(world, underBlock, tilePos);
				}
			}
		}
	}

	private static void createFireAtLocation(World world, Block<?> underBlock, TilePos tilePos) {
		if (underBlock.hasTag(BlockTags.INFINITE_BURN_SULFURIC)) {
			world.setBlockTypeNotify(tilePos, Blocks.FIRE_SULFURIC);
			return;
		}
		if (underBlock.hasTag(BlockTags.INFINITE_BURN_COLD)) {
			world.setBlockTypeNotify(tilePos, Blocks.FIRE_COLD);
			return;
		}
		world.setBlockTypeNotify(tilePos, Blocks.FIRE);

	}

    public static void spawnCritParticles(Player player, @NotNull Entity entity) {
		World world = player.world;
		double x = entity.x;
		double y = entity.y + entity.bbHeight;
		double z = entity.z;
		Vector3d view = new Vector3d(player.x - entity.x, 0, player.z - entity.z).normalize();
		Vector3d vector = new Vector3d(0, 1, 0);
		for(int i = 0; i < 12; i++){
			double angle = Math.toRadians(45 + i * 90f);
			Vector3d dir = new Vector3d(vector); // copy each time
			dir = dir.rotateAxis(angle, view.x, view.y, view.z);
			world.spawnParticle("crit", x, y, z, dir.x * 0.2f, dir.y * 0.2f, dir.z * 0.2f, 0, 32.0f, false);
		}



    }
}
