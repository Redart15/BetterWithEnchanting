package googy.betterwithenchanting.particle;

import net.minecraft.client.render.particle.Particle;
import net.minecraft.client.render.tessellator.TessellatorParticle;
import net.minecraft.client.render.texture.stitcher.TextureRegistry;
import net.minecraft.core.Global;
import net.minecraft.core.world.World;
import org.jetbrains.annotations.NotNull;

import static googy.betterwithenchanting.BetterWithEnchanting.MOD_ID;

public class ParticleCrit extends Particle {
	private final float oSize;

	public ParticleCrit(@NotNull World world, double x, double y, double z, double xa, double ya, double za) {
		super(world, x, y, z, xa, ya, za);
		this.tex = TextureRegistry.getTexture(MOD_ID + ":particle/crit");
		this.oSize = this.size;
//		this.lifetime = 10 * Global.TICKS_PER_SECOND;
	}

	@Override
	public void render(@NotNull TessellatorParticle tessellatorParticle, float partialTick) {
		float s = (this.age + partialTick) / this.lifetime;
		this.size = this.oSize * (1.0F - s * s * 0.5F);
		super.render(tessellatorParticle, partialTick);
	}

	@Override
	public void tick() {
		this.cachedLightmapCoord = this.calcLightIndex(1.0F);
		this.xo = this.x;
		this.yo = this.y;
		this.zo = this.z;
		this.move(this.xd, this.yd, this.zd);
		if (this.age++ >= this.lifetime || this.onGround) {
			this.remove();
		}
		this.yd -= 0.04 * (double)this.gravity;
		this.xd *= 0.96;
		this.yd *= 0.96;
		this.zd *= 0.96;
	}
}
