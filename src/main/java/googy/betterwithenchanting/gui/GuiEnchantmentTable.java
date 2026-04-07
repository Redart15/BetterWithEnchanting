package googy.betterwithenchanting.gui;

import googy.betterwithenchanting.BetterWithEnchanting;
import googy.betterwithenchanting.block.TileEntityEnchantmentTable;
import googy.betterwithenchanting.network.packet.PacketEnchantItem;
import googy.betterwithenchanting.inventory.ContainerEnchantmentTable;
import net.minecraft.client.entity.player.PlayerLocalMultiplayer;
import net.minecraft.client.gui.container.ScreenContainerAbstract;
import net.minecraft.core.player.inventory.container.ContainerInventory;
import org.lwjgl.opengl.GL11;

public class GuiEnchantmentTable extends ScreenContainerAbstract {
	TileEntityEnchantmentTable enchantmentTable;
	ContainerEnchantmentTable enchantmentTableContainer;

	int mouseX = 0;
	int mouseY = 0;

	private final int ACTIVE_BUTTON_OFFSET = 166;
	private final int DEACTIVATED_BUTTON_OFFSET = 185;
	private final int MOUSEOVER_BUTTON_OFFSET = 204;

	private final int ACTIVE_LEVEL_OFFSET = 223;
	private final int DEACTIVATED_LEVEL_OFFSET = 239;

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
			if (!isMouseOver) continue;

			boolean canEnchant = enchantmentTableContainer.playerCanEnchant(mc.thePlayer, i);
			if (canEnchant) {
				if (mc.thePlayer instanceof PlayerLocalMultiplayer)
					mc.getSendQueue().addToSendQueue(new PacketEnchantItem(enchantmentTableContainer.containerId, i));
				else
					enchantmentTableContainer.enchantItem(mc.thePlayer, i);
			}
		}
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float delta) {
		GL11.glColor4f(1, 1, 1, 1);

		this.mc.textureManager.loadTexture("/assets/" + BetterWithEnchanting.MOD_ID + "/gui/" + "enchantment_table.png").bind();

		int x = (width - xSize) / 2;
		int y = (height - ySize) / 2;

		// draw background
		drawTexturedModalRect(x, y, 0, 0, xSize, ySize);

		int buttonWidth = 108;
		int buttonHeight = 19;

		int levelSize = 16;

		// draw enchant buttons
		for (int i = 0; i < 3; i++) {
			int buttonX = x + 60;
			int buttonY = y + 14 + buttonHeight * i;

			boolean canEnchant = enchantmentTableContainer.playerCanEnchant(mc.thePlayer, i);
			boolean isMouseOver = (mouseX > buttonX && mouseX < buttonX + buttonWidth) && (mouseY > buttonY && mouseY < buttonY + buttonHeight);

			int offset = DEACTIVATED_BUTTON_OFFSET;
			if (canEnchant) offset = ACTIVE_BUTTON_OFFSET;
			if (canEnchant && isMouseOver) offset = MOUSEOVER_BUTTON_OFFSET;

			drawTexturedModalRect(buttonX, buttonY, 0, offset, buttonWidth, buttonHeight);

			int levelOffset = DEACTIVATED_LEVEL_OFFSET;
			if (canEnchant) levelOffset = ACTIVE_LEVEL_OFFSET;

			drawTexturedModalRect(buttonX, buttonY, levelSize * i, levelOffset, levelSize, levelSize);
		}

		// draw enchant cost
		for (int i = 0; i < 3; i++) {
			if (!enchantmentTableContainer.getSlot(0).hasItem()) continue;
			int color = enchantmentTableContainer.playerCanEnchant(mc.thePlayer, i) ? 16777088 : 6839882;

			String costText = String.valueOf(enchantmentTableContainer.enchantCost[i]);
			int costWidth = mc.font.getStringWidth(costText);

			mc.font.drawStringWithShadow(costText, x + 166 - costWidth, y + 23 + buttonHeight * i, color);
		}

		// draw player's score
		{
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

		GL11.glColor4f(1, 1, 1, 1);
	}
}
