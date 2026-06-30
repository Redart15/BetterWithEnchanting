package googy.betterwithenchanting.gui.table;

import googy.betterwithenchanting.render.GlyphRenderer;
import googy.betterwithenchanting.block.TileEntityEnchantmentTable;
import googy.betterwithenchanting.gui.ScreenFix;
import googy.betterwithenchanting.network.MessageEnchantItem;
import googy.betterwithenchanting.api.EnchantmentContainer;
import net.minecraft.client.entity.player.PlayerLocalMultiplayer;
import net.minecraft.client.render.renderer.GLRenderer;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.Items;
import net.minecraft.core.player.inventory.container.ContainerInventory;
import net.minecraft.core.player.inventory.slot.Slot;
import turniplabs.halplibe.helper.network.NetworkHandler;

import static googy.betterwithenchanting.BetterWithEnchanting.*;

public class ScreenEnchantmentTable extends ScreenFix {
	private static final int ACTIVE_BUTTON_OFFSET = 166;
	private static final int DEACTIVATED_BUTTON_OFFSET = 185;
	private static final int MOUSEOVER_BUTTON_OFFSET = 204;
	private static final int ACTIVE_LEVEL_OFFSET = 223;
	private static final int DEACTIVATED_LEVEL_OFFSET = 239;

	TileEntityEnchantmentTable enchantmentTable;
	MenuEnchantmentTable enchantmentTableContainer;
	int mouseX = 0;
	int mouseY = 0;

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
			String label = LABELS[this.enchantmentTableContainer.getLabelIndexAtIndex(i) % LABELS.length];
			GlyphRenderer.drawString(label, x + 80, y + 18 + buttonHeight * i, color, this.enchantmentTableContainer.getType(i), canEnchant);
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
