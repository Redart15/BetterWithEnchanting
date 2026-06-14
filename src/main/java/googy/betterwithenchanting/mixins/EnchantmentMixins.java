package googy.betterwithenchanting.mixins;

import googy.betterwithenchanting.BetterWithEnchanting;
import googy.betterwithenchanting.api.EnchantmentContainer;
import googy.betterwithenchanting.api.EnchantmentStack;
import googy.betterwithenchanting.api.Enchantments;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.render.TextureManager;
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

	public static void applyDiscovery(World world, int x, int y, int z, ItemStack stack) {
		int level = EnchantmentContainer.getLevel(stack, Enchantments.CATALYST);
		if (level <= 0 || random.nextInt(128) > 1) {
			return;
		}
		for (int i = level; i > 0; i--) {
			world.dropItem(x, y, z, new ItemStack(Items.DYE, 1, 4));
		}
	}

	public static void applyFortune(World world, int x, int y, int z, ItemStack stack) {
		int level = EnchantmentContainer.getLevel(stack, Enchantments.FORTUNE);
		if (level <= 0 || random.nextInt(128) >= (1 << (level - 1))) {
			return;
		}
		world.dropItem(x, y, z, fortuneBag.getRandom(random).getItemStack(random));
	}

	public static void applyInsight(Player player, ItemStack stack) {
		int level = EnchantmentContainer.getLevel(stack, Enchantments.INSIGHT);
		if (level <= 0) {
			player.score += 3;// to give more excess to xp
		}else{
			player.score += (int) Math.floor(10 * Math.pow(level, 0.85));
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
				results.addAll(Arrays.asList(processItem(player, drops, EnchantmentMixins::matchSmeltingRecipes)));
			}
			if (scavenge > 0 && random.nextBoolean()) {
				results.addAll(Arrays.asList(processItem(player, drops, EnchantmentMixins::matchTrommelRecipes)));
			}
			return results.toArray(new ItemStack[]{});
		}
		return drops;
	}

	public static ItemStack[] processItem(Player player, ItemStack[] drops, UnaryOperator<ItemStack> processor) {
		ItemStack heldItem = player.getHeldItem();
		if (heldItem == null || drops == null || drops.length == 0) {
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
			if (recipeEntryBase != null && recipeEntryBase.matches(currentDrop)) {
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

	/// Period 3000, offset 1873
	public static final int PERIOD = 3000;
	public static final int OFF_SET = 1873;

	/// RGBA (0.5, 0.25, 0.8, 1.0)
	private static final float R = 0.1F;
	private static final float G = 0.1F;
	private static final float B = 0.5F;
	private static final float A = 1.0F;
	public static final String TEXTURE = "/assets/" + MOD_ID + "/textures/misc/glintB.png";

	private static float getOffset(int i, float factor) {
		int samplingTime = PERIOD + i * OFF_SET;
		return (System.currentTimeMillis() % samplingTime) / ((float)samplingTime) * factor;
	}

    public static void renderEffectFlat(Tessellator tessellator, TextureManager textureManager, ItemStack itemStack) {
        if (!EnchantmentContainer.hasEnchantments(itemStack)) {
            return;
        }
        GL11.glDepthFunc(GL11.GL_EQUAL);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDepthMask(true);
        textureManager.bindTexture(textureManager.loadTexture(TEXTURE));
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_DST_COLOR, GL11.GL_DST_COLOR);
        GL11.glColor4f(R, G, B, A);
        for (int i = 0; i < 2; ++i) {
            GL11.glBlendFunc(GL11.GL_SRC_COLOR, GL11.GL_ONE);
            float c = 0.00390625F;
            float startingOffset = getOffset(i, 256.0F);
            float shiftY = i == 1 ? -1.0f : 4.0F;

            double u1 = (startingOffset + (double) 20.0f * shiftY) * c;
            double u2 = (startingOffset + (double) 20.0f + (double) 20.0f * shiftY) * c;
            double u3 = (startingOffset + (double) 20.0f) * c;
            double u4 = startingOffset * c;
            double v12 = (double) 20.0f * c;
            double v34 = 0.0f * c;

            tessellator.startDrawingQuads();
            tessellator.setNormal(0.0F, 1.0F, 0.0F);
            tessellator.addVertexWithUV(-0.5f, -0.25f, 0.0f, u1, v12);
            tessellator.addVertexWithUV(0.5f, -0.25f, 0.0f, u2, v12);
            tessellator.addVertexWithUV(0.5f, 0.75f, 0.0f, u3, v34);
            tessellator.addVertexWithUV(-0.5f, 0.75f, 0.0f, u4, v34);
            tessellator.draw();
        }
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glDepthMask(true);
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glDepthFunc(GL11.GL_LEQUAL);
    }

	public static void renderEffectGui(
        Tessellator tessellator, TextureManager textureManager, ItemStack itemStack,
        int x, int y, int offX, int offY
    ) {
        GL11.glDepthFunc(GL11.GL_GREATER);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDepthMask(false);
        textureManager.bindTexture(textureManager.loadTexture(TEXTURE));
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_DST_COLOR, GL11.GL_DST_COLOR);
        GL11.glColor4f(R, G, B, A);
		renderGlint(tessellator, x, y, offX, offY);
		GL11.glColor4f(R, G, B, A);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glDepthMask(true);
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glDepthFunc(GL11.GL_LEQUAL);
    }

	private static void renderGlint2D(Tessellator tessellator) {
		float colorDimmer = 0.86F; // original 0.76
		GL11.glColor4f(colorDimmer * R, colorDimmer * G, colorDimmer * B, A);
		GL11.glMatrixMode(GL11.GL_TEXTURE);
		GL11.glPushMatrix();
		float scaling = 1.0F / 16.0F;
		GL11.glScalef(scaling, scaling, scaling);
		float offset = getOffset(0, 8.0f);
		GL11.glTranslatef(offset, 0.0F, 0.0F);
		GL11.glRotatef(-50.0F, 0.0F, 0.0F, 1.0F);
		renderItemIn2D(tessellator, 0.0f, 1.0f, 0.0f, 1.0f, 256, 256, 0.0625F);
		GL11.glPopMatrix();
		GL11.glPushMatrix();
		GL11.glScalef(scaling, scaling, scaling);
		offset = getOffset(1, 8.0f);
		GL11.glTranslatef(-offset, 0.0F, 0.0F);
		GL11.glRotatef(10.0F, 0.0F, 0.0F, 1.0F);
		renderItemIn2D(tessellator, 0.0f, 1.0f, 0.0f, 1.0f, 256, 256, 0.0625F);
		GL11.glPopMatrix();
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
	}

	public static void renderEffect2D(Tessellator tessellator, TextureManager textureManager, ItemStack itemStack) {
		if (!EnchantmentContainer.hasEnchantments(itemStack)) {
			return;
		}
		GL11.glDepthFunc(GL11.GL_EQUAL);
		GL11.glDisable(GL11.GL_LIGHTING);
		textureManager.bindTexture(textureManager.loadTexture(EnchantmentMixins.TEXTURE));
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_COLOR, GL11.GL_ONE);
		renderGlint2D(tessellator);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glDepthFunc(GL11.GL_LEQUAL);
	}

	private static void renderGlint(Tessellator tessellator, int x, int y, int offX, int offY) {
		for (int i = 0; i < 2; ++i) {
			GL11.glBlendFunc(GL11.GL_SRC_COLOR, GL11.GL_ONE);
			float adj = 0.00390625F;
			float var9 = getOffset(i, 256.0F);
			float var12 = i == 1 ? -1.0f : 4.0F;

			float u1 = (var9 + offY * var12) * adj;
			float u2 = (var9 + offX + offY * var12) * adj;
			float u3 = (var9 + offX) * adj;
			float u4 = (var9 + 0.0F) * adj;

			float v12 = offY * adj;
			float v34 = 0.0f;

			float z = 1.0f;
			tessellator.startDrawingQuads();
			tessellator.addVertexWithUV(x, 					(double)y + offY, 	z, u1, v12);
			tessellator.addVertexWithUV((double)x + offX, 	(double)y + offY, 	z, u2, v12);
			tessellator.addVertexWithUV((double)x + offX, 	y, 					z, u3, v34);
			tessellator.addVertexWithUV(x, y, 			z, u4, v34);
			tessellator.draw();
		}
	}

	private static void renderItemIn2D(Tessellator tessellator, float uMin, float uMax, float vMin, float vMax, int tileWidth, int tileHeight, float thickness) {
		float foon = 0.5F / tileHeight;
		float goon = thickness * (16.0F /  tileWidth);
		float pixelWidth = 1.0F /  tileWidth;
		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, 0.0F, 1.0F);
		tessellator.addVertexWithUV(0.0F, 0.0F, 0.0F, uMax, vMax);
		tessellator.addVertexWithUV(1.0F, 0.0F, 0.0F, uMin, vMax);
		tessellator.addVertexWithUV(1.0F, 1.0F, 0.0F, uMin, vMin);
		tessellator.addVertexWithUV(0.0F, 1.0F, 0.0F, uMax, vMin);
		tessellator.draw();
		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, 0.0F, -1.0F);
		tessellator.addVertexWithUV(0.0F, 1.0F, -thickness, uMax, vMin);
		tessellator.addVertexWithUV(1.0F, 1.0F, -thickness, uMin, vMin);
		tessellator.addVertexWithUV(1.0F, 0.0F, -thickness, uMin, vMax);
		tessellator.addVertexWithUV(0.0F, 0.0F, -thickness, uMax, vMax);
		tessellator.draw();
		tessellator.startDrawingQuads();
		tessellator.setNormal(-1.0F, 0.0F, 0.0F);

		float uDiff = uMin - uMax;
		float vDiff = vMin - vMax;
		for (int i = 0; i < tileWidth; ++i) {
			float texProgress = i * pixelWidth;
			float u = uMax + uDiff * texProgress - foon;
			tessellator.addVertexWithUV(texProgress, 0.0F, -thickness, u, vMax);
			tessellator.addVertexWithUV(texProgress, 0.0F, 0.0F, u, vMax);
			tessellator.addVertexWithUV(texProgress, 1.0F, 0.0F, u, vMin);
			tessellator.addVertexWithUV(texProgress, 1.0F, -thickness, u, vMin);
		}

		tessellator.draw();
		tessellator.startDrawingQuads();
		tessellator.setNormal(1.0F, 0.0F, 0.0F);

		for (int i = 0; i < tileWidth; ++i) {
			float texProgress = i * pixelWidth;
			float u = uMax + uDiff * texProgress - foon;
			float x = texProgress + goon;
			tessellator.addVertexWithUV(x, 1.0F, -thickness, u, vMin);
			tessellator.addVertexWithUV(x, 1.0F, 0.0F, u, vMin);
			tessellator.addVertexWithUV(x, 0.0F, 0.0F, u, vMax);
			tessellator.addVertexWithUV(x, 0.0F, -thickness, u, vMax);
		}

		tessellator.draw();
		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, 1.0F, 0.0F);

		for (int i = 0; i < tileWidth; ++i) {
			float texProgress = i * pixelWidth;
			float v = vMax + vDiff * texProgress - foon;
			float y = texProgress + goon;
			tessellator.addVertexWithUV(0.0F, y, 0.0F, uMax, v);
			tessellator.addVertexWithUV(1.0F, y, 0.0F, uMin, v);
			tessellator.addVertexWithUV(1.0F, y, -thickness, uMin, v);
			tessellator.addVertexWithUV(0.0F, y, -thickness, uMax, v);
		}

		tessellator.draw();
		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, -1.0F, 0.0F);

		for (int i = 0; i < tileWidth; ++i) {
			float texProgress = i * pixelWidth;
			float v = vMax + vDiff * texProgress - foon;
			tessellator.addVertexWithUV(1.0F, texProgress, 0.0F, uMin, v);
			tessellator.addVertexWithUV(0.0F, texProgress, 0.0F, uMax, v);
			tessellator.addVertexWithUV(0.0F, texProgress, -thickness, uMax, v);
			tessellator.addVertexWithUV(1.0F, texProgress, -thickness, uMin, v);
		}

		tessellator.draw();
	}
}
