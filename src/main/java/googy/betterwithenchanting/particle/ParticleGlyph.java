package googy.betterwithenchanting.particle;

import googy.betterwithenchanting.helper.EnchantmentFont;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.particle.Particle;
import net.minecraft.client.render.LightmapHelper;
import net.minecraft.client.render.ParticleEngine;
import net.minecraft.client.render.TextureManager;
import net.minecraft.client.render.camera.EntityCamera;
import net.minecraft.client.render.tessellator.Tessellator;
import net.minecraft.core.util.helper.MathHelper;
import net.minecraft.core.world.World;
import org.lwjgl.opengl.GL11;

public class ParticleGlyph extends Particle implements CustomTextureAtlas{
	private static final TextureManager TEXTURE_MANAGER = Minecraft.getMinecraft().textureManager;
	private static final String LETTERS = "abcdefghijklmnopqrstuvwxyz0123456789";
	private final double xa;
	private final double za;
	private int life;
	private final boolean useIllager;
	private final char letter;


	public ParticleGlyph(World world, double x, double y, double z, double xa, double ya, double za) {
		super(world, x, y, z, xa, ya, za);
		this.lifetime = (int)(4.0f / (this.random.nextFloat() * 0.5f));
		this.life = 0;
		this.xd = this.xa = xa;
		this.yd = ya;
		this.zd = this.za = za;
		this.letter = LETTERS.charAt(this.random.nextInt(LETTERS.length()));
		this.useIllager = this.random.nextBoolean();
	}

	@Override
	public int getParticleTexture() {
		return ParticleEngine.ENTITY_PARTICLE_TEXTURE;
	}

	@Override
	public void tick() {
		++this.life;
		if(this.life == this.lifetime){
			this.remove();
		}
		this.yd -= 0.04 * this.gravity;
		this.move(this.xd, this.yd, this.zd);
		this.xd = xa;
		this.zd = za;
	}


	@Override
	public void render(
		Tessellator t, float partialTick,
		double xOff, double yOff, double zOff,
		float xa, float ya, float za, float xa2, float za2
	) {
		t.startDrawingQuads();
		GL11.glDisable(2896);
		TEXTURE_MANAGER.bindTexture(EnchantmentFont.TEXTURE);
		int index = EnchantmentFont.getIndex(this.letter, this.useIllager);
		double uMin = EnchantmentFont.getUMin(index);
		double uMax = EnchantmentFont.getUMax(index);
		double vMin = EnchantmentFont.getVMin(index);
		double vMax = EnchantmentFont.getVMax(index);
		float r = 0.1F * this.size;
		float x = (float)(this.xo + (this.x - this.xo) * (double)partialTick - xOff);
		float y = (float)(this.yo + (this.y - this.yo) * (double)partialTick - yOff);
		float z = (float)(this.zo + (this.z - this.zo) * (double)partialTick - zOff);
		float br = 1.0F;
		if (LightmapHelper.isLightmapEnabled()) {
			t.setLightmapCoord(this.getLightmapCoord(partialTick));
		} else {
			br = this.getBrightness(partialTick);
		}
		t.setColorOpaque_F(this.rCol * br, this.gCol * br, this.bCol * br);
		t.addVertexWithUV(x - xa * r - xa2 * r, y - ya * r, z - za * r - za2 * r, uMax, vMax);
		t.addVertexWithUV(x - xa * r + xa2 * r, y + ya * r, z - za * r + za2 * r, uMax, vMin);
		t.addVertexWithUV(x + xa * r + xa2 * r, y + ya * r, z + za * r + za2 * r, uMin, vMin);
		t.addVertexWithUV(x + xa * r - xa2 * r, y - ya * r, z + za * r - za2 * r, uMin, vMax);
		GL11.glDisable(3042);
		GL11.glEnable(2896);
		t.draw();
	}
}
