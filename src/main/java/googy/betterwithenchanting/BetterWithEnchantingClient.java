package googy.betterwithenchanting;

import googy.betterwithenchanting.command.CommandEnchantment;
import googy.betterwithenchanting.command.CommandScore;
import googy.betterwithenchanting.particle.ParticleCrit;
import googy.betterwithenchanting.particle.ParticleGlyph;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.render.particle.Particle;
import net.minecraft.client.render.particle.ParticleDispatcher;
import net.minecraft.client.render.particle.ParticleEntry;
import net.minecraft.client.render.texture.stitcher.AtlasStitcher;
import net.minecraft.client.render.texture.stitcher.TextureRegistry;
import net.minecraft.core.net.command.CommandManager;
import net.minecraft.core.world.World;
import org.jetbrains.annotations.NotNull;
import turniplabs.halplibe.helper.TextureHelper;
import turniplabs.halplibe.util.ClientStartEntrypoint;

import static googy.betterwithenchanting.BetterWithEnchanting.MOD_ID;

public class BetterWithEnchantingClient implements ClientModInitializer, ClientStartEntrypoint {

	@Override
	public void beforeClientStart() {
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

	@Override
	public void onInitializeClient() {/* no need */}

	@Override
	public void afterClientStart() {/* no need */}
}

