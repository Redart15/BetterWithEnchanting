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
		if (!EnchantmentContainer.hasEnchantments(itemStack)) {
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
		if (!EnchantmentContainer.hasEnchantments(itemStack)) {
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
		if (!EnchantmentContainer.hasEnchantments(itemStack)) {
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
		float offset = getOffset(0, 256.0f);
		GLRenderer.setColor4f(colorDimmer * R, colorDimmer * G, colorDimmer * B, A);
		GLRenderer.textureM4f().translate(offset, 0.0F, 0.0F);
		GLRenderer.textureM4f().scale(scaling, scaling, scaling);
		GLRenderer.modelM4f().rotate(MathHelper.toRadians(-50.0F), 0.0F, 0.0F, 1.0F);
		renderCoordinate(tessellator, textureManager, 0.0F + offset, 1.0F + offset, 0.0F + offset, 1.0F + offset, lightIndex, 256, 256, 0.0625F);
		GLRenderer.popFrame();
		GLRenderer.pushFrame();
		offset = -getOffset(1, 256.0f);
		GLRenderer.setColor4f(colorDimmer * R, colorDimmer * G, colorDimmer * B, A);
		GLRenderer.textureM4f().translate(offset, 0.0F, 0.0F);
		GLRenderer.textureM4f().scale(scaling, scaling, scaling);
		GLRenderer.modelM4f().rotate(MathHelper.toRadians(10.0F), 0.0F, 0.0F, 1.0F);
		renderCoordinate(tessellator, textureManager, 0.0F + offset, 1.0F + offset, 0.0F + offset, 1.0F + offset, lightIndex, 256, 256, 0.0625F);
		GLRenderer.popFrame();
		GLRenderer.setColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		GLRenderer.globalSetLightEnabled(lightning);
		GLRenderer.disableState(State.BLEND);
		GLRenderer.setDepthFunc(CompareFunc.EQUAL);
		GLRenderer.popFrame();
	}

	private static void renderGlint2D(TessellatorGeneral tessellator, TextureManager textureManager, byte lightIndex) {
		float colorDimmer = 0.86F; // original 0.76
		GLRenderer.pushFrame();
		GLRenderer.setColor4f(colorDimmer * R, colorDimmer * G, colorDimmer * B, A);
		float scaling = 1024.0f / 16.0F;
		GLRenderer.textureM4f().scale(scaling, scaling, scaling);
		float offset = getOffset(0, 8.0f);
		GLRenderer.modelM4f().translate(offset, 0.0F, 0.0F);
		GLRenderer.modelM4f().rotate(MathHelper.toRadians(-50.0F), 0.0F, 0.0F, 1.0F);
		renderCoordinate(tessellator, textureManager, 0.0f, 1.0f, 0.0f, 1.0f, lightIndex, 256, 256, 0.0625F);
//		GLRenderer.popFrame();
//		GLRenderer.pushFrame();
		GLRenderer.textureM4f().scale(scaling, scaling, scaling);
		offset = getOffset(1, 8.0f);
		GLRenderer.modelM4f().translate(-offset, 0.0F, 0.0F);
		GLRenderer.modelM4f().rotate(MathHelper.toRadians(10.0F), 0.0F, 0.0F, 1.0F);
		renderCoordinate(tessellator, textureManager, 0.0f, 1.0f, 0.0f, 1.0f, lightIndex, 256, 256, 0.0625F);
		GLRenderer.popFrame();
		GLRenderer.setColor4f(1.0f, 1.0f, 1.0f, 1.0f);
	}

	protected static void renderCoordinate(
		@NotNull TessellatorGeneral tessellator, TextureManager textureManager,
		float cUMin, float cUMax, float cVMin, float cVMax,
		byte lightIndex, int tileWidth, int tileHeight, float thinckness
	) {
		float halfThinkness = thinckness / 2.0f;
		tessellator.startDrawingQuads();
		tessellator.setLightmapCoord1i(lightIndex);
//		tessellator.setColor1i(color);
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

	@Deprecated
	private static void renderItemIn2D(TessellatorGeneral tessellator, float uMin, float uMax, float vMin, float vMax, int tileWidth, int tileHeight, float thickness) {
		float foon = 0.5F / tileHeight;
		float goon = thickness * (16.0F / tileWidth);
		float pixelWidth = 1.0F / tileWidth;
		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, 0.0F, 1.0F);
		tessellator.addVertexWithUV(0.0F, 0.0F, 0.0F, uMax, vMax);
		tessellator.addVertexWithUV(1.0F, 0.0F, 0.0F, uMin, vMax);
		tessellator.addVertexWithUV(1.0F, 1.0F, 0.0F, uMin, vMin);
		tessellator.addVertexWithUV(0.0F, 1.0F, 0.0F, uMax, vMin);
		tessellator.draw();
		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, 0.0F, -1.0F);
		tessellator.addVertexWithUV(0.0F, 1.0F, -thickness, uMax, vMin);
		tessellator.addVertexWithUV(1.0F, 1.0F, -thickness, uMin, vMin);
		tessellator.addVertexWithUV(1.0F, 0.0F, -thickness, uMin, vMax);
		tessellator.addVertexWithUV(0.0F, 0.0F, -thickness, uMax, vMax);
		tessellator.draw();
		tessellator.startDrawingQuads();
		tessellator.setNormal(-1.0F, 0.0F, 0.0F);

		float uDiff = uMin - uMax;
		float vDiff = vMin - vMax;
		for (int i = 0; i < tileWidth; ++i) {
			float texProgress = i * pixelWidth;
			float u = uMax + uDiff * texProgress - foon;
			tessellator.addVertexWithUV(texProgress, 0.0F, -thickness, u, vMax);
			tessellator.addVertexWithUV(texProgress, 0.0F, 0.0F, u, vMax);
			tessellator.addVertexWithUV(texProgress, 1.0F, 0.0F, u, vMin);
			tessellator.addVertexWithUV(texProgress, 1.0F, -thickness, u, vMin);
		}

		tessellator.draw();
		tessellator.startDrawingQuads();
		tessellator.setNormal(1.0F, 0.0F, 0.0F);

		for (int i = 0; i < tileWidth; ++i) {
			float texProgress = i * pixelWidth;
			float u = uMax + uDiff * texProgress - foon;
			float x = texProgress + goon;
			tessellator.addVertexWithUV(x, 1.0F, -thickness, u, vMin);
			tessellator.addVertexWithUV(x, 1.0F, 0.0F, u, vMin);
			tessellator.addVertexWithUV(x, 0.0F, 0.0F, u, vMax);
			tessellator.addVertexWithUV(x, 0.0F, -thickness, u, vMax);
		}

		tessellator.draw();
		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, 1.0F, 0.0F);

		for (int i = 0; i < tileWidth; ++i) {
			float texProgress = i * pixelWidth;
			float v = vMax + vDiff * texProgress - foon;
			float y = texProgress + goon;
			tessellator.addVertexWithUV(0.0F, y, 0.0F, uMax, v);
			tessellator.addVertexWithUV(1.0F, y, 0.0F, uMin, v);
			tessellator.addVertexWithUV(1.0F, y, -thickness, uMin, v);
			tessellator.addVertexWithUV(0.0F, y, -thickness, uMax, v);
		}

		tessellator.draw();
		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, -1.0F, 0.0F);

		for (int i = 0; i < tileWidth; ++i) {
			float texProgress = i * pixelWidth;
			float v = vMax + vDiff * texProgress - foon;
			tessellator.addVertexWithUV(1.0F, texProgress, 0.0F, uMin, v);
			tessellator.addVertexWithUV(0.0F, texProgress, 0.0F, uMax, v);
			tessellator.addVertexWithUV(0.0F, texProgress, -thickness, uMax, v);
			tessellator.addVertexWithUV(1.0F, texProgress, -thickness, uMin, v);
		}

		tessellator.draw();
	}

	@Deprecated
	public static void renderEffectGui(
		TessellatorGeneral tessellator, TextureManager textureManager, ItemStack itemStack,
		int x, int y, int offX, int offY
	) {
		GLRenderer.setDepthFunc(CompareFunc.GREATER_EQUAL);
		Lighting.disable();
		GLRenderer.setDepthMask(false);
		textureManager.bindTexture(textureManager.loadTexture(TEXTURE));
		GLRenderer.enableState(State.BLEND);
		GLRenderer.setBlendFunc(BlendFactor.DST_COLOR, BlendFactor.DST_COLOR);
		GLRenderer.setColor4f(R, G, B, A);
		for (int i = 0; i < 2; ++i) {
			GLRenderer.setBlendFunc(BlendFactor.SRC_COLOR, BlendFactor.ONE);
			float c = 0.00390625F;
			float startingOffset = getOffset(i, 256.0F);
			float shiftY = i == 1 ? -1.0f : 4.0F;

			float u1 = (startingOffset + offY * shiftY) * c;
			float u2 = (startingOffset + offX + offY * shiftY) * c;
			float u3 = (startingOffset + offX) * c;
			float u4 = (startingOffset + 0.0F) * c;
			float v12 = offY * c;
			float v34 = 0.0f;

			float z = 1.0f;
			tessellator.startDrawingQuads();
			tessellator.addVertexWithUV(x, (double) y + offY, z, u1, v12);
			tessellator.addVertexWithUV((double) x + offX, (double) y + offY, z, u2, v12);
			tessellator.addVertexWithUV((double) x + offX, y, z, u3, v34);
			tessellator.addVertexWithUV(x, y, z, u4, v34);
			tessellator.draw();
		}
		GLRenderer.setColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		GLRenderer.disableState(State.BLEND);
		GLRenderer.setDepthMask(true);
		Lighting.enableLight();
		GLRenderer.setDepthFunc(CompareFunc.LESS_EQUAL);
	}

}
