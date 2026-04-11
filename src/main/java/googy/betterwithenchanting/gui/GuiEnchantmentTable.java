package googy.betterwithenchanting.gui;

import googy.betterwithenchanting.block.TileEntityEnchantmentTable;
import googy.betterwithenchanting.helper.EnchantmentFont;
import googy.betterwithenchanting.network.packet.PacketEnchantItem;
import googy.betterwithenchanting.inventory.ContainerEnchantmentTable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.PlayerLocalMultiplayer;
import net.minecraft.client.gui.container.ScreenContainerAbstract;
import net.minecraft.client.render.tessellator.Tessellator;
import net.minecraft.client.render.texture.Texture;
import net.minecraft.core.player.inventory.container.ContainerInventory;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL11;

import java.io.IOException;
import java.io.InputStream;

import static googy.betterwithenchanting.BetterWithEnchanting.MOD_ID;

public class GuiEnchantmentTable extends ScreenContainerAbstract {
	private static final int ACTIVE_BUTTON_OFFSET = 166;
	private static final int DEACTIVATED_BUTTON_OFFSET = 185;
	private static final int MOUSEOVER_BUTTON_OFFSET = 204;
	private static final int ACTIVE_LEVEL_OFFSET = 223;
	private static final int DEACTIVATED_LEVEL_OFFSET = 239;
	public static final int MAX_STRING_LENGTH = 77;

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
		mouseX = x;
		mouseY = y;
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
			int rowIndex = Math.floorDiv(index, EnchantmentFont.ROWSIZE);
			int columnIndex = index - rowIndex * EnchantmentFont.ROWSIZE;
			double len = ((double) iRight - (double) iLeft - 0.02F) / (double) EnchantmentFont.USIZE * sy;
			double uMin = ((double) columnIndex * EnchantmentFont.COLOMNSIZE + (double) iLeft) / (EnchantmentFont.COLOMNSIZE * EnchantmentFont.USIZE);
			double uMax = ((double) columnIndex * EnchantmentFont.COLOMNSIZE + (double) iRight) / (EnchantmentFont.COLOMNSIZE * EnchantmentFont.USIZE);
			double vMin = (double) rowIndex / EnchantmentFont.ROWSIZE;
			double vMax = vMin + 1.0f / EnchantmentFont.ROWSIZE;
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
}
