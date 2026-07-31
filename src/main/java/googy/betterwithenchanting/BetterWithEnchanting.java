package googy.betterwithenchanting;

import googy.betterwithenchanting.api.EnchantmentAchievements;
import googy.betterwithenchanting.api.Enchantments;
import googy.betterwithenchanting.block.EnchantmentBlocks;
import googy.betterwithenchanting.block.TileEntityEnchantmentTable;
import googy.betterwithenchanting.item.EnchantmentItems;
import googy.betterwithenchanting.item.EnchantmentTags;
import googy.betterwithenchanting.network.OpenGuiBookMessage;
import googy.betterwithenchanting.network.EnchantItemMessage;
import googy.betterwithenchanting.network.OpenGuiTableMessage;
import googy.betterwithenchanting.network.UpdateLockState;
import net.fabricmc.api.ModInitializer;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.data.registry.Registries;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.Items;
import net.minecraft.core.util.collection.NamespaceID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import turniplabs.halplibe.HalpLibe;
import turniplabs.halplibe.event.defs.CommonEvents;
import turniplabs.halplibe.helper.*;
import turniplabs.halplibe.helper.network.NetworkHandler;
import turniplabs.halplibe.util.*;
import turniplabs.halplibe.util.dependency.Key;
import turniplabs.halplibe.util.toml.Toml;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;


public class BetterWithEnchanting implements ModInitializer{
	public static final String MOD_ID = HalpLibe.registerMod("betterwithenchanting");
	public static final Logger LOG = LoggerFactory.getLogger(MOD_ID);

	public static final int MAX_ENCHANTMENT_COST = 12000;
	public static final int DEFAULT_ITEM_ENCHANTABILITY = 15;
	private static final TomlConfigHandler CONFIG_HANDLER;
	private static final String GENERAL_CATEGORY = "General.";
	public static final int BLOCK_ID;
	public static final int ITEM_ID;

	public static final boolean COLORED_PARTICLE;
	public static final boolean ILLAGER_FONT;
	public static final boolean DESTRUCTIBLE;
	public static final int CHANCE;
	public static final boolean CAN_PICKUP;

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
		LOG.info("Better with Enchanting loading properties.");
		LOG.info("Initializing config.");
		Toml properties = new Toml("Better with Enchanting config");
		properties.addCategory("General")
			.addEntry("BLOCK_ID", 116)
			.addEntry("ITEM_ID", 18444)
			.addEntry("USE_ILLAGER_FONT", "Makes the particles and Enchanting Table use Illager font.", true)
			.addEntry("USE_COLORED_PARTICLES", "Makes the particle spawed by Enchanting Table colored", false)
			.addEntry("DESTRUCTIBLE", "Enchanting Table cannot be crafted or mined." , true)
			.addEntry("CHANCE", "Chance for an enchanting table to spawn in the library. Odds are 1/value, 1 equals 100%, 10 equals 10%" , 10)
			.addEntry("CAN_PICKUP", "Only applies when DESTRUCTIBLE is false. Allows the enchanting table to be picked up.", true);
		CONFIG_HANDLER = new TomlConfigHandler(MOD_ID, properties);
		File configFile = CONFIG_HANDLER.getConfigFile();
		if(configFile.exists()){
			LOG.info("Loading config.");
			try{
				CONFIG_HANDLER.loadConfig();
			}catch (Exception e){
				LOG.error("Failed to load config, creating a new one.", e);
				try {
					Files.move(
						configFile.toPath(),
						configFile.toPath().resolveSibling("enchant.bak"),
						StandardCopyOption.REPLACE_EXISTING
					);
					CONFIG_HANDLER.writeConfig();
				}catch (IOException io){
					throw new RuntimeException("Failed to replace corrupt config.", io);
				}
			}
		}else {
			try{
				LOG.info("Creating config.");
				if(!configFile.createNewFile()){
					throw new IOException("Could not create config file");
				}
				LOG.info("Write to config");
				CONFIG_HANDLER.writeConfig();
			}
			catch (IOException e){
				throw new RuntimeException("Failed to create config.", e);
			}
		}
		BLOCK_ID = CONFIG_HANDLER.getInt(GENERAL_CATEGORY + "BLOCK_ID");
		ITEM_ID = CONFIG_HANDLER.getInt(GENERAL_CATEGORY + "ITEM_ID");
		ILLAGER_FONT = CONFIG_HANDLER.getBoolean(GENERAL_CATEGORY + "USE_ILLAGER_FONT");
		COLORED_PARTICLE = CONFIG_HANDLER.getBoolean(GENERAL_CATEGORY + "USE_COLORED_PARTICLES");
		DESTRUCTIBLE = CONFIG_HANDLER.getBoolean(GENERAL_CATEGORY + "DESTRUCTIBLE");
		CHANCE = CONFIG_HANDLER.getInt(GENERAL_CATEGORY + "CHANCE");
		CAN_PICKUP = CONFIG_HANDLER.getBoolean(GENERAL_CATEGORY + "CAN_PICKUP");
	}

	@Override
	public void onInitialize() {
		NetworkHandler.registerNetworkMessage(EnchantItemMessage::new);
		NetworkHandler.registerNetworkMessage(OpenGuiBookMessage::new);
		NetworkHandler.registerNetworkMessage(OpenGuiTableMessage::new);
		NetworkHandler.registerNetworkMessage(UpdateLockState::new);
		CommonEvents.BEFORE_GAME_START.listen(Key.of(MOD_ID), BetterWithEnchanting::beforeGameStart);
		CommonEvents.AFTER_GAME_START.listen(Key.of(MOD_ID), BetterWithEnchanting::afterGameStart);
		CommonEvents.RECIPES_NAMESPACE_INIT.listen(Key.of(MOD_ID), BetterWithEnchanting::initNamespaces);
		CommonEvents.RECIPES_READY.listen(Key.of(MOD_ID), BetterWithEnchanting::onRecipesReady);
		CommonEvents.AFTER_BLOCK_INIT.listen(Key.of(MOD_ID), EnchantmentBlocks::afterBlockInit);
		CommonEvents.AFTER_ITEM_INIT.listen(Key.of(MOD_ID), EnchantmentItems::afterItemInit);
		LOG.info("BetterWithEnchanting initialized!");
	}


	public static void beforeGameStart() {
		EntityHelper.addMapping(TileEntityEnchantmentTable.class, NamespaceID.getPermanent(MOD_ID, "enchantment_table"));
	}

	public static void afterGameStart() {
		EnchantmentTags.init();
		Enchantments.init();
		Registries.getInstance().register(MOD_ID + ":enchantments", Enchantments.getInstance());
		LOG.info("Registered {} enchantments.", Enchantments.getInstance().size());
	}

	public static void onRecipesReady() {
		if(!DESTRUCTIBLE){
			return;
		}
		RecipeBuilder.Shaped(MOD_ID, "CBC", "DOD", "OOO")
			.addInput('C', Items.CLOTH)
			.addInput('B', Items.BOOK)
			.addInput('O', "minecraft:cobblestones")
			.addInput('D', Items.DIAMOND)
			.create("enchantingtable", new ItemStack(EnchantmentBlocks.ENCHANTMENT_TABLE));
	}

	public static void initNamespaces() {
		RecipeBuilder.initNameSpace(MOD_ID);
	}

}
