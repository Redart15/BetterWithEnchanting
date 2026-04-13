package googy.betterwithenchanting.particle;

import googy.betterwithenchanting.BetterWithEnchanting;
import googy.betterwithenchanting.mixin.accessor.ParticleAccessor;
import net.minecraft.client.entity.particle.Particle;
import net.minecraft.client.render.texture.stitcher.IconCoordinate;
import net.minecraft.client.render.texture.stitcher.TextureRegistry;
import net.minecraft.core.Global;
import net.minecraft.core.world.World;

import static googy.betterwithenchanting.BetterWithEnchanting.MOD_ID;

public class ParticleGlyph extends Particle {
	private static final String LETTERS = "abcdefghijklmnopqrstuvwxyz0123456789";
	private final double vx;
	private final double vy;
	private final double vz;
	private int life;
	public static final int TIME = Global.TICKS_PER_SECOND * 3;


	public ParticleGlyph(World world, double x, double y, double z, double xa, double ya, double za) {
		super(world, x, y, z, xa, ya, za);
		this.lifetime = TIME;
		this.life = 0;
		this.vx = xa;
		this.vy = ya;
		this.vz = za;
		this.xd = xa + (Math.random() - 0.5) * 2 * (this.vx / ParticleGlyph.TIME);
		this.zd = xa + (Math.random() - 0.5) * 2 * (this.vz / ParticleGlyph.TIME);
		this.yd = ya;
		this.tex = this.setTex();
		this.noPhysics = true;
		if(BetterWithEnchanting.coloredParticle){
			((ParticleAccessor)this).setRCol(this.random.nextFloat());
			((ParticleAccessor)this).setGCol(this.random.nextFloat());
			((ParticleAccessor)this).setBCol(this.random.nextFloat());
		}
	}

	private IconCoordinate setTex() {
		String path = new StringBuilder(MOD_ID)
			.append(":particle/")
			.append(this.random.nextBoolean() && BetterWithEnchanting.illagerFont ? "ill_" : "svg_")
			.append(LETTERS.charAt(this.random.nextInt(LETTERS.length())))
			.toString();
		return TextureRegistry.getTexture(path);
	}

	@Override
	public void tick() {
		this.xo = this.x;
		this.yo = this.y;
		this.zo = this.z;
		this.move(this.xd, this.yd, this.zd);
		++this.life;
		if(this.life == this.lifetime){
			this.remove();
		}
		this.yd -= 2* this.vy / ParticleGlyph.TIME;
		this.xd = this.vx;
		this.zd = this.vz;
	}
}
