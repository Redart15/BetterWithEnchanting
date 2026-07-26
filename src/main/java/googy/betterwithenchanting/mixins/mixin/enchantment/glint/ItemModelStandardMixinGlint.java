package googy.betterwithenchanting.mixins.mixin.enchantment.glint;

import googy.betterwithenchanting.mixins.MixinsHelperRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.render.item.model.ItemModelStandard;
import net.minecraft.client.render.tessellator.TessellatorGeneral;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ItemModelStandard.class, remap = false)
public abstract class ItemModelStandardMixinGlint {

	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/item/model/ItemModelStandard;renderSingle(Lnet/minecraft/client/render/tessellator/TessellatorGeneral;Lnet/minecraft/core/entity/Entity;Lnet/minecraft/core/item/ItemStack;ZBIFZ)V", shift = At.Shift.AFTER))
	private void renderItemGlint(
		TessellatorGeneral tessellator, Entity holder, ItemStack itemStack, String displayPosId,
		boolean items3d, int clusterSize, byte lightIndex, float partialTick, boolean leftHanded,
		CallbackInfo ci
	){
		if(items3d){
			///  glint 3d item entity - broken, going to think while trying to figure out what to do.
			MixinsHelperRenderer.renderEffect2DD(tessellator, Minecraft.getMinecraft().textureManager, itemStack, lightIndex);
		}else {
			///  glint flat item entity
			MixinsHelperRenderer.renderEffectFlat(tessellator, Minecraft.getMinecraft().textureManager, itemStack);
		}
	}
}
