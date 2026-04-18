package googy.betterwithenchanting;

import googy.betterwithenchanting.api.Enchantments;
import googy.betterwithenchanting.block.BlockEnchantmentTable;
import googy.betterwithenchanting.block.TileEntityEnchantmentTable;
import googy.betterwithenchanting.item.EnchantingTags;
import googy.betterwithenchanting.item.ItemEnchantmentBottle;
import googy.betterwithenchanting.render.ItemScoreBottleModel;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import net.minecraft.client.entity.particle.ParticleDispatcher;
import net.minecraft.client.render.EntityRenderDispatcher;
import net.minecraft.client.render.TileEntityRenderDispatcher;
import net.minecraft.client.render.block.color.BlockColorDispatcher;
import net.minecraft.client.render.block.model.BlockModelDispatcher;
import net.minecraft.client.render.block.model.BlockModelStandard;
import net.minecraft.client.render.item.model.ItemModelDispatcher;
import net.minecraft.client.render.texture.stitcher.AtlasStitcher;
import net.minecraft.client.render.texture.stitcher.TextureRegistry;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogic;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.block.tag.BlockTags;
import net.minecraft.core.data.registry.Registries;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.Items;
import net.minecraft.core.sound.BlockSound;
import net.minecraft.core.util.collection.NamespaceID;
import net.minecraft.core.util.helper.Side;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import turniplabs.halplibe.helper.*;
import turniplabs.halplibe.util.*;

import java.util.Properties;

import googy.betterwithenchanting.render.EnchantmentTableRenderer;


public class BetterWithEnchanting implements ModInitializer, ModelEntrypoint, RecipeEntrypoint, GameStartEntrypoint, ClientModInitializer, ClientStartEntrypoint {
	public static final String MOD_ID = "betterwithenchanting";
	public static final Logger LOG = LoggerFactory.getLogger(MOD_ID);
	public static final ConfigHandler CONFIG_HANDLER;
	public static final int START_COST_OFFSET = 5;
	public static final boolean COLORED_PARTICLE;
	public static final boolean ILLAGER_FONT;
	public static final int MAX_ENCHANTMENT_COST;
	public static final int DEFAULT_ITEM_ENCHANTABILITY;
	public static final int WINDOW_ID;

	static {
		Properties prop = new Properties();
		prop.setProperty("max_enchantment_cost", "12000");
		prop.setProperty("enchantment_window_type_id", "24");
		prop.setProperty("packet_enchant_id", "190");
		prop.setProperty("enchantment_table_id", "116");
		prop.setProperty("bottled_score_id", "18444");
		prop.setProperty("expensive_crafting", "true");
		prop.setProperty("default_item_enchantability", "15");
		prop.setProperty("use_illager_font", "true");
		prop.setProperty("colored_particle", "false");
		CONFIG_HANDLER = new ConfigHandler(MOD_ID, prop);

		WINDOW_ID = CONFIG_HANDLER.getInt("enchantment_window_type_id");
		ILLAGER_FONT = CONFIG_HANDLER.getBoolean("use_illager_font");
		COLORED_PARTICLE = CONFIG_HANDLER.getBoolean("colored_particle");
		MAX_ENCHANTMENT_COST = CONFIG_HANDLER.getInt("max_enchantment_cost");
		DEFAULT_ITEM_ENCHANTABILITY = CONFIG_HANDLER.getInt("default_item_enchantability");
	}
	public static final Block ENCHANTMENT_TABLE = new BlockBuilder(MOD_ID)
		.setBlockSound(new BlockSound("step.stone", "step.stone", 1.0f, 1.0f))
		.setHardness(5)
		.setResistance(1200)
		.setLuminance(7)
		.setTags(BlockTags.MINEABLE_BY_PICKAXE)
		.build("enchantment.table", "enchantment_table", CONFIG_HANDLER.getInt("enchantment_table_id"), (b) ->  new BlockEnchantmentTable(b));

	public static final Item SCORE_BOTTLE = new ItemBuilder(MOD_ID).build(new ItemEnchantmentBottle("bottle.score", MOD_ID + ":item/bottle_score", CONFIG_HANDLER.getInt("bottled_score_id")));

	@Override
	public void onInitialize() {
		LOG.info("BetterWithEnchanting initialized!");
	}

	@Override
	public void beforeGameStart() {
		EntityHelper.createTileEntity(TileEntityEnchantmentTable.class, NamespaceID.getPermanent(MOD_ID, "enchantment_table"));
	}

	@Override public void afterGameStart() {
		EnchantingTags.init();
		Enchantments.init();
		Registries.getInstance().register(MOD_ID + ":enchantments", Enchantments.getInstance());
		LOG.info("{} enchantments registered.", Enchantments.getInstance().size());
		LOG.info("Enchantments initialized.");
	}

	@Override
	public void onRecipesReady() {
		RecipeBuilder.Shaped(MOD_ID, " B ", "DCD", "CCC")
			.addInput('B', Items.BOOK)
			.addInput('C', Blocks.OBSIDIAN)
			.addInput('D', CONFIG_HANDLER.getBoolean("expensive_crafting") ? Blocks.BLOCK_DIAMOND : Items.DIAMOND)
			.create("enchantingtable", new ItemStack(ENCHANTMENT_TABLE));

		RecipeBuilder.Shaped(MOD_ID, " D ", "C C", " C ")
			.addInput('D', "minecraft:planks")
			.addInput('C', Blocks.GLASS)
			.create("score_bottle", new ItemStack(ENCHANTMENT_TABLE));
	}

	@Override
	public void initBlockModels(BlockModelDispatcher dispatcher) {
		dispatcher.addDispatch(new BlockModelStandard<BlockLogic>(ENCHANTMENT_TABLE)
			.setTex(BlockModelStandard.BLOCK_TEXTURES, MOD_ID + ":block/top", Side.TOP)
			.setTex(BlockModelStandard.BLOCK_TEXTURES, MOD_ID + ":block/bottom", Side.BOTTOM)
			.setTex(BlockModelStandard.BLOCK_TEXTURES, MOD_ID + ":block/side", Side.WEST, Side.NORTH, Side.SOUTH, Side.EAST)
		);
	}

	@Override public void initItemModels(ItemModelDispatcher dispatcher) {
		dispatcher.addDispatch(new ItemScoreBottleModel(SCORE_BOTTLE, null).setIcon(MOD_ID + ":item/score_bottle1"));
	}

	@Override public void initTileEntityModels(TileEntityRenderDispatcher dispatcher) {
		ModelHelper.setTileEntityModel(TileEntityEnchantmentTable.class, EnchantmentTableRenderer::new);
	}

	@Override
	public void beforeClientStart() {
		ParticleDispatcher.getInstance().addDispatch("enchant", (world, x, y, z, xa, ya, za, id) -> new ParticleGlyph(world, x, y, z, xa, ya, za));
		BetterWithEnchanting.registerTextures();
	}

	public static void registerTextures() {
		for (final AtlasStitcher stitcher : TextureRegistry.stitcherMap.values()) {
			try {
				TextureHelper.initializeAllFiles(MOD_ID, stitcher, Integer.MAX_VALUE);
			} catch (Exception e) {
				LOG.error("Failed to initialize texture files!", e);
			}
		}
	}

	public BetterWithEnchanting() {
//		PacketEnchantItem.addMapping(CONFIG_HANDLER.getInt("packet_enchant_id"), false, true, PacketEnchantItem.class);
	}
	@Override public void initNamespaces() {/* no need */}
	@Override public void initEntityModels(EntityRenderDispatcher dispatcher) {/* no need */}
	@Override public void initBlockColors(BlockColorDispatcher dispatcher) {/* no need */}
	@Override public void onInitializeClient() {/* no need */}
	@Override public void afterClientStart() {/* no need */}
}
