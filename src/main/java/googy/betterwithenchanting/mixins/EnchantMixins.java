package googy.betterwithenchanting.mixins;

import googy.betterwithenchanting.mixins.mixin.accessor.ItemAccessor;
import net.minecraft.client.render.TextureManager;
import net.minecraft.client.render.item.model.ItemModelStandard;
import net.minecraft.client.render.tessellator.Tessellator;
import net.minecraft.core.item.ItemArmor;
import net.minecraft.core.item.ItemStack;
import org.lwjgl.opengl.GL11;

import java.util.Random;

import static googy.betterwithenchanting.BetterWithEnchanting.MOD_ID;

public class EnchantMixins {
	private EnchantMixins(){}

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

    public static boolean shouldNegateDamage(ItemStack stack, int level) {
		Random random = ItemAccessor.getItemRand();
		if (stack.getItem() instanceof ItemArmor && random.nextFloat() < 0.6f) {
            return true;
        }
        return random.nextInt(level) > 0;
    }
}
