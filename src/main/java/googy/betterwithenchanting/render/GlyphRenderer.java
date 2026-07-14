package googy.betterwithenchanting.render;

import googy.betterwithenchanting.BetterWithEnchanting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.render.renderer.GLRenderer;
import net.minecraft.client.render.renderer.State;
import net.minecraft.client.render.tessellator.TessellatorGeneral;
import net.minecraft.client.render.texture.Texture;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;

import static googy.betterwithenchanting.BetterWithEnchanting.MOD_ID;

public class GlyphRenderer {
	private static final int MAX_STRING_LENGTH = 77;
	private static final int UV_SIZE = 16;
	private static final int ROW_COLUMN_SIZE = 16;
	private static final int GALACTIC_INDEX = 0;
	private static final int GALACTIC_INDEX_NUMBERS = GALACTIC_INDEX + 3 * ROW_COLUMN_SIZE;
	private static final int ILLAGER_INDEX = GALACTIC_INDEX_NUMBERS + ROW_COLUMN_SIZE;
	private static final int ILLAGER_INDEX_NUMBERS = ILLAGER_INDEX + 3 * ROW_COLUMN_SIZE;
	private static final Texture TEXTURE = Minecraft.getMinecraft().textureManager.loadTexture("/assets/" + MOD_ID + "/gui/enchantment_letters.png");
	private static final byte[] CHAR_WIDTH = new byte[256];

	static {
		InputStream stream = Texture.class.getResourceAsStream("/assets/" + MOD_ID + "/gui/enchant.bin");
		if (stream == null) {
			throw new RuntimeException("Missing font data");
		}
		try {
			stream.read(CHAR_WIDTH, 0, CHAR_WIDTH.length);
			stream.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private GlyphRenderer(){}
	public static void init(){/* load class, to allow it be used */}

	public static int drawString(String text, int x, int y, int color, boolean useIllager, boolean shadow) {
		if (text == null) {
			return x;
		}
		if (shadow) {
			GlyphRenderer.renderString(text, x + 1, y + 1, color, GlyphRenderer.MAX_STRING_LENGTH, useIllager, true);
		}
		return GlyphRenderer.renderString(text, x, y, color, GlyphRenderer.MAX_STRING_LENGTH, useIllager, false);
	}

	public static int drawStringWithShadow(String text, int x, int y, int color, boolean useIllager) {
		return GlyphRenderer.drawString(text, x, y, color, GlyphRenderer.MAX_STRING_LENGTH, useIllager, true);
	}

	public static int drawString(String text, int x, int y, int color, int length, boolean useIllager, boolean shadow) {
		if (text == null) {
			return x;
		}
		if (shadow) {
			GlyphRenderer.renderString(text, x + 1, y + 1, color, length, useIllager, true);
		}
		return GlyphRenderer.renderString(text, x, y, color, length, useIllager, false);
	}

	public static int drawStringWithShadow(String text, int x, int y, int color, int length, boolean useIllager) {
		return GlyphRenderer.drawString(text, x, y, color, useIllager, true);
	}

	private static int renderString(@NotNull String text, int x, int y, int color, int length, boolean useIllager, boolean shadow) {
		if ((color & -16777216) == 0) {
			color |= -16777216;
		}
		if (shadow) {
			color = (color & 16579836) >> 2 | color & -16777216;
		}
		float red 	= (color >> 16 & 255) / 255.0F;
		float blue 	= (color >> 8 & 255) / 255.0F;
		float green = (color & 255) / 255.0F;
		float alpha = (color >> 24 & 255) / 255.0F;
		GLRenderer.setColor4f(red, blue, green, alpha);
		GLRenderer.enableState(State.DEPTH_TEST);
		TessellatorGeneral t = GLRenderer.getTessellator();
		Minecraft.getMinecraft().textureManager.bindTexture(GlyphRenderer.TEXTURE);
		t.startDrawingQuads();
		float sy = 7.99F;
		float ex = x;
		int i = 0;
		for (; i < text.length(); i++) {
			char c = text.charAt(i);
			if(Math.abs(ex - x) > length){
				break;
			}
			int index = GlyphRenderer.getIndex(c, useIllager);
			if (c == ' ' || index < 0) {
				ex += 4.0f;
				continue;
			}
			int iLeft = (GlyphRenderer.CHAR_WIDTH[index] >> 4);
			int iRight = (GlyphRenderer.CHAR_WIDTH[index] & 15) + 1;
			int rowIndex = Math.floorDiv(index, GlyphRenderer.ROW_COLUMN_SIZE);
			int columnIndex = index - rowIndex * GlyphRenderer.ROW_COLUMN_SIZE;
			double len = ((double) iRight - (double) iLeft - 0.02F) / GlyphRenderer.UV_SIZE * sy;
			double uMin = ((double) columnIndex * GlyphRenderer.ROW_COLUMN_SIZE + iLeft) / (GlyphRenderer.ROW_COLUMN_SIZE * GlyphRenderer.UV_SIZE);
			double uMax = ((double) columnIndex * GlyphRenderer.ROW_COLUMN_SIZE + iRight) / (GlyphRenderer.ROW_COLUMN_SIZE * GlyphRenderer.UV_SIZE);
			double vMin = (double) rowIndex / GlyphRenderer.ROW_COLUMN_SIZE;
			double vMax = vMin + 1.0f / GlyphRenderer.ROW_COLUMN_SIZE;
			t.addVertexWithUV(ex, y, 0, uMin, vMin);
			t.addVertexWithUV(ex, y + sy, 0, uMin, vMax);
			t.addVertexWithUV(ex + len, y + sy, 0, uMax, vMax);
			t.addVertexWithUV(ex + len, y, 0, uMax, vMin);
			ex += (float) (len);
			ex += 1.0f;
		}
		t.draw();
		GLRenderer.disableState(State.DEPTH_TEST);
		GLRenderer.setColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		return i;
	}

	public static int getIndex(char c, boolean useIllager) {
		boolean illager = useIllager && BetterWithEnchanting.ILLAGER_FONT;
		int fontIndex = illager ? GlyphRenderer.ILLAGER_INDEX : GlyphRenderer.GALACTIC_INDEX;
		if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')) {
			return fontIndex + Character.toUpperCase(c) - 'A';
		} else if ((c >= '0' && c <= '9')) {
			fontIndex = illager ? GlyphRenderer.ILLAGER_INDEX_NUMBERS : GlyphRenderer.GALACTIC_INDEX_NUMBERS;
			return fontIndex + c - '0';
		}
		return -1;
	}
}
