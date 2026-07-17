package googy.betterwithenchanting.gui.book;

import com.mojang.nbt.tags.CompoundTag;
import googy.betterwithenchanting.api.EnchantmentContainer;
import googy.betterwithenchanting.api.EnchantmentStack;
import googy.betterwithenchanting.gui.ScreenFix;
import googy.betterwithenchanting.network.MessageEnchantItem;
import googy.betterwithenchanting.render.GlyphRenderer;
import net.minecraft.client.entity.player.PlayerLocalMultiplayer;
import net.minecraft.client.render.renderer.GLRenderer;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.lang.I18n;
import net.minecraft.core.player.inventory.container.ContainerInventory;
import net.minecraft.core.player.inventory.slot.Slot;
import turniplabs.halplibe.helper.network.NetworkHandler;

import java.util.List;

import static googy.betterwithenchanting.BetterWithEnchanting.MOD_ID;

public class ScreenEnchantmentBook extends ScreenFix {
	// need to adjust
	public static final int BUTTON_WIDTH = 68;
	public static final int BUTTON_HEIGHT = 93;
	public static final int BUTTON_WIDTH_OFFSET = 20;
	public static final int BUTTON_HEIGHT_OFFSET = 9;
	public static final float SCALE_U = 1.0F / 512F;
	public static final float SCALE_V = 1.0F / 512F;

	private static final int[] BUTTONS_HEIGHT_OFFSET = new int[]{194, 287};
	private static final int ACTIVE_BUTTON_OFFSET = 0;
	private static final int DEACTIVATED_BUTTON_OFFSET = ACTIVE_BUTTON_OFFSET + BUTTON_WIDTH;
	private static final int MOUSEOVER_BUTTON_OFFSET = DEACTIVATED_BUTTON_OFFSET + BUTTON_WIDTH;
	private static final int CHOOSEN_BUTTON_OFFSET = MOUSEOVER_BUTTON_OFFSET + BUTTON_WIDTH;
	public static final int LETTER_SIZE = 9;


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
	private final MenuEnchantmentBook menu;
	private final int[] textIndex = new int[2];

	public ScreenEnchantmentBook(ContainerInventory inventory, ItemStack selfStack) {
		super(new MenuEnchantmentBook(inventory, selfStack));
		this.ySize = 194;
		this.menu = (MenuEnchantmentBook) this.inventorySlots;
		this.textIndex[0] = 0;
		this.textIndex[1] = 1;
	}

	@Override
	public void render(int mouseX, int mouseY, float renderPartialTicks) {
		this.mouseX = mouseX;
		this.mouseY = mouseY;
		super.render(mouseX, mouseY, renderPartialTicks);
	}

	@Override
	public void mouseClicked(int clickedX, int clickedY, int mouseButton) {
		super.mouseClicked(clickedX, clickedY, mouseButton);
		if(this.menu.enchantmentWasUsed()){
			return;
		}
		int x = (width - xSize) / 2;
		int y = (height - ySize) / 2;
		for (int i = 0; i < 2; i++) {
			int minWidth = x + BUTTON_WIDTH_OFFSET + BUTTON_WIDTH * i;
			int minHeight = y + BUTTON_HEIGHT_OFFSET;
			if ((clickedX <= minWidth || clickedX >= minWidth + BUTTON_WIDTH) || (clickedY <= minHeight || clickedY >= minHeight + BUTTON_HEIGHT)) {
				continue;
			}
			if(this.menu.playerCanEnchant(i)){
				if(this.mc.thePlayer instanceof PlayerLocalMultiplayer){
//					NetworkHandler.sendToServer(new MessageEnchantItem());
				}else{
					this.menu.enchantItem(this.mc.thePlayer, i);
				}
			}
		}
		this.menu.broadcastChanges();
	}

	private boolean isIsMouseOver(int minWidth, int maxWidth, int minHeight, int maxHeight) {
		boolean hoverEnchantSlot = this.getIsMouseOverSlot(this.inventorySlots.getSlot(0), this.mouseX, this.mouseY);
		boolean mouseOverOption = (this.mouseX > minWidth && this.mouseX < maxWidth) && (this.mouseY > minHeight && this.mouseY < maxHeight);
		return mouseOverOption && !hoverEnchantSlot;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float delta) {
		GLRenderer.setColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.textureManager.loadTexture("/assets/" + MOD_ID + "/gui/" + "enchantment_book2.png").bind();
		int x = (this.width - this.xSize) / 2;
		int y = (this.height - this.ySize) / 2;
		this.drawTexturedModalRect(x, y, 0, 0, this.xSize, this.ySize, SCALE_U, SCALE_V);
		this.renderButtons(x, y);
		this.renderText(x, y);
		GLRenderer.setColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	}

	private void renderButtons(int x, int y) {
		for (int i = 0; i < 2; i++) {
			int heightOffset = BUTTONS_HEIGHT_OFFSET[i];
			int minWidth = x + BUTTON_WIDTH_OFFSET + BUTTON_WIDTH * i;
			int minHeight = y + BUTTON_HEIGHT_OFFSET;
			boolean isIsMouseOver = this.isIsMouseOver(minWidth, minWidth + BUTTON_WIDTH, minHeight, minHeight + BUTTON_HEIGHT);
			boolean canEnchant = this.menu.playerCanEnchant(i);
			int offset = DEACTIVATED_BUTTON_OFFSET;
			if (this.menu.enchantmentWasUsed()) {
				offset = this.menu.enchantmentChoosenOption() == i ? CHOOSEN_BUTTON_OFFSET : DEACTIVATED_BUTTON_OFFSET;
			} else {
				if (canEnchant) {
					offset = isIsMouseOver ? MOUSEOVER_BUTTON_OFFSET : ACTIVE_BUTTON_OFFSET;
				}
			}
			this.drawTexturedModalRect(minWidth, minHeight, offset, heightOffset, BUTTON_WIDTH, BUTTON_HEIGHT, SCALE_U, SCALE_V);
		}
	}

	private void renderText(int x, int y) {
		ItemStack selfStack = menu.selfStack();
		CompoundTag tag = selfStack.getData();
		boolean conTains = tag.containsKey("id");
		Slot enchantItemSlot = this.menu.getSlot(0);
		ItemStack stackInSlot = null;
		if(enchantItemSlot != null){
			stackInSlot = enchantItemSlot.getItemStack();
		}
		for(int i = 0; i < 2; i++){
			int sx = x + BUTTON_WIDTH_OFFSET + 2 + (BUTTON_WIDTH + 3) * i;
			int sy = y + BUTTON_HEIGHT_OFFSET + 2;
			boolean canEnchant = menu.playerCanEnchant(i);
			int color = canEnchant ? 16777088 : 6839882;
			this.renderGlyphs(i, conTains ? tag.getLong("id") : textIndex[i], sx, sy, color, canEnchant);
			this.renderEnchantments(i, canEnchant, stackInSlot, sx, sy + 6 * LETTER_SIZE, color);
		}
	}

	private void renderGlyphs(int option, long id, int sx, int sy, int color, boolean canEnchant) {
		long random = id >> ((option + 1L) * 12L);
		boolean useIllager = (random & 1L) == 0;
		StringBuilder working = new StringBuilder(this.texts[(int) Long.remainderUnsigned(random, this.texts.length)]);
		for (int lines = 0; lines < 4; lines++) {
			int index = GlyphRenderer.drawString(working.toString(), sx, sy + lines * LETTER_SIZE, color, 56, useIllager, canEnchant);
			working.delete(0, index);
		}
	}

	private void renderEnchantments(int option, boolean canEnchant, ItemStack stackInSlot, int sx, int sy, int color) {
		List<EnchantmentStack> enchantmentStackList = menu.getOption(option);
		for (EnchantmentStack enchantmentStack : enchantmentStackList) {
			if (canEnchant && stackInSlot != null && !enchantmentStack.canEnchant(stackInSlot)) {
				continue;
			}
			boolean noLevel = enchantmentStack.minLevel() == enchantmentStack.maxLevel();
			StringBuilder text = new StringBuilder()
				.append(I18n.getInstance().translateKey(enchantmentStack.getEnchantment().translationKeyName()))
				.append(" ")
				.append(noLevel ? "" : String.valueOf(enchantmentStack.getLevel()));
			GLRenderer.pushFrame();
			GLRenderer.modelM4f().scaleAround(0.89f, sx, sy, this.zLevel);
			if (canEnchant) {
				this.drawStringShadow(this.mc.font, text.toString(), sx, sy, color);
			} else {
				this.drawStringNoShadow(this.mc.font, text.toString(), sx, sy, color);
			}
			GLRenderer.popFrame();
			sy += LETTER_SIZE;
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
