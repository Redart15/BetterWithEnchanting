package googy.betterwithenchanting.mixins;

import googy.betterwithenchanting.api.EnchantmentContainer;
import net.minecraft.client.render.Lighting;
import net.minecraft.client.render.TextureManager;
import net.minecraft.client.render.renderer.*;
import net.minecraft.client.render.tessellator.TessellatorGeneral;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.util.helper.MathHelper;
import org.jetbrains.annotations.NotNull;

import static googy.betterwithenchanting.BetterWithEnchanting.MOD_ID;

public class MixinsHelperRenderer {
	/// Period 3000, offset 1873
	public static final int PERIOD = 3000;
	public static final int OFF_SET = 1873;
	public static final String TEXTURE = "/assets/" + MOD_ID + "/textures/misc/glintB.png";
	/// RGBA (0.5, 0.25, 0.8, 1.0)
	private static final float R = 0.1F;
	private static final float G = 0.1F;
	private static final float B = 0.5F;
	private static final float A = 1.0F;

	private static float getOffset(int i, float factor) {
		int samplingTime = PERIOD + i * OFF_SET;
		return (System.currentTimeMillis() % samplingTime) / ((float) samplingTime) * factor;
	}

	public static void renderEffectFlat(TessellatorGeneral tessellator, TextureManager textureManager, ItemStack itemStack) {
		if (itemStack == null || !EnchantmentContainer.hasEnchantments(itemStack)) {
			return;
		}
		boolean lightning = GLRenderer.globalGetLightEnabled();
		boolean depthMask = GLRenderer.getDepthMask();
		GLRenderer.pushFrame();
		GLRenderer.setShader(Shaders.ITEM);
		GLRenderer.globalSetLightEnabled(false);
		GLRenderer.enableState(State.DEPTH_TEST);
		GLRenderer.setDepthFunc(CompareFunc.EQUAL);
		GLRenderer.setDepthMask(true);
		textureManager.bindTexture(textureManager.loadTexture(TEXTURE));
		GLRenderer.enableState(State.BLEND);
		GLRenderer.setColor4f(R, G, B, A);
		GLRenderer.setBlendFunc(BlendFactor.SRC_COLOR, BlendFactor.ONE);
		for (int i = 0; i < 2; ++i) {
			float c = 0.00390625F;
			float offsetExtra = 20.0f;
			float startingOffset = getOffset(i, 256.0F);
			float shiftY = i == 1 ? -1.0f : 4.0F;
			double u1 = (startingOffset + (double) offsetExtra * shiftY) * c;
			double u2 = (startingOffset + (double) offsetExtra + (double) offsetExtra * shiftY) * c;
			double u3 = (startingOffset + (double) offsetExtra) * c;
			double u4 = startingOffset * c;
			double v12 = (double) offsetExtra * c;
			double v34 = 0.0f * c;
			// frontside
			tessellator.startDrawingQuads();
			tessellator.setNormal(0.0F, 1.0F, 0.0F);
			tessellator.addVertexWithUV(-0.5f, -0.5f, 0.0f, u1, v12);
			tessellator.addVertexWithUV(0.5f, -0.5f, 0.0f, u2, v12);
			tessellator.addVertexWithUV(0.5f, 0.5f, 0.0f, u3, v34);
			tessellator.addVertexWithUV(-0.5f, 0.5f, 0.0f, u4, v34);
			// backside
			tessellator.setNormal(0.0F, 1.0F, 0.0F);
			tessellator.addVertexWithUV(0.5f, 0.5f, 0.0f, u1, v12);
			tessellator.addVertexWithUV(0.5f, -0.5f, 0.0f, u2, v12);
			tessellator.addVertexWithUV(-0.5f, 0.5f, 0.0f, u4, v34);
			tessellator.addVertexWithUV(-0.5f, -0.5f, 0.0f, u3, v34);
			tessellator.draw();
		}
		GLRenderer.setColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		GLRenderer.disableState(State.BLEND);
		GLRenderer.setDepthMask(depthMask);
		GLRenderer.disableState(State.DEPTH_TEST);
		GLRenderer.setDepthFunc(CompareFunc.NOT_EQUAL);
		GLRenderer.globalSetLightEnabled(lightning);
		GLRenderer.popFrame();
	}

	public static void renderEffect2D(TessellatorGeneral tessellator, TextureManager textureManager, ItemStack itemStack, byte lightIndex) {
		if (itemStack == null || !EnchantmentContainer.hasEnchantments(itemStack)) {
			return;
		}
		GLRenderer.pushFrame();
		GLRenderer.setShader(Shaders.ITEM);
		GLRenderer.enableState(State.BLEND);
		GLRenderer.setBlendFunc(BlendFactor.SRC_COLOR, BlendFactor.ONE);
		GLRenderer.setDepthFunc(CompareFunc.EQUAL);
		GLRenderer.globalSetLightEnabled(false);
		textureManager.bindTexture(textureManager.loadTexture("/assets/" + MOD_ID + "/textures/misc/glintB.png"));
		float colorDimmer = 0.76F; // original 0.76
		GLRenderer.pushFrame();
		GLRenderer.setColor4f(colorDimmer * R, colorDimmer * G, colorDimmer * B, A);
		float scaling = 1.0f / 16.0f;
		float offset = getOffset(0, 8.0f);
		GLRenderer.textureM4f().scale(scaling, scaling, scaling);
		GLRenderer.textureM4f().translate(offset, 0.0F, 0.0F);
		GLRenderer.textureM4f().rotate(MathHelper.toRadians(-50.0F), 0.0F, 0.0F, 1.0F);
		renderCoordinate(tessellator, textureManager, 0.0f + offset, 1.0f + offset, 0.0f + offset, 1.0f + offset, lightIndex, 256, 256, 0.0625F);
		GLRenderer.popFrame();
		GLRenderer.pushFrame();
		offset = getOffset(1, 8.0f);
		GLRenderer.textureM4f().scale(scaling, scaling, scaling);
		GLRenderer.textureM4f().translate(-offset, 0.0F, 0.0F);
		GLRenderer.textureM4f().rotate(MathHelper.toRadians(10.0F), 0.0F, 0.0F, 1.0F);
		renderCoordinate(tessellator, textureManager, 0.0f + offset, 1.0f +offset, 0.0f + offset, 1.0f + offset, lightIndex, 256, 256, 0.0625F);
		GLRenderer.setColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		GLRenderer.globalSetLightEnabled(true);
		GLRenderer.disableState(State.BLEND);
		GLRenderer.setDepthFunc(CompareFunc.EQUAL);
		GLRenderer.popFrame();
	}

	public static void renderEffect2DD(TessellatorGeneral tessellator, TextureManager textureManager, ItemStack itemStack, byte lightIndex) {
		if (itemStack == null || !EnchantmentContainer.hasEnchantments(itemStack)) {
			return;
		}
		boolean lightning = GLRenderer.globalGetLightEnabled();
		GLRenderer.pushFrame();
		GLRenderer.setShader(Shaders.ITEM);
		GLRenderer.enableState(State.BLEND);
		GLRenderer.setBlendFunc(BlendFactor.SRC_COLOR, BlendFactor.ONE);
		GLRenderer.setDepthFunc(CompareFunc.EQUAL);
		GLRenderer.globalSetLightEnabled(false);
		float scaling = 1.0f / 16.0f;
		float colorDimmer = 0.76F; // original 0.76
		textureManager.bindTexture(textureManager.loadTexture(TEXTURE));

		GLRenderer.pushFrame();
		float factor = 1024.0f;
		float offset = getOffset(0, factor);
		GLRenderer.setColor4f(colorDimmer * R, colorDimmer * G, colorDimmer * B, A);
		GLRenderer.textureM4f().scale(scaling, scaling, scaling);
		GLRenderer.textureM4f().translate(offset, 0.0F, 0.0F);
		GLRenderer.textureM4f().rotate(MathHelper.toRadians(-50.0F), 0.0F, 0.0F, 1.0F);
		renderCoordinate(tessellator, textureManager, 0.0F + offset, 1.0F + offset, 0.0F + offset, 1.0F + offset, lightIndex, 256, 256, 0.0625F);
//		renderCoordinate(tessellator, textureManager, 0.0F, 1.0F, 0.0F, 1.0F, lightIndex, 256, 256, 0.0625F);
		GLRenderer.popFrame();
		GLRenderer.pushFrame();
		offset = -getOffset(1, factor);
		GLRenderer.setColor4f(colorDimmer * R, colorDimmer * G, colorDimmer * B, A);
		GLRenderer.textureM4f().scale(scaling, scaling, scaling);
		GLRenderer.textureM4f().translate(offset, 0.0F, 0.0F);
		GLRenderer.textureM4f().rotate(MathHelper.toRadians(10.0F), 0.0F, 0.0F, 1.0F);
		renderCoordinate(tessellator, textureManager, 0.0F + offset, 1.0F + offset, 0.0F + offset, 1.0F + offset, lightIndex, 256, 256, 0.0625F);
//		renderCoordinate(tessellator, textureManager, 0.0F, 1.0F, 0.0F, 1.0F, lightIndex, 256, 256, 0.0625F);
		GLRenderer.popFrame();
		GLRenderer.setColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		GLRenderer.globalSetLightEnabled(lightning);
		GLRenderer.disableState(State.BLEND);
		GLRenderer.setDepthFunc(CompareFunc.EQUAL);
		GLRenderer.popFrame();
	}

	protected static void renderCoordinate(
		@NotNull TessellatorGeneral tessellator, TextureManager textureManager,
		float cUMin, float cUMax, float cVMin, float cVMax,
		byte lightIndex, int tileWidth, int tileHeight, float thinckness
	) {
		float halfThinkness = thinckness / 2.0f;
		tessellator.startDrawingQuads();
		tessellator.setLightmapCoord1i(lightIndex);
		tessellator.setNormal(0.0F, 0.0F, -1.0F);
		tessellator.addVertexWithUV(-0.5F, 0.5F, -halfThinkness, cUMin, cVMin);
		tessellator.addVertexWithUV(0.5F, 0.5F, -halfThinkness, cUMax, cVMin);
		tessellator.addVertexWithUV(0.5F, -0.5F, -halfThinkness, cUMax, cVMax);
		tessellator.addVertexWithUV(-0.5F, -0.5F, -halfThinkness, cUMin, cVMax);

		for(int h = 0; h < tileHeight; ++h) {
			double y1 = (double)h / (double) tileHeight;
			double y2 = (double)(h + 1) / (double) tileHeight;
			double vMin = cVMin + tileHeight * y1;
			double vMax = cVMin + tileHeight * y2;
			tessellator.setNormal(0.0F, 1.0F, 0.0F);
			tessellator.addVertexWithUV(-0.5F, 0.5F - y1, -halfThinkness, cUMin, vMin);
			tessellator.addVertexWithUV(-0.5F, 0.5F - y1, halfThinkness, cUMin, vMax);
			tessellator.addVertexWithUV(0.5F, 0.5F - y1, halfThinkness, cUMax, vMax);
			tessellator.addVertexWithUV(0.5F, 0.5F - y1, -halfThinkness, cUMax, vMin);
			tessellator.setNormal(0.0F, -1.0F, 0.0F);
			tessellator.addVertexWithUV(-0.5F, 0.5F - y2, -halfThinkness, cUMin, vMin);
			tessellator.addVertexWithUV(0.5F, 0.5F - y2, -halfThinkness, cUMax, vMin);
			tessellator.addVertexWithUV(0.5F, 0.5F - y2, halfThinkness, cUMax, vMax);
			tessellator.addVertexWithUV(-0.5F, 0.5F - y2, halfThinkness, cUMin, vMax);
		}

		for(int w = 0; w < tileWidth; ++w) {
			double x1 = (double)w / (double) tileWidth;
			double x2 = (double)(w + 1) / (double) tileWidth;
			double uMin = cUMin + tileWidth * x1;
			double uMax = cUMin + tileWidth * x2;
			tessellator.setNormal(-1.0F, 0.0F, 0.0F);
			tessellator.addVertexWithUV(x1 - .5F, 0.5F, -halfThinkness, uMin, cVMin);
			tessellator.addVertexWithUV(x1 - 0.5F, -0.5F, -halfThinkness, uMax, cVMax);
			tessellator.addVertexWithUV(x1 - 0.5F, -0.5F, halfThinkness, uMax, cVMax);
			tessellator.addVertexWithUV(x1 - 0.5F, 0.5F, halfThinkness, uMin, cVMin);
			tessellator.setNormal(1.0F, 0.0F, 0.0F);
			tessellator.addVertexWithUV(x2 - 0.5F, 0.5F, -halfThinkness, uMin, cVMin);
			tessellator.addVertexWithUV(x2 - 0.5F, 0.5F, halfThinkness, uMin, cVMin);
			tessellator.addVertexWithUV(x2 - 0.5F, -0.5F, halfThinkness, uMax, cVMax);
			tessellator.addVertexWithUV(x2 - 0.5F, -0.5F, -halfThinkness, uMax, cVMax);
		}

		tessellator.setNormal(0.0F, 0.0F, 1.0F);
		tessellator.addVertexWithUV(-0.5F, 0.5F, halfThinkness, cUMin, cVMin);
		tessellator.addVertexWithUV(-0.5F, -0.5F, halfThinkness, cUMin, cVMax);
		tessellator.addVertexWithUV(0.5F, -0.5F, halfThinkness, cUMax, cVMax);
		tessellator.addVertexWithUV(0.5F, 0.5F, halfThinkness, cUMax, cVMin);
		tessellator.draw();
	}
}
