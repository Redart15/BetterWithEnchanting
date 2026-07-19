package googy.betterwithenchanting.gui.book;

import com.mojang.nbt.tags.CompoundTag;
import googy.betterwithenchanting.api.EnchantmentStack;
import googy.betterwithenchanting.gui.ScreenFix;
import googy.betterwithenchanting.render.GlyphRenderer;
import net.minecraft.client.entity.player.PlayerLocalMultiplayer;
import net.minecraft.client.render.renderer.GLRenderer;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.lang.I18n;
import net.minecraft.core.player.inventory.container.ContainerInventory;
import net.minecraft.core.player.inventory.slot.Slot;

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
		// About Aether
		"Unlike this realm, the hostile paradise is a place where magic is unbound",
		"Do not mistake peace for security. It is often in the quietest moments that the greatest threats gather",
		"The vast plains occasionally reveal the great Mazes lurking beneath",
		"The Valkyr, once roaming the realm, have withdrawn into their extravagant temples",
		"The night was banished by the Great Thief, a mortal who crowned himself a god",
		"Rumors speak of a great foe among the tricksters, one known as Wallace",
		// About Nether
		"Its unclear if the hellish realm, is home to the dead",
		"Avoid any and all liquids in this hellish realm",
		"This realm is both dead and alive",
		"Nethercoal embodies the heat that the realm is known for",
		// About BattleTower
		"Once contained within a realm of their own, they have slowly seeped into ours",
		"What was once a prison has become a battle tower",
		"It great to see some prisons have outlasted their denizens",
		"Most concerning are the prisons that have descended beneath the earth, hidden from sight",
		// About LostTreasures
		"I am still searching for my misplaced wand, I hope noone finds it",
		"My old bat, as destructive as always",
		// Abaout Boons
		"Where there is no struggle there is no strength",
		"The greates wealth is health",
		"Your focus determins your reality",
		"That which does not kill us makes us stronger",
		"To the pure all things are pure",
		"Though hope is frail its hard to kill",
		"Vulnerability is not weakness its our most accurate measure of courage",
		// About Curses
		"A feeble body weakens the mind",
		"Cursed is the man who dies but the evil done by him survives",
		"Too much wit makes the world rotten",
		"Underneath all reason lies delicium and drift",
		"Two can keep secret if one of them is dead",
		"Suppresed emotions eventually erupt",
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
		this.textIndex[1] = 1 << 12;
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
		this.mc.textureManager.loadTexture("/assets/" + MOD_ID + "/gui/" + "enchantment_book.png").bind();
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
		boolean contains = tag.containsKey("id");
		Slot enchantItemSlot = this.menu.getSlot(0);
		ItemStack stackInSlot = null;
		if(enchantItemSlot != null){
			stackInSlot = enchantItemSlot.getItemStack();
		}
		for(int i = 0; i < 2; i++){
			int sx = x + BUTTON_WIDTH_OFFSET + 2 + (BUTTON_WIDTH + 3) * i;
			int sy = y + BUTTON_HEIGHT_OFFSET + 2;
			boolean canEnchant = menu.playerCanEnchant(i);
			boolean optionChoosen = this.menu.enchantmentChoosenOption() == i;
			boolean useFirstColor = this.menu.enchantmentWasUsed() ? optionChoosen : canEnchant;
			int color = useFirstColor ? 16777088 : 6839882;
			this.renderLock(sx, sy, optionChoosen);
			this.renderGlyphs(i, contains ? tag.getLong("id") : textIndex[i], sx, sy, useFirstColor, color);
			this.renderEnchantments(stackInSlot, i, sx, sy + 6 * LETTER_SIZE, useFirstColor, color);
		}
	}

	private void renderGlyphs(int option, long id, int sx, int sy, boolean canEnchant, int color) {
		long random = id >> ((option) * 12L);
		boolean useIllager = (random & 1L) == 0;
		StringBuilder working = new StringBuilder(this.texts[(int) Long.remainderUnsigned(random, this.texts.length)]);
		for (int lines = 0; lines < 4; lines++) {
			int index = GlyphRenderer.drawString(working.toString(), sx, sy + lines * LETTER_SIZE, color, 56, useIllager, canEnchant);
			working.delete(0, index);
		}
	}

	private void renderEnchantments(ItemStack stackInSlot, int option, int sx, int sy, boolean canEnchant, int color) {
		List<EnchantmentStack> enchantmentStackList = menu.getOption(option);
		for (EnchantmentStack enchantmentStack : enchantmentStackList) {
			if (canEnchant && stackInSlot != null && !enchantmentStack.canEnchant(stackInSlot) && !this.menu.enchantmentWasUsed()) {
				continue;
			}
			boolean noLevel = enchantmentStack.minLevel() == enchantmentStack.maxLevel();
			StringBuilder text = new StringBuilder()
				.append(I18n.getInstance().translateKey(enchantmentStack.getEnchantment().translationKeyName()))
				.append(" ")
				.append(noLevel ? "" : String.valueOf(enchantmentStack.getLevel()))
				.append("§r");
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

	private void renderLock(int sx, int sy, boolean optionChoosen) {
		if(optionChoosen){
			this.drawTexturedModalRect(sx, sy, 176, 0, 8, 12);
		}
	}

	@Override
	public int getTargetSlot(ItemStack stackInSlot, int target) {
		if (stackInSlot != null) {
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
