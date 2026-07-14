package googy.betterwithenchanting.gui.book;

import googy.betterwithenchanting.api.Enchantment;
import googy.betterwithenchanting.api.EnchantmentContainer;
import googy.betterwithenchanting.api.EnchantmentStack;
import googy.betterwithenchanting.api.Enchantments;
import googy.betterwithenchanting.gui.ScreenFix;
import googy.betterwithenchanting.render.GlyphRenderer;
import net.minecraft.client.render.renderer.GLRenderer;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.lang.I18n;
import net.minecraft.core.player.inventory.container.ContainerInventory;

import java.util.List;

import static googy.betterwithenchanting.BetterWithEnchanting.MOD_ID;

public class ScreenEnchantmentBook extends ScreenFix {
	// need to adjust
	public static final int BUTTON_WIDTH = 69;
	public static final int BUTTON_HEIGHT = 79;
	private static final int ACTIVE_BUTTON_OFFSET = 0;
	private static final int DEACTIVATED_BUTTON_OFFSET = ACTIVE_BUTTON_OFFSET + 69;
	private static final int MOUSEOVER_BUTTON_OFFSET = DEACTIVATED_BUTTON_OFFSET + 69;
	public static final int BUTTON_HEIGHT_OFFSET = 174;

	// Release Wizard rant
	String[] texts = {
		"Knowledge is power",
		"Where there is no struggle there is no strength",
		"Loyalty is rare if you find it keep it",
		"The greates wealth is health",
		"To endure wehat is unendurable is true endurance",
		"Your focus determins your reality",
		"That which does not kill us makes us stronger",
		"Your direction is more important than your speed",
		"Never mistake knowledge for wisdom",
		"The keen spirit seizes the prompt occasion",
		"Every challange you face is an opportunity to grow",
		"Accustom yourself to tireless activity",
		"Beauty maybe dangerous but intelligence is lethal",
		"To the pure all things are pure",
		"Be exalted",
		"Though hope is frail its hard to kill",
		"A feeble body weakens the mind",
		"Cursed is the man who dies but the evil done by him survives",
		"Too much wit makes the world rotten",
		"Vulnerability is not weakness its our most accurate measure of courage",
		"Once exposed a secret loses all its power",
		"Its the flaw that brings out our beauty",
		"Uncertainty is most stressfull feeling",
		"Underneath all reason lies delicium and drift",
		"The strongest have their moment of fatigue",
		"Two can keep secret if one of them is dead",
		"Suppresed emotions eventually erupt",
		""

	};

	int mouseX = 0;
	int mouseY = 0;

	public ScreenEnchantmentBook(ContainerInventory inventory, ItemStack selfStack) {
		super(new MenuEnchantmentBook(inventory, selfStack));
		this.ySize = 174;
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
		this.renderButtons(x, y);
		this.renderText(x, y);
		GLRenderer.setColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	}

	private void renderButtons(int x, int y) {
		for (int i = 0; i < 2; i++) {
			int minWidth = x + 7 + (BUTTON_WIDTH + 24) * i;
			int minHeight = y + 6;
			int maxHeight = minHeight + BUTTON_HEIGHT;
			int maxWidth = minWidth + BUTTON_WIDTH;
			boolean isMouseOver = (this.mouseX > minWidth && this.mouseX < maxWidth) && (this.mouseY > minHeight && this.mouseY < maxHeight);
			boolean canEnchant = ((MenuEnchantmentBook)this.inventorySlots).playerCanEnchant(i);
			int offset = DEACTIVATED_BUTTON_OFFSET;
			if (canEnchant) {
				offset = isMouseOver ? MOUSEOVER_BUTTON_OFFSET : ACTIVE_BUTTON_OFFSET;
			}
			this.drawTexturedModalRect(minWidth, minHeight, offset, BUTTON_HEIGHT_OFFSET, BUTTON_WIDTH, BUTTON_HEIGHT);
		}
	}

	private void renderText(int x, int y) {
		MenuEnchantmentBook menu = (MenuEnchantmentBook) this.inventorySlots;
		int sy = y + 6;
		for (int i = 0; i < 2; i++) {
			int sx = x + 7 + (BUTTON_WIDTH + 24) * i + 3;
			boolean canEnchant = menu.playerCanEnchant(i);
			int color = canEnchant ? 16777088 : 6839882;
			List<EnchantmentStack> enchantmentStackList = menu.getOption(i);
			int count = 0;
			int totalength = 36 - 9 * enchantmentStackList.size();
			for(int k = 0; k < enchantmentStackList.size(); k++){
				int ey = sy + k * 9 + 40 + totalength / 2;
				EnchantmentStack enchantmentStack = enchantmentStackList.get(k);
				count += enchantmentStack.getLevel();
				boolean noLevel = enchantmentStack.minLevel() == enchantmentStack.maxLevel();
				String level = noLevel ? "" : String.valueOf(enchantmentStack.getLevel());
				String name = I18n.getInstance().translateKey(enchantmentStack.getEnchantment().translationKeyName());
				String enchantmentString = String.format("%s %s", name, level);
				GLRenderer.pushFrame();
				GLRenderer.modelM4f().scaleAround(0.94f, sx, ey, this.zLevel);
				if(canEnchant){
					this.drawStringShadow(this.mc.font, enchantmentString , sx, ey, color);
				}else{
					this.drawStringNoShadow(this.mc.font, enchantmentString , sx, ey, color);
				}
				GLRenderer.popFrame();
			}
			// TODO figure out how to do this differently
			String label = this.texts[count % this.texts.length];
			for(int lines = 0; lines < 3 ; lines++){
				int index = GlyphRenderer.drawString(label, sx, sy + 2 + lines * 9, color, 60 , (i % 2) == 1, canEnchant);
				label = label.substring(index);
			}

		}
	}

	@Override
	public int getTargetSlot(ItemStack stackInSlot, int target) {
		if (stackInSlot != null && EnchantmentContainer.getEnchantments(stackInSlot).isEmpty()) {
			return 1;
		}
		return 0;
	}

	@Override
	public void removed() {
		super.removed();
		this.inventorySlots.onCraftGuiClosed(this.mc.thePlayer);
	}

}
