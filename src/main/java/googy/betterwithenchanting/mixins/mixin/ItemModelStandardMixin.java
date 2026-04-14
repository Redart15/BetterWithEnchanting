package googy.betterwithenchanting.mixins.mixin;

import net.minecraft.client.render.Font;
import net.minecraft.client.render.TextureManager;
import net.minecraft.client.render.item.model.ItemModelStandard;
import net.minecraft.client.render.tessellator.Tessellator;
import net.minecraft.core.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ItemModelStandard.class, remap = false)
public class ItemModelStandardMixin {

	@Inject(method = "renderItemIntoGui", at = @At("TAIL"))
	public void renderGlint(Tessellator tessellator, Font font, TextureManager textureManager, ItemStack itemStack, int x, int y, float brightness, float alpha, CallbackInfo ci) {
//		EnchantMixins.renderGlint((ItemModelStandard) (Object) this, tessellator, textureManager, itemStack, x, y, brightness, alpha);
	}


}
