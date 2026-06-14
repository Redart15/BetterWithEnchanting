package googy.betterwithenchanting;

import googy.betterwithenchanting.command.CommandEnchantment;
import googy.betterwithenchanting.command.CommandScore;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.entity.particle.ParticleDispatcher;
import net.minecraft.client.render.texture.stitcher.AtlasStitcher;
import net.minecraft.client.render.texture.stitcher.TextureRegistry;
import net.minecraft.core.net.command.CommandManager;
import turniplabs.halplibe.helper.TextureHelper;
import turniplabs.halplibe.util.ClientStartEntrypoint;

import static googy.betterwithenchanting.BetterWithEnchanting.LOG;
import static googy.betterwithenchanting.BetterWithEnchanting.MOD_ID;

public class BetterWithEnchantingClient implements ClientModInitializer, ClientStartEntrypoint {

	@Override
	public void beforeClientStart() {
		CommandManager.registerCommand(new CommandEnchantment());
		CommandManager.registerCommand(new CommandScore());
		ParticleDispatcher.getInstance().addDispatch("enchant", (world, x, y, z, xa, ya, za, id) -> new ParticleGlyph(world, x, y, z, xa, ya, za));
		BetterWithEnchantingClient.registerTextures();
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

	@Override public void onInitializeClient() {/* no need */}
	@Override public void afterClientStart() {/* no need */}
}

