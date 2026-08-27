package googy.betterwithenchanting.gui;

import googy.betterwithenchanting.BetterWithEnchantingClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.hud.HudIngame;
import net.minecraft.client.gui.hud.component.HudComponentMovable;
import net.minecraft.client.gui.hud.component.layout.Layout;
import net.minecraft.client.option.GameSettings;
import net.minecraft.client.render.font.FontRenderer;
import net.minecraft.core.net.command.TextFormatting;

public class HudComponentScore extends HudComponentMovable {

	public static final int X_SIZE = 90;
	public static final int Y_SIZE = 9;

	public HudComponentScore(String key, Layout layout) {
		super(key, X_SIZE, Y_SIZE, layout);
	}

	@Override
	public boolean isVisible() {
		return mc.thePlayer != null
			&& !BetterWithEnchantingClient.HIDE_SCORE.value
			&& GameSettings.IMMERSIVE_MODE.drawHotbar()
			&& !mc.thePlayer.getGamemode().hasInvulnerablePlayer();
	}

	@Override
	public void render(HudIngame hudIngame, int xSizeScreen, int ySizeScreen, float partialTick) {
		int x = this.getLayout().getComponentX(this, xSizeScreen);
		int y = this.getLayout().getComponentY(this, ySizeScreen);
		String score = String.valueOf(Minecraft.getMinecraft().thePlayer.getScore());
		this.renderString(score, x, y);
	}

	@Override
	public void renderPreview(Gui gui, Layout layout, int xSizeScreen, int ySizeScreen) {
		int x = layout.getComponentX(this, xSizeScreen);
		int y = layout.getComponentY(this, ySizeScreen);
		String score = "12739";
		this.renderString(score, x, y);
	}

	private void renderString(String score, int x, int y) {
		int length = Minecraft.getMinecraft().font.stringWidth(score);
		boolean isVertical = this.isVertical();
		int sx = isVertical ? x : x + (X_SIZE - length) / 2;
		int sy = isVertical ? y + (X_SIZE - length) / 2: y;
		if(isVertical){
			FontRenderer font = Minecraft.getMinecraft().font;
			for(int i = 0; i < score.length(); i++){
				font.render(TextFormatting.YELLOW + String.valueOf(score.charAt(i)), sx, sy + Y_SIZE * i).setShadow().setZ(1).call();
			}
		}else{
			Minecraft.getMinecraft().font.render(TextFormatting.YELLOW + score, sx, sy).setShadow().setZ(1).call();
		}
	}

	private boolean isVertical() {
		return BetterWithEnchantingClient.VERTICAL_SCORE.value;
	}

	@Override
	public int getDisplayedXSize() {
		return this.isVertical() ? super.getDisplayedYSize() : super.getDisplayedXSize();
	}

	@Override
	public int getDisplayedYSize() {
		return this.isVertical() ? super.getDisplayedXSize() : super.getDisplayedYSize();
	}

	@Override
	public int getTrueXSize() {
		return this.isVertical() ? super.getTrueYSize() : super.getTrueXSize();
	}

	@Override
	public int getTrueYSize() {
		return this.isVertical() ? super.getTrueXSize() : super.getTrueYSize();
	}

}
