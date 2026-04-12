package googy.betterwithenchanting.helper;

import net.minecraft.client.Minecraft;
import net.minecraft.client.render.texture.Texture;

import static googy.betterwithenchanting.BetterWithEnchanting.MOD_ID;

public class EnchantmentFont {
	public static final int USIZE = 16;
	public static final int VSIZE = 16;
	public static final int ROW_SIZE = 16;
	public static final int COLUMN_SIZE = 16;
	public static final int GALACTIC_INDEX = 0;
	public static final int GALACTIC_INDEX_NUMBERS = GALACTIC_INDEX + 3 * COLUMN_SIZE;
	public static final int ILLAGER_INDEX = GALACTIC_INDEX_NUMBERS + COLUMN_SIZE;
	public static final int ILLAGER_INDEX_NUMBERS = ILLAGER_INDEX + 3 * COLUMN_SIZE;
	public static final Texture TEXTURE = Minecraft.getMinecraft().textureManager.loadTexture("/assets/" + MOD_ID + "/gui/enchantment_letters.png");

	private EnchantmentFont(){}

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
		return ((index - Math.floorDiv(index, ROW_SIZE) * ROW_SIZE) * (double) USIZE) / (double) (COLUMN_SIZE * USIZE);
	}

	public static double getUMax(int index) {
		return EnchantmentFont.getUMin(index) + ((double) USIZE) / (double) (COLUMN_SIZE * USIZE);
	}

	public static double getVMin(int index) {
		return Math.floorDiv(index, ROW_SIZE) * (double) USIZE / (double) (ROW_SIZE * VSIZE);
	}

	public static double getVMax(int index) {
		return EnchantmentFont.getVMin(index) + (double) VSIZE / (double) (ROW_SIZE * VSIZE);
	}

}
