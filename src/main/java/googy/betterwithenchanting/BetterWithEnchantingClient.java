package googy.betterwithenchanting;

import googy.betterwithenchanting.api.EnchantmentAchievements;
import googy.betterwithenchanting.block.EnchantmentBlocks;
import googy.betterwithenchanting.block.TileEntityEnchantmentTable;
import googy.betterwithenchanting.command.CommandEnchantment;
import googy.betterwithenchanting.command.CommandScore;
import googy.betterwithenchanting.item.EnchantmentItems;
import googy.betterwithenchanting.particle.ParticleCrit;
import googy.betterwithenchanting.particle.ParticleGlyph;
import googy.betterwithenchanting.render.EnchantmentTableRenderer;
import googy.betterwithenchanting.render.GlyphRenderer;
import googy.betterwithenchanting.render.ItemModelEnchantmentTable;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.render.TileEntityRenderDispatcher;
import net.minecraft.client.render.block.model.BlockModelDispatcher;
import net.minecraft.client.render.block.model.BlockModelStandard;
import net.minecraft.client.render.item.model.ItemModelDispatcher;
import net.minecraft.client.render.item.model.ItemModelStandard;
import net.minecraft.client.render.particle.Particle;
import net.minecraft.client.render.particle.ParticleDispatcher;
import net.minecraft.client.render.particle.ParticleEntry;
import net.minecraft.client.render.texture.stitcher.AtlasStitcher;
import net.minecraft.client.render.texture.stitcher.TextureRegistry;
import net.minecraft.core.item.block.ItemBlock;
import net.minecraft.core.net.command.CommandManager;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;
import org.jetbrains.annotations.NotNull;
import turniplabs.halplibe.event.defs.ClientEvents;
import turniplabs.halplibe.helper.TextureHelper;
import turniplabs.halplibe.util.dependency.Key;

import static googy.betterwithenchanting.BetterWithEnchanting.MOD_ID;

public class BetterWithEnchantingClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		ClientEvents.AFTER_CLIENT_START.listen(Key.of(MOD_ID), BetterWithEnchantingClient::afterClientStart);
		ClientEvents.BEFORE_CLIENT_START.listen(Key.of(MOD_ID), BetterWithEnchantingClient::beforeClientStart);

		ClientEvents.BLOCK_MODEL_RELOAD.listen(Key.of(MOD_ID), BetterWithEnchantingClient::initBlockModels);
		ClientEvents.ITEM_MODEL_RELOAD.listen(Key.of(MOD_ID), BetterWithEnchantingClient::initItemModels);
		ClientEvents.TILE_ENTITY_RENDERER_RELOAD.listen(Key.of(MOD_ID), BetterWithEnchantingClient::initTileEntityModels);
	}

	public static void beforeClientStart() {
		CommandManager.registerCommand(new CommandEnchantment());
		CommandManager.registerCommand(new CommandScore());
		ParticleDispatcher.getInstance().addDispatch("enchant",
			new ParticleEntry() {
				@Override
				public Particle newParticle(
					@NotNull World world,
					double x, double y, double z,
					double xa, double ya, double za,
					int data
				) {
					return new ParticleGlyph(world, x, y, z, xa, ya, za);
				}
			}
		);
		ParticleDispatcher.getInstance().addDispatch("crit",
			new ParticleEntry() {
				@Override
				public Particle newParticle(
					@NotNull World world,
					double x, double y, double z,
					double xa, double ya, double za,
					int data
				) {
					return new ParticleCrit(world, x, y, z, xa, ya, za);
				}
			}
		);

		BetterWithEnchantingClient.registerTextures();
	}

	public static void registerTextures() {
		for (final AtlasStitcher stitcher : TextureRegistry.stitcherMap.values()) {
			TextureHelper.initializeAllFiles(MOD_ID, stitcher, true);
		}
	}


	public static void afterClientStart() {
		GlyphRenderer.init();
		EnchantmentAchievements.init();
	}

	static final String BLOCK_DIR = MOD_ID + ":block/";
	static final String ITEM_DIR = MOD_ID + ":item/";
	static final Side[] SIDES = new Side[]{Side.WEST, Side.NORTH, Side.SOUTH, Side.EAST};

	public static void initBlockModels(BlockModelDispatcher dispatcher) {
		dispatcher.addDispatch(new BlockModelStandard<>(EnchantmentBlocks.ENCHANTMENT_TABLE)
			.setTex(BLOCK_DIR + "enchanter/top", Side.TOP)
			.setTex(BLOCK_DIR + "enchanter/bottom", Side.BOTTOM)
			.setTex(BLOCK_DIR + "enchanter/side", SIDES)
		);

		dispatcher.addDispatch(new BlockModelStandard<>(EnchantmentBlocks.ENCHANTED_BOOKSHELF)
			.setTex(BLOCK_DIR + "enchanted_book_shelf/top", Side.TOP)
			.setTex(BLOCK_DIR + "enchanted_book_shelf/top", Side.BOTTOM)
			.setTex(BLOCK_DIR + "enchanted_book_shelf/side", SIDES)
		);
	}

	public static void initItemModels(ItemModelDispatcher dispatcher) {
		dispatcher.addDispatch(new ItemModelStandard(EnchantmentItems.SCORE_BOTTLE, true).setIcon(ITEM_DIR + "score_bottle1"));
		dispatcher.addDispatch(new ItemModelEnchantmentTable((ItemBlock<?>) EnchantmentBlocks.ENCHANTMENT_TABLE.asItem()));
		dispatcher.addDispatch(new ItemModelStandard( EnchantmentItems.ENCHANTED_BOOK, true).setIcon(ITEM_DIR + "enchanted_book"));
	}

	public static void initTileEntityModels(TileEntityRenderDispatcher dispatcher) {
		dispatcher.assignRenderer(TileEntityEnchantmentTable.class, new EnchantmentTableRenderer());
	}
}

