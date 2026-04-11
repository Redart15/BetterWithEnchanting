package googy.betterwithenchanting.helper;

import net.minecraft.client.Minecraft;
import net.minecraft.client.render.texture.Texture;

import static googy.betterwithenchanting.BetterWithEnchanting.MOD_ID;

public class EnchantmentFont {
	public static final int USIZE = 16;
	public static final int VSIZE = 16;
	public static final int ROWSIZE = 16;
	public static final int COLOMNSIZE = 16;
	public static final int GALACTIC_INDEX = 0;
	public static final int GALACTIC_INDEX_NUMBERS = GALACTIC_INDEX + 3 * COLOMNSIZE;
	public static final int ILLAGER_INDEX = GALACTIC_INDEX_NUMBERS + COLOMNSIZE;
	public static final int ILLAGER_INDEX_NUMBERS = ILLAGER_INDEX + 3 * COLOMNSIZE;
	public static final Texture TEXTURE = Minecraft.getMinecraft().textureManager.loadTexture("/assets/" + MOD_ID + "/gui/enchantment_letters.png");

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

}
