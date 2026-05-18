package googy.betterwithenchanting.mixins;

import googy.betterwithenchanting.BetterWithEnchanting;
import googy.betterwithenchanting.api.EnchantmentContainer;
import googy.betterwithenchanting.api.EnchantmentStack;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.render.TextureManager;
import net.minecraft.client.render.item.model.ItemModelStandard;
import net.minecraft.client.render.tessellator.Tessellator;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.lang.I18n;
import net.minecraft.core.net.command.TextFormatting;
import org.lwjgl.opengl.GL11;

import java.util.Comparator;
import java.util.List;

import static googy.betterwithenchanting.BetterWithEnchanting.MOD_ID;

public class EnchantmentMixins {
	private EnchantmentMixins(){}

	public static void renderGlint(ItemModelStandard asThis, Tessellator tessellator, TextureManager textureManager, ItemStack itemStack, int x, int y, float brightness, float alpha) {
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_ALPHA_TEST);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glColor4f(1F, 1F, 1F, 0.3F); // tint + transparency
		GL11.glColorMask(true, true, true, true);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_COLOR, GL11.GL_ONE);
		textureManager.bindTexture(textureManager.loadTexture("/assets/" + MOD_ID + "/textures/misc/glint.png"));
		GL11.glMatrixMode(GL11.GL_TEXTURE);
		GL11.glPushMatrix();
		float t = (System.currentTimeMillis() % 12000L) / 12000.0F;
		GL11.glTranslatef(t, 0, 0);
		GL11.glRotatef(30F, 0, 0, 1);
		tessellator.startDrawingQuads();
		tessellator.addVertexWithUV(x, y + 16, 0.0, 0, 1);
		tessellator.addVertexWithUV(x + 16, y + 16, 0.0, 1, 1);
		tessellator.addVertexWithUV(x + 16, y, 0.0, 1, 0);
		tessellator.addVertexWithUV(x, y, 0.0, 0, 0);
		tessellator.draw();
		GL11.glPopMatrix();
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glDisable(GL11.GL_BLEND);
	}

	public static void devLog(String message){
		if(FabricLoader.getInstance().isDevelopmentEnvironment()){
			BetterWithEnchanting.LOG.info(message);
		}
	}

    public static void getEnchantmentText(ItemStack itemStack, StringBuilder toolTip) {
        List<EnchantmentStack> enchantmentsData = EnchantmentContainer.getEnchantments(itemStack);
        enchantmentsData.sort(Comparator.comparing(e ->e.getEnchantment().id()));
        for (EnchantmentStack enchantmentStack : enchantmentsData) {
            boolean isNull = enchantmentStack.getEnchantment() == null;
            boolean noLevel = isNull || enchantmentStack.minLevel() == enchantmentStack.maxLevel();
            String key = isNull ? "disabled" : enchantmentStack.getTranslationKey() + ".name";
            String enchantLevel = noLevel ? "" : String.valueOf(enchantmentStack.getLevel());
            String enchantName = TextFormatting.formatted(I18n.getInstance().translateKey(key), TextFormatting.CYAN);
            enchantLevel = TextFormatting.formatted(enchantLevel, TextFormatting.CYAN);
            toolTip.append(enchantName).append(" ").append(enchantLevel).append("\n");
        }
    }

	public static double log(double value, double base) {
		return Math.log(value)/Math.log(base);
	}

}
