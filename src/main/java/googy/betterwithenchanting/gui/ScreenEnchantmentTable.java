package googy.betterwithenchanting.gui;

import googy.betterwithenchanting.BetterWithEnchanting;
import googy.betterwithenchanting.block.TileEntityEnchantmentTable;
import googy.betterwithenchanting.network.MessageEnchantItem;
import googy.betterwithenchanting.api.EnchantmentContainer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.PlayerLocalMultiplayer;
import net.minecraft.client.render.font.RenderIntegerBase;
import net.minecraft.client.render.renderer.GLRenderer;
import net.minecraft.client.render.renderer.State;
import net.minecraft.client.render.tessellator.TessellatorGeneral;
import net.minecraft.client.render.texture.Texture;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.Items;
import net.minecraft.core.player.inventory.container.ContainerInventory;
import net.minecraft.core.player.inventory.slot.Slot;
import org.jetbrains.annotations.NotNull;
import turniplabs.halplibe.helper.network.NetworkHandler;

import java.io.IOException;
import java.io.InputStream;

import static googy.betterwithenchanting.BetterWithEnchanting.*;

public class ScreenEnchantmentTable extends ScreenFix {
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
	MenuEnchantmentTable enchantmentTableContainer;
	int mouseX = 0;
	int mouseY = 0;
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

	public ScreenEnchantmentTable(ContainerInventory inventory, TileEntityEnchantmentTable tileEntity) {
		super(new MenuEnchantmentTable(inventory, tileEntity));
		this.enchantmentTable = tileEntity;
		this.enchantmentTableContainer = (MenuEnchantmentTable) inventorySlots;
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
			boolean canEnchant = this.enchantmentTableContainer.playerCanEnchant(this.mc.thePlayer, i);
			if (canEnchant) {
				if (this.mc.thePlayer instanceof PlayerLocalMultiplayer) {
					NetworkHandler.sendToServer(new MessageEnchantItem(this.enchantmentTableContainer.containerId, i));
				} else {
					this.enchantmentTableContainer.enchantItem(this.mc.thePlayer, i);
				}
				this.enchantmentTable.setRandomLabel();
			}
		}
		this.enchantmentTableContainer.broadcastChanges();
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float delta) {
		GLRenderer.setColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.textureManager.loadTexture("/assets/" + MOD_ID + "/gui/" + "enchantment_table.png").bind();
		int x = (this.width - this.xSize) / 2;
		int y = (this.height - this.ySize) / 2;
		// draw background
		this.drawTexturedModalRect(x, y, 0, 0, this.xSize, this.ySize);
		int buttonWidth = 108;
		int buttonHeight = 19;
		int levelSize = 16;
		this.renderEnchantButtons(x, y, buttonHeight, buttonWidth, levelSize);
		this.renderCostAndGlyphs(x, y, buttonHeight);
		this.renderScore(x, y);
		GLRenderer.setColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	}

	/// draw enchant buttons
	private void renderEnchantButtons(int x, int y, int buttonHeight, int buttonWidth, int levelSize) {
		for (int i = 0; i < 3; i++) {
			int buttonX = x + 60;
			int buttonY = y + 14 + buttonHeight * i;
			boolean canEnchant = this.enchantmentTableContainer.playerCanEnchant(this.mc.thePlayer, i);
			boolean isMouseOver = (this.mouseX > buttonX && this.mouseX < buttonX + buttonWidth) && (this.mouseY > buttonY && this.mouseY < buttonY + buttonHeight);
			int offset = DEACTIVATED_BUTTON_OFFSET;
			if (canEnchant) {
				offset = isMouseOver ? MOUSEOVER_BUTTON_OFFSET : ACTIVE_BUTTON_OFFSET;
			}
			this.drawTexturedModalRect(buttonX, buttonY, 0, offset, buttonWidth, buttonHeight);
			int levelOffset = DEACTIVATED_LEVEL_OFFSET;
			if (canEnchant) {
				levelOffset = ACTIVE_LEVEL_OFFSET;
			}
			this.drawTexturedModalRect(buttonX, buttonY, levelSize * i, levelOffset, levelSize, levelSize);
		}
	}

	private void renderCostAndGlyphs(int x, int y, int buttonHeight) {
		Slot enchantmentSlot = this.enchantmentTableContainer.getSlot(0);
		if (enchantmentSlot == null) {
			return;
		}
		// draw enchant cost
		for (int i = 0; i < 3; i++) {
			boolean canEnchant = this.enchantmentTableContainer.playerCanEnchant(this.mc.thePlayer, i) && this.enchantmentTableContainer.getCostAtIndex(i) > 0;
			int color = canEnchant ? 16777088 : 6839882;
			String costText = String.valueOf(this.enchantmentTableContainer.getCostAtIndex(i));
			int costWidth = this.mc.font.stringWidth(costText) + 1;
			if(canEnchant){
				this.drawStringShadow(this.mc.font, costText, x + 166 - costWidth, y + 23 + buttonHeight * i, color);
			}
			else {
				this.drawStringNoShadow(this.mc.font, costText, x + 166 - costWidth, y + 23 + buttonHeight * i, color);
			}
			this.drawString(LABELS[this.enchantmentTableContainer.getLabelIndexAtIndex(i) % LABELS.length], x + 80, y + 18 + buttonHeight * i, color, this.enchantmentTableContainer.getType(i), canEnchant);
		}
	}

	private void renderScore(int x, int y) {
		int xPos = x + 30;
		int yPos = y + 10;
		String scoreText = "Score:";
		String scoreNumberText = String.valueOf(this.mc.thePlayer.score);
		int scoreWidth = this.mc.font.stringWidth(scoreText);
		int scoreNumberWidth = this.mc.font.stringWidth(scoreNumberText);
		int fontHeight = this.mc.font.getFont().fontHeight() ; // original was 9 now its 8
		this.drawStringShadow(this.mc.font, scoreText, xPos - scoreWidth / 2, yPos, 0xFFFFFF);
		this.drawStringShadow(this.mc.font, scoreNumberText, xPos - scoreNumberWidth / 2, yPos + fontHeight + 1, 16777088);
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
		float red 	= (color >> 16 & 255) / 255.0F;
		float blue 	= (color >> 8 & 255) / 255.0F;
		float green = (color & 255) / 255.0F;
		float alpha = (color >> 24 & 255) / 255.0F;
		GLRenderer.setColor4f(red, blue, green, alpha);
		GLRenderer.enableState(State.DEPTH_TEST);
		TessellatorGeneral t = GLRenderer.getTessellator();
		this.mc.textureManager.bindTexture(ScreenEnchantmentTable.TEXTURE);
		t.startDrawingQuads();
		float sy = 7.99F;
		float ex = x;
		for (int i = 0; i < text.length(); i++) {
			char c = text.charAt(i);
			int index = ScreenEnchantmentTable.getIndex(c, useIllager);
			if (c == ' ' || index < 0) {
				ex += 4.0f;
				continue;
			}
			int iLeft = (CHAR_WIDTH[index] >> 4);
			int iRight = (CHAR_WIDTH[index] & 15) + 1;
			int rowIndex = Math.floorDiv(index, ScreenEnchantmentTable.ROW_COLUMN_SIZE);
			int columnIndex = index - rowIndex * ScreenEnchantmentTable.ROW_COLUMN_SIZE;
			double len = ((double) iRight - (double) iLeft - 0.02F) / ScreenEnchantmentTable.UV_SIZE * sy;
			double uMin = ((double) columnIndex * ScreenEnchantmentTable.ROW_COLUMN_SIZE + iLeft) / (ScreenEnchantmentTable.ROW_COLUMN_SIZE * ScreenEnchantmentTable.UV_SIZE);
			double uMax = ((double) columnIndex * ScreenEnchantmentTable.ROW_COLUMN_SIZE + iRight) / (ScreenEnchantmentTable.ROW_COLUMN_SIZE * ScreenEnchantmentTable.UV_SIZE);
			double vMin = (double) rowIndex / ScreenEnchantmentTable.ROW_COLUMN_SIZE;
			double vMax = vMin + 1.0f / ScreenEnchantmentTable.ROW_COLUMN_SIZE;
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
		GLRenderer.disableState(State.DEPTH_TEST);
		GLRenderer.setColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		return (int) ex;
	}

	public static int getIndex(char c, boolean useIllager) {
		boolean illager = useIllager && BetterWithEnchanting.ILLAGER_FONT;
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
	public int getTargetSlot(ItemStack stackInSlot, int target) {
		if (stackInSlot != null && stackInSlot.getItem().id == Items.DYE.id && stackInSlot.getMetadata() == 4) {
			return 2;
		}
		if (stackInSlot != null && EnchantmentContainer.getEnchantments(stackInSlot).isEmpty()) {
			return 1;
		}
		return 0;
	}
}
