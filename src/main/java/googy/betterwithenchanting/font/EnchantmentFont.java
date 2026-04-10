package googy.betterwithenchanting.font;

import net.minecraft.client.Minecraft;
import net.minecraft.client.render.TextureManager;
import net.minecraft.client.render.tessellator.Tessellator;
import net.minecraft.client.render.texture.Texture;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL11;

import java.io.IOException;
import java.io.InputStream;

import static googy.betterwithenchanting.BetterWithEnchanting.MOD_ID;

public class EnchantmentFont {
	private static final int USIZE = 16;
	private static final int VSIZE = 16;
	private static final int ROWSIZE = 16;
	private static final int COLOMNSIZE = 16;
	private static final int GALACTIC_INDEX = 0;
	private static final int GALACTIC_INDEX_NUMBERS = GALACTIC_INDEX + 3 * COLOMNSIZE;
	private static final int ILLAGER_INDEX = GALACTIC_INDEX_NUMBERS + COLOMNSIZE;
	private static final int ILLAGER_INDEX_NUMBERS = ILLAGER_INDEX + 3 * COLOMNSIZE;
	private static Texture TEXTURE = Minecraft.getMinecraft().textureManager.loadTexture("/assets/" + MOD_ID + "/font/enchantment_letters.png");
	private static byte[] charWidth = new byte[65536];

	static {
		InputStream stream = Texture.class.getResourceAsStream("/assets/" + MOD_ID + "/font/enchant.bin");

		if (stream == null) {
			throw new RuntimeException("Missing font data");
		}
		try {
			stream.read(charWidth, 0, charWidth.length);
			stream.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static Texture getTex() {
		return TEXTURE;
	}

	public static int getIndex(char c, boolean useIllager) {
		int fontIndex = useIllager ? ILLAGER_INDEX : GALACTIC_INDEX;
		if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')) {
			return fontIndex + Character.toUpperCase(c) - 'A';
		} else if ((c >= '0' && c <= '9')) {
			fontIndex = useIllager ? ILLAGER_INDEX_NUMBERS : GALACTIC_INDEX_NUMBERS;
			return fontIndex + c - '0';
		}
		return -1;
	}

	public static double getUMin(int index) {
		return (index - Math.floorDiv(index, ROWSIZE) * ROWSIZE * (double) USIZE) / (double) (COLOMNSIZE * USIZE);
	}

	public static double getUMax(int index) {
		return getUMin(index) + ((double) USIZE) / (double) (COLOMNSIZE * USIZE);
	}

	public static double getVMin(int index) {
		return Math.floorDiv(index, ROWSIZE) * (double) USIZE / (double) (ROWSIZE * VSIZE);
	}

	public static double getVMax(int index) {
		return EnchantmentFont.getVMin(index) + (double) VSIZE / (double) (ROWSIZE * VSIZE);
	}

	public static void drawStringWithShadow(String text, int x, int y, int color, boolean useIllager) {
		EnchantmentFont.drawString(text, x, y, color, useIllager, true);
	}

	public static void drawString(String text, int x, int y, int color, boolean useIllager, boolean shadow) {
		if (text == null) {
			return;
		}
		if (shadow) {
			EnchantmentFont.renderString(text, x + 1, y + 1, color, useIllager, true);
			EnchantmentFont.renderString(text, x, y, color, useIllager, false);
		} else {
			EnchantmentFont.renderString(text, x, y, color, useIllager, false);
		}
	}

	private static void renderString(@NotNull String text, int x, int y, int color, boolean useIllager, boolean shadow) {
		if ((color & -16777216) == 0) {
			color |= -16777216;
		}
		if (shadow) {
			color = (color & 16579836) >> 2 | color & -16777216;
		}
		float red = (float) (color >> 16 & 255) / 255.0F;
		float blue = (float) (color >> 8 & 255) / 255.0F;
		float green = (float) (color & 255) / 255.0F;
		float alpha = (float) (color >> 24 & 255) / 255.0F;
		GL11.glColor4f(red, blue, green, alpha);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		Tessellator t = Tessellator.instance;
		Minecraft.getMinecraft().textureManager.bindTexture(EnchantmentFont.TEXTURE);
		t.startDrawingQuads();
		float sy = 7.99F;
		float ex = (float) x;
		for (int i = 0; i < text.length(); i++) {
			char c = text.charAt(i);
			int index = EnchantmentFont.getIndex(c, useIllager);
			if (c == ' ' || index < 0) {
				ex += 4.0f;
				continue;
			}
			int iLeft = (charWidth[index] >> 4);
			int iRight = (charWidth[index] & 15) + 1;
			int rowIndex = Math.floorDiv(index, ROWSIZE);
			int columnIndex = index - rowIndex * ROWSIZE;
			double len = ((double)iRight - (double)iLeft - 0.02F) / (double) USIZE * sy;
			double uMin = ((double)columnIndex * COLOMNSIZE + (double)iLeft) / (COLOMNSIZE * USIZE);
			double uMax = ((double)columnIndex * COLOMNSIZE + (double)iRight)/ (COLOMNSIZE * USIZE);
			double vMin = (double)rowIndex / ROWSIZE;
			double vMax = vMin + 1.0f / ROWSIZE;
			t.addVertexWithUV(ex, y, 0, uMin, vMin);
			t.addVertexWithUV(ex, y + sy, 0, uMin, vMax);
			t.addVertexWithUV(ex + len, y + sy, 0, uMax, vMax);
			t.addVertexWithUV(ex + len, y, 0, uMax, vMin);
			ex += (len);
			ex += 1.0f;
		}
		t.draw();
		GL11.glEnable(GL11.GL_DEPTH_TEST);
	}
}
