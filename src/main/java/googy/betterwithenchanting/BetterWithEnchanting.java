package googy.betterwithenchanting;

import googy.betterwithenchanting.block.BlockEnchantmentTable;
import googy.betterwithenchanting.block.TileEntityEnchantmentTable;
import googy.betterwithenchanting.particle.ParticleGlyph;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import net.minecraft.client.entity.particle.ParticleDispatcher;
import net.minecraft.client.render.EntityRenderDispatcher;
import net.minecraft.client.render.TileEntityRenderDispatcher;
import net.minecraft.client.render.block.color.BlockColorDispatcher;
import net.minecraft.client.render.block.model.BlockModelDispatcher;
import net.minecraft.client.render.block.model.BlockModelStandard;
import net.minecraft.client.render.item.model.ItemModelDispatcher;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogic;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.block.tag.BlockTags;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.Items;
import net.minecraft.core.sound.BlockSound;
import net.minecraft.core.util.collection.NamespaceID;
import net.minecraft.core.util.helper.Side;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import turniplabs.halplibe.helper.BlockBuilder;
import turniplabs.halplibe.helper.EntityHelper;
import turniplabs.halplibe.helper.ModelHelper;
import turniplabs.halplibe.helper.RecipeBuilder;
import turniplabs.halplibe.util.*;

import java.util.Properties;

import googy.betterwithenchanting.block.EnchantmentTableRenderer;


public class BetterWithEnchanting implements ModInitializer, ModelEntrypoint, RecipeEntrypoint, GameStartEntrypoint, ClientModInitializer, ClientStartEntrypoint {
	public static final String MOD_ID = "betterwithenchanting";
	public static final Logger LOG = LoggerFactory.getLogger(MOD_ID);
	public static final ConfigHandler config;
	public static int START_COST_OFFSET = 5;
	public static String ENCHANTMENT_TABLE_NAME = "Enchantment Table";


	static {
		Properties prop = new Properties();
		prop.setProperty("max_enchantment_cost", "12000");
		prop.setProperty("enchantment_window_type_id", "24");
		prop.setProperty("packet_enchant_id", "190");
		prop.setProperty("enchantment_table_id", "116");
		prop.setProperty("expensive_crafting", "true");
		prop.setProperty("default_item_enchantability", "15");
		config = new ConfigHandler(MOD_ID, prop);
	}
	public static final Block ENCHANTMENT_TABLE = new BlockBuilder(MOD_ID)
		.setBlockSound(new BlockSound("step.stone", "step.stone", 1.0f, 1.0f))
		.setHardness(5)
		.setResistance(1200)
		.setLuminance(7)
		.setTags(BlockTags.MINEABLE_BY_PICKAXE)
		.build("enchantment.table", "enchantment_table", config.getInt("enchantment_table_id"), (b) -> new BlockEnchantmentTable(b));

	@Override
	public void onInitialize() {
		LOG.info("BetterWithEnchanting initialized!");
	}

	@Override
	public void onRecipesReady() {
		RecipeBuilder.Shaped(MOD_ID, " B ", "DCD", "CCC")
			.addInput('B', Items.BOOK)
			.addInput('C', Blocks.OBSIDIAN)
			.addInput('D', config.getBoolean("expensive_crafting") ? Blocks.BLOCK_DIAMOND : Items.DIAMOND)
			.create("enchantingtable", new ItemStack(ENCHANTMENT_TABLE));
	}

	@Override
	public void initBlockModels(BlockModelDispatcher dispatcher) {
		dispatcher.addDispatch(new BlockModelStandard<BlockLogic>(ENCHANTMENT_TABLE)
			.setTex(BlockModelStandard.BLOCK_TEXTURES, MOD_ID + ":block/enchantment_table/top", Side.TOP)
			.setTex(BlockModelStandard.BLOCK_TEXTURES, MOD_ID + ":block/enchantment_table/bottom", Side.BOTTOM)
			.setTex(BlockModelStandard.BLOCK_TEXTURES, MOD_ID + ":block/enchantment_table/side", Side.WEST, Side.NORTH, Side.SOUTH, Side.EAST)
		);
	}

	@Override
	public void beforeGameStart() {
		EntityHelper.createTileEntity(TileEntityEnchantmentTable.class, NamespaceID.getPermanent(MOD_ID, "enchantment_table"));
	}

	@Override public void initTileEntityModels(TileEntityRenderDispatcher dispatcher) {
		ModelHelper.setTileEntityModel(TileEntityEnchantmentTable.class, EnchantmentTableRenderer::new);
	}

	@Override
	public void beforeClientStart() {
		ParticleDispatcher.getInstance().addDispatch(
			"enchant", (world, x, y, z, xa, ya, za, id) -> new ParticleGlyph(world, x, y, z, xa, ya, za));
	}

	public BetterWithEnchanting() {/*	PacketMixin.callAddIdClassMapping(Global.config.getInt("packet_enchant_id"), false, true, PacketEnchantItem.class); */}
	@Override public void initNamespaces() {/* no need */}
	@Override public void initItemModels(ItemModelDispatcher dispatcher) {/* no need */}
	@Override public void initEntityModels(EntityRenderDispatcher dispatcher) {/* no need */}
	@Override public void initBlockColors(BlockColorDispatcher dispatcher) {/* no need */}
	@Override public void afterGameStart() {/* no need */}
	@Override public void onInitializeClient() {/* no need */}

	@Override public void afterClientStart() {/* no need */}


}
