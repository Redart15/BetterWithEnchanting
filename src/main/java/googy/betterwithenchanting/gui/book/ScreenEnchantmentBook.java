package googy.betterwithenchanting.gui.book;

import googy.betterwithenchanting.api.EnchantmentContainer;
import googy.betterwithenchanting.api.EnchantmentStack;
import googy.betterwithenchanting.gui.ScreenFix;
import googy.betterwithenchanting.render.GlyphRenderer;
import net.minecraft.client.render.renderer.GLRenderer;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.player.inventory.container.ContainerInventory;
import net.minecraft.core.world.World;

import java.util.List;

import static googy.betterwithenchanting.BetterWithEnchanting.MOD_ID;

public class ScreenEnchantmentBook extends ScreenFix {
	private static final int ACTIVE_BUTTON_OFFSET = 1;
	private static final int DEACTIVATED_BUTTON_OFFSET = 2;
	private static final int MOUSEOVER_BUTTON_OFFSET = 3;

	int mouseX = 0;
	int mouseY = 0;

	public ScreenEnchantmentBook(ContainerInventory inventory, World world) {
		super(new MenuEnchantmentBook(inventory, world));
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
			int buttonWidth = 1;
			int buttonHeight = 2;
			int buttonX = guiX + 60;
			int buttonY = guiY + 14 + buttonHeight * i;
			boolean isMouseOver = (x > buttonX && x < buttonX + buttonWidth) && (y > buttonY && y < buttonY + buttonHeight);
			if (!isMouseOver) {
				continue;
			}
		}
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float delta) {
		GLRenderer.setColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.textureManager.loadTexture("/assets/" + MOD_ID + "/gui/" + "enchantment_book.png").bind();
		int x = (this.width - this.xSize) / 2;
		int y = (this.height - this.ySize) / 2;
		// draw background
		this.drawTexturedModalRect(x, y, 0, 0, this.xSize, this.ySize);
		int buttonWidth = 1; // need to adjust
		int buttonHeight = 2;
		this.renderButtons(x, y, buttonHeight, buttonWidth);
		this.renderGlyphs(x, y, buttonHeight, buttonWidth);
		GLRenderer.setColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	}

	private void renderButtons(int x, int y, int buttonHeight, int buttonWidth) {
		for (int i = 0; i < 3; i++) {
			int buttonX = x + 60;
			int buttonY = y + 14 + buttonHeight * i;
			boolean canEnchant = ((MenuEnchantmentBook)this.inventorySlots).playerCanEnchant();
			boolean isMouseOver = (this.mouseX > buttonX && this.mouseX < buttonX + buttonWidth) && (this.mouseY > buttonY && this.mouseY < buttonY + buttonHeight);
			int offset = DEACTIVATED_BUTTON_OFFSET;
			if (canEnchant) {
				offset = isMouseOver ? MOUSEOVER_BUTTON_OFFSET : ACTIVE_BUTTON_OFFSET;
			}
			this.drawTexturedModalRect(buttonX, buttonY, 0, offset, buttonWidth, buttonHeight);
		}
	}

	private void renderGlyphs(int x, int y, int buttonHeight, int buttonWidth) {
		MenuEnchantmentBook menu = (MenuEnchantmentBook) this.inventorySlots;
		for (int i = 0; i < 2; i++) {
			List<EnchantmentStack> enchantmentStackList = menu.getOption(i);
			boolean canEnchant = menu.playerCanEnchant();
			int color = canEnchant ? 16777088 : 6839882;
			String label = "";
			boolean type = false;
			GlyphRenderer.drawString(label, x + 80, y + 18 + buttonHeight * 9999, color, type, canEnchant);
		}
	}

	@Override
	public int getTargetSlot(ItemStack stackInSlot, int target) {
		if (stackInSlot != null && EnchantmentContainer.getEnchantments(stackInSlot).isEmpty()) {
			return 1;
		}
		return 0;
	}
}
