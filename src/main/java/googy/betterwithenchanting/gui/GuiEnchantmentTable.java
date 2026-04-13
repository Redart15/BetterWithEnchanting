package googy.betterwithenchanting.gui;

import googy.betterwithenchanting.BetterWithEnchanting;
import googy.betterwithenchanting.block.TileEntityEnchantmentTable;
import googy.betterwithenchanting.network.packet.PacketEnchantItem;
import googy.betterwithenchanting.inventory.ContainerEnchantmentTable;
import googy.betterwithenchanting.utils.EnchantmentUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.PlayerLocalMultiplayer;
import net.minecraft.client.render.tessellator.Tessellator;
import net.minecraft.client.render.texture.Texture;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.Items;
import net.minecraft.core.player.inventory.container.ContainerInventory;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL11;

import java.io.IOException;
import java.io.InputStream;

import static googy.betterwithenchanting.BetterWithEnchanting.MOD_ID;

public class GuiEnchantmentTable extends ScreenFix {
	private static final int ACTIVE_BUTTON_OFFSET = 166;
	private static final int DEACTIVATED_BUTTON_OFFSET = 185;
	private static final int MOUSEOVER_BUTTON_OFFSET = 204;
	private static final int ACTIVE_LEVEL_OFFSET = 223;
	private static final int DEACTIVATED_LEVEL_OFFSET = 239;
	public static final int MAX_STRING_LENGTH = 77;

	public static final int UV_SIZE = 16;
	public static final int ROW_COLUMN_SIZE = 16;
	public static final int GALACTIC_INDEX = 0;
	public static final int GALACTIC_INDEX_NUMBERS = GALACTIC_INDEX + 3 * ROW_COLUMN_SIZE;
	public static final int ILLAGER_INDEX = GALACTIC_INDEX_NUMBERS + ROW_COLUMN_SIZE;
	public static final int ILLAGER_INDEX_NUMBERS = ILLAGER_INDEX + 3 * ROW_COLUMN_SIZE;
	public static final Texture TEXTURE = Minecraft.getMinecraft().textureManager.loadTexture("/assets/" + MOD_ID + "/gui/enchantment_letters.png");


	TileEntityEnchantmentTable enchantmentTable;
	ContainerEnchantmentTable enchantmentTableContainer;
	int mouseX = 0;
	int mouseY = 0;

	private static byte[] charWidth = new byte[65536];

	static {
		InputStream stream = Texture.class.getResourceAsStream("/assets/" + MOD_ID + "/gui/enchant.bin");

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

	public GuiEnchantmentTable(ContainerInventory inventory, TileEntityEnchantmentTable tileEntity) {
		super(new ContainerEnchantmentTable(inventory, tileEntity));
		enchantmentTable = tileEntity;
		enchantmentTableContainer = (ContainerEnchantmentTable) inventorySlots;
	}

	@Override
	public void render(int x, int y, float renderPartialTicks) {
		this.mouseX = x;
		this.mouseY = y;
		super.render(x, y, renderPartialTicks);
	}

	@Override
	public void mouseClicked(int x, int y, int mouseButton) {
		super.mouseClicked(x, y, mouseButton);

		int guiX = (width - xSize) / 2;
		int guiY = (height - ySize) / 2;

		for (int i = 0; i < 3; i++) {
			int buttonWidth = 108;
			int buttonHeight = 19;
			int buttonX = guiX + 60;
			int buttonY = guiY + 14 + buttonHeight * i;

			boolean isMouseOver = (x > buttonX && x < buttonX + buttonWidth) && (y > buttonY && y < buttonY + buttonHeight);
			if (!isMouseOver) {
				continue;
			}

			boolean canEnchant = enchantmentTableContainer.playerCanEnchant(mc.thePlayer, i);
			if (canEnchant) {
				if (mc.thePlayer instanceof PlayerLocalMultiplayer) {
					mc.getSendQueue().addToSendQueue(new PacketEnchantItem(enchantmentTableContainer.containerId, i));
				} else {
					enchantmentTableContainer.enchantItem(mc.thePlayer, i);
				}
				this.enchantmentTable.setRandomLabel();
			}
		}
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float delta) {
		GL11.glColor4f(1, 1, 1, 1);

		this.mc.textureManager.loadTexture("/assets/" + MOD_ID + "/gui/" + "enchantment_table.png").bind();

		int x = (width - xSize) / 2;
		int y = (height - ySize) / 2;

		// draw background
		drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
		int buttonWidth = 108;
		int buttonHeight = 19;
		int levelSize = 16;
		this.renderEnchantButtons(x, y, buttonHeight, buttonWidth, levelSize);
		this.renderCostAndGlyphs(x, y, buttonHeight);
		this.renderScore(x, y);
		GL11.glColor4f(1, 1, 1, 1);
	}

	private void renderEnchantButtons(int x, int y, int buttonHeight, int buttonWidth, int levelSize) {
		// draw enchant buttons
		for (int i = 0; i < 3; i++) {
			int buttonX = x + 60;
			int buttonY = y + 14 + buttonHeight * i;

			boolean canEnchant = enchantmentTableContainer.playerCanEnchant(mc.thePlayer, i);
			boolean isMouseOver = (mouseX > buttonX && mouseX < buttonX + buttonWidth) && (mouseY > buttonY && mouseY < buttonY + buttonHeight);

			int offset = DEACTIVATED_BUTTON_OFFSET;
			if (canEnchant) {
				offset = isMouseOver ? MOUSEOVER_BUTTON_OFFSET : ACTIVE_BUTTON_OFFSET;
			}

			drawTexturedModalRect(buttonX, buttonY, 0, offset, buttonWidth, buttonHeight);

			int levelOffset = DEACTIVATED_LEVEL_OFFSET;
			if (canEnchant) {
				levelOffset = ACTIVE_LEVEL_OFFSET;
			}

			drawTexturedModalRect(buttonX, buttonY, levelSize * i, levelOffset, levelSize, levelSize);
		}
	}

	private void renderCostAndGlyphs(int x, int y, int buttonHeight) {
		if (!enchantmentTableContainer.getSlot(0).hasItem()) {
			return;
		}
		// draw enchant cost
		for (int i = 0; i < 3; i++) {
			boolean canEnchant = enchantmentTableContainer.playerCanEnchant(mc.thePlayer, i) && enchantmentTableContainer.enchantCost[i] > 0;
			int color = canEnchant ? 16777088 : 6839882;
			String costText = String.valueOf(enchantmentTableContainer.enchantCost[i]);
			int costWidth = mc.font.getStringWidth(costText);
			mc.font.drawString(costText, x + 166 - costWidth, y + 23 + buttonHeight * i, color, canEnchant);
			this.drawString(this.enchantmentTable.getAtIndex(i), x + 80, y + 18 + buttonHeight * i, color, this.enchantmentTable.getType(i), canEnchant);
		}
	}

	private void renderScore(int x, int y) {
		int xPos = x + 30;
		int yPos = y + 10;

		String scoreText = "Score:";
		String scoreNumberText = String.valueOf(mc.thePlayer.score);
		int scoreWidth = mc.font.getStringWidth(scoreText);
		int scoreNumberWidth = mc.font.getStringWidth(scoreNumberText);
		int fontHeight = mc.font.fontHeight;

		mc.font.drawStringWithShadow(scoreText, xPos - scoreWidth / 2, yPos, 0xFFFFFF);
		mc.font.drawStringWithShadow(scoreNumberText, xPos - scoreNumberWidth / 2, yPos + fontHeight + 1, 16777088);
	}

	public int drawStringWithShadow(String text, int x, int y, int color, boolean useIllager) {
		return this.drawString(text, x, y, color, useIllager, true);
	}

	public int drawString(String text, int x, int y, int color, boolean useIllager, boolean shadow) {
		if (text == null) {
			return x;
		}
		if (shadow) {
			this.renderString(text, x + 1, y + 1, color, useIllager, true);
		}
		return this.renderString(text, x, y, color, useIllager, false);
	}

	private int renderString(@NotNull String text, int x, int y, int color, boolean useIllager, boolean shadow) {
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
		Minecraft.getMinecraft().textureManager.bindTexture(GuiEnchantmentTable.TEXTURE);
		t.startDrawingQuads();
		float sy = 7.99F;
		float ex = (float) x;
		for (int i = 0; i < text.length(); i++) {
			char c = text.charAt(i);
			int index = GuiEnchantmentTable.getIndex(c, useIllager);
			if (c == ' ' || index < 0) {
				ex += 4.0f;
				continue;
			}
			int iLeft = (charWidth[index] >> 4);
			int iRight = (charWidth[index] & 15) + 1;
			int rowIndex = Math.floorDiv(index, GuiEnchantmentTable.ROW_COLUMN_SIZE);
			int columnIndex = index - rowIndex * GuiEnchantmentTable.ROW_COLUMN_SIZE;
			double len = ((double) iRight - (double) iLeft - 0.02F) / (double) GuiEnchantmentTable.UV_SIZE * sy;
			double uMin = ((double) columnIndex * GuiEnchantmentTable.ROW_COLUMN_SIZE + (double) iLeft) / (GuiEnchantmentTable.ROW_COLUMN_SIZE * GuiEnchantmentTable.UV_SIZE);
			double uMax = ((double) columnIndex * GuiEnchantmentTable.ROW_COLUMN_SIZE + (double) iRight) / (GuiEnchantmentTable.ROW_COLUMN_SIZE * GuiEnchantmentTable.UV_SIZE);
			double vMin = (double) rowIndex / GuiEnchantmentTable.ROW_COLUMN_SIZE;
			double vMax = vMin + 1.0f / GuiEnchantmentTable.ROW_COLUMN_SIZE;
			t.addVertexWithUV(ex, y, 0, uMin, vMin);
			t.addVertexWithUV(ex, y + sy, 0, uMin, vMax);
			t.addVertexWithUV(ex + len, y + sy, 0, uMax, vMax);
			t.addVertexWithUV(ex + len, y, 0, uMax, vMin);
			ex += (float) (len);
			ex += 1.0f;
			if(Math.abs(ex - x) > MAX_STRING_LENGTH){
				break;
			}
		}
		t.draw();
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		return (int) ex;
	}

	public static int getIndex(char c, boolean useIllager) {
		boolean illager = useIllager && BetterWithEnchanting.illagerFont;
		int fontIndex = illager ? ILLAGER_INDEX : GALACTIC_INDEX;
		if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')) {
			return fontIndex + Character.toUpperCase(c) - 'A';
		} else if ((c >= '0' && c <= '9')) {
			fontIndex = illager ? ILLAGER_INDEX_NUMBERS : GALACTIC_INDEX_NUMBERS;
			return fontIndex + c - '0';
		}
		return -1;
	}

	@Override
	public int getTargetSlot(ItemStack stackInSlot, int clickedItemId) {
		if (stackInSlot != null && stackInSlot.getItem().id == Items.DYE.id && stackInSlot.getMetadata() == 4) {
			return 2;
		} else if (EnchantmentUtils.getEnchantments(stackInSlot).isEmpty()) {
			return 1;
		}
		return 0;
	}
}
