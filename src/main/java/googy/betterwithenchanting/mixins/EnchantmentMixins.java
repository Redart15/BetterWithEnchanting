package googy.betterwithenchanting.mixins;

import googy.betterwithenchanting.BetterWithEnchanting;
import googy.betterwithenchanting.api.EnchantmentContainer;
import googy.betterwithenchanting.api.EnchantmentStack;
import googy.betterwithenchanting.api.Enchantments;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.render.TextureManager;
import net.minecraft.client.render.item.model.ItemModelStandard;
import net.minecraft.client.render.tessellator.Tessellator;
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
import org.lwjgl.opengl.GL11;

import java.util.*;
import java.util.function.UnaryOperator;

import static googy.betterwithenchanting.BetterWithEnchanting.MOD_ID;

public class EnchantmentMixins {
	protected static Random random = new Random();
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

	private EnchantmentMixins() {
	}

	public static void renderGlint(ItemModelStandard asThis, Tessellator tessellator, TextureManager textureManager, ItemStack itemStack, int x, int y, float brightness, float alpha) {
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_ALPHA_TEST);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glColor4f(1F, 1F, 1F, 0.3F); // tint + transparency
		GL11.glColorMask(true, true, true, true);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_COLOR, GL11.GL_ONE);
		textureManager.bindTexture(textureManager.loadTexture("/assets/" + MOD_ID + "/textures/misc/glint.png"));
		GL11.glMatrixMode(GL11.GL_TEXTURE);
		GL11.glPushMatrix();
		float t = (System.currentTimeMillis() % 12000L) / 12000.0F;
		GL11.glTranslatef(t, 0, 0);
		GL11.glRotatef(30F, 0, 0, 1);
		tessellator.startDrawingQuads();
		tessellator.addVertexWithUV(x, y + 16, 0.0, 0, 1);
		tessellator.addVertexWithUV(x + 16, y + 16, 0.0, 1, 1);
		tessellator.addVertexWithUV(x + 16, y, 0.0, 1, 0);
		tessellator.addVertexWithUV(x, y, 0.0, 0, 0);
		tessellator.draw();
		GL11.glPopMatrix();
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glDisable(GL11.GL_BLEND);
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

	// something is wack!
	public static void applyDiscovery(World world, int x, int y, int z, ItemStack stack) {
		int level = EnchantmentContainer.getLevel(stack, Enchantments.DISCOVERY);
		if(level <= 0 || random.nextInt(128) > 1){
			return;
		}
		for(int i = level; i > 0; i--){
			world.dropItem(x, y, z, new ItemStack(Items.DYE, 1, 4));
		}
	}

	public static void applyFortune(World world, int x, int y, int z, ItemStack stack) {
		int level = EnchantmentContainer.getLevel(stack, Enchantments.FORTUNE);
		if(level <= 0 || random.nextInt(128) >= (1 << (level - 1))){
			return;
		}
		world.dropItem(x, y, z, fortuneBag.getRandom(random).getItemStack(random));
	}

	public static void applyInsight(Player player, ItemStack stack) {
		int level = EnchantmentContainer.getLevel(stack, Enchantments.INSIGHT);
		if(level <= 0){
			return;
		}
		player.score += (int) Math.floor(10 * Math.pow(level, 0.85));
	}

	public static ItemStack[] applyMolten(EnumDropCause dropCause, Player player, ItemStack[] drops) {
		if (player == null) {
			return drops;
		}
		ItemStack heldItem = player.getHeldItem();
		int molten = EnchantmentContainer.getLevel(heldItem, Enchantments.MOLTEN);
		int scavange = EnchantmentContainer.getLevel(heldItem, Enchantments.SCAVANGE);
		if (dropCause == EnumDropCause.PROPER_TOOL) {
			List<ItemStack> results = new ArrayList<>();
			if(molten > 0){
				results.addAll(Arrays.asList(processItem(player, drops, EnchantmentMixins::matchSmeltingRecipes)));
			}
			if(scavange > 0 && random.nextBoolean()){
				results.addAll(Arrays.asList(processItem(player, drops, EnchantmentMixins::matchTrommelRecipes)));
			}
			return results.toArray(new ItemStack[]{});
		}
		return drops;
	}

	public static ItemStack[] processItem(Player player, ItemStack[] drops, UnaryOperator<ItemStack> processor) {
		ItemStack heldItem = player.getHeldItem();
		if(heldItem == null || drops == null || drops.length == 0){
			return drops;
		}
		int durabilityDamage = 0;
		int durabilityLeft = heldItem.getMetadata();
		List<ItemStack> results = new ArrayList<>();
		for(ItemStack currentDrop: drops){
			if(durabilityLeft > durabilityDamage){
				ItemStack result = processor.apply(currentDrop);
				if(result.itemID != currentDrop.itemID){
					durabilityDamage += result.stackSize;
				}
				results.add(result);
			}else{
				results.add(currentDrop);
			}
		}
		return results.toArray(new ItemStack[0]);
	}

	private static ItemStack matchSmeltingRecipes(ItemStack currentDrop) {
		for(RecipeEntryBlastFurnace recipeEntryBase : Registries.RECIPES.getAllBlastFurnaceRecipes()) {
			if (recipeEntryBase != null && recipeEntryBase.matches(currentDrop)) {
				return recipeEntryBase.getOutput().copy();
			}
		}
		for(RecipeEntryFurnace recipeEntryBase : Registries.RECIPES.getAllFurnaceRecipes()) {
			if (recipeEntryBase != null && recipeEntryBase.matches(currentDrop)) {
				return recipeEntryBase.getOutput().copy();
			}
		}
		return currentDrop;
	}

	private static ItemStack matchTrommelRecipes(ItemStack currentDrop) {
		for(RecipeEntryTrommel recipe : Registries.RECIPES.getAllTrommelRecipes()) {
			if (recipe.getInput().matches(currentDrop)) {
				return ((recipe.getOutput()).getRandom(random)).getItemStack();
			}
		}
		return currentDrop;
	}
}
