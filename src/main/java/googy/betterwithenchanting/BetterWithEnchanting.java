package googy.betterwithenchanting;

import googy.betterwithenchanting.api.Enchantments;
import googy.betterwithenchanting.block.BlockEnchantmentTable;
import googy.betterwithenchanting.block.TileEntityEnchantmentTable;
import googy.betterwithenchanting.item.EnchantingTags;
import googy.betterwithenchanting.item.ItemEnchantmentBottle;
import googy.betterwithenchanting.network.MessageEnchantItem;
import googy.betterwithenchanting.render.GlyphRenderer;
import net.fabricmc.api.ModInitializer;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.block.tag.BlockTags;
import net.minecraft.core.data.registry.Registries;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.Items;
import net.minecraft.core.item.tag.ItemTags;
import net.minecraft.core.lang.I18n;
import net.minecraft.core.sound.BlockSound;
import net.minecraft.core.util.collection.NamespaceID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import turniplabs.halplibe.HalpLibe;
import turniplabs.halplibe.helper.*;
import turniplabs.halplibe.helper.network.NetworkHandler;
import turniplabs.halplibe.util.*;

import java.util.Properties;


public class BetterWithEnchanting implements ModInitializer, RecipeEntrypoint, GameStartEntrypoint{
	public static final String MOD_ID = HalpLibe.registerMod("betterwithenchanting");
	public static final Logger LOG = LoggerFactory.getLogger(MOD_ID);
	public static final ConfigHandler CONFIG_HANDLER;
	public static final int START_COST_OFFSET = 5;
	public static final boolean COLORED_PARTICLE;
	public static final boolean ILLAGER_FONT;
	public static final int MAX_ENCHANTMENT_COST;
	public static final int DEFAULT_ITEM_ENCHANTABILITY;
	public static final int WINDOW_ID;
	@SuppressWarnings({"java:S1104", "java:S1444", "java:S3008"}) public static I18n TRANSLATE;

	/// cant be client side, needs to be core
	public static final String[] LABELS = new String[]{
		"powerful", "strong", "loyal", "vital", "enduring", "focused", "potent", "swift", "agile",
		"unbreaking", "fortunate", "wise", "keen", "resilient", "tireless", "durable", "fierce",
		"lethal", "dominant", "pure", "exalted", "blessed", "enhanced", "elevated",

		"frail", "feeble", "brittle", "cursed", "blighted", "tainted", "rotten", "vulnerable", "exposed",
		"broken", "ruined", "fractured", "crippled", "confused", "dazed", "unstable", "deranged", "delirious",
		"drained", "exhausted", "sinister", "suppressed", "profane", "forsaken"
	};

	static {
		Properties prop = new Properties();
		// not sure if I keep them or not
		prop.setProperty("max_enchantment_cost", "12000");
		prop.setProperty("default_item_enchantability", "15");
		// functional
		prop.setProperty("enchantment_table_id", "116");
		prop.setProperty("bottled_score_id", "18444");
		prop.setProperty("enchantment_window_type_id", "24");
		prop.setProperty("expensive_crafting", "true");
		// cosmetic
		prop.setProperty("use_illager_font", "true");
		prop.setProperty("colored_particle", "false");
		CONFIG_HANDLER = new ConfigHandler(MOD_ID, prop);

		WINDOW_ID = CONFIG_HANDLER.getInt("enchantment_window_type_id");
		ILLAGER_FONT = CONFIG_HANDLER.getBoolean("use_illager_font");
		COLORED_PARTICLE = CONFIG_HANDLER.getBoolean("colored_particle");
		MAX_ENCHANTMENT_COST = CONFIG_HANDLER.getInt("max_enchantment_cost");
		DEFAULT_ITEM_ENCHANTABILITY = CONFIG_HANDLER.getInt("default_item_enchantability");
	}
	public static final Block<?> ENCHANTMENT_TABLE = new BlockBuilder(MOD_ID)
		.setBlockSound(new BlockSound("step.stone", "step.stone", 1.0f, 1.0f))
		.setHardness(5)
		.setResistance(1200)
		.setLuminance(7)
		.setTags(BlockTags.MINEABLE_BY_PICKAXE)
		.build("enchantment.table", "enchantment_table", CONFIG_HANDLER.getInt("enchantment_table_id"), BlockEnchantmentTable::new);

	public static final Item SCORE_BOTTLE = new ItemBuilder(MOD_ID)
		.addTags(ItemTags.NOT_IN_CREATIVE_MENU)
		.build(new ItemEnchantmentBottle("bottle.score", MOD_ID + ":item/bottle_score", CONFIG_HANDLER.getInt("bottled_score_id")));

	@Override
	public void onInitialize() {
		NetworkHandler.registerNetworkMessage(MessageEnchantItem::new);
		LOG.info("BetterWithEnchanting initialized!");
	}

	@Override
	public void beforeGameStart() {
		EntityHelper.addMapping(TileEntityEnchantmentTable.class, NamespaceID.getPermanent(MOD_ID, "enchantment_table"));
	}

	@Override public void afterGameStart() {
		EnchantingTags.init();
		Enchantments.init();
		Registries.getInstance().register(MOD_ID + ":enchantments", Enchantments.getInstance());
		TRANSLATE = I18n.getInstance();
		LOG.info("Registered {} enchantments.", Enchantments.getInstance().size());
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

	@Override public void initNamespaces() {
		RecipeBuilder.initNameSpace(MOD_ID);
	}

}
