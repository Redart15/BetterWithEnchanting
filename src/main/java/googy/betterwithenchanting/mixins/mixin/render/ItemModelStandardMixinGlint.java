package googy.betterwithenchanting.mixins.mixin.render;

import googy.betterwithenchanting.mixins.MixinsHelperRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.render.item.model.ItemModelStandard;
import net.minecraft.client.render.tessellator.TessellatorGeneral;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ItemModelStandard.class, remap = false)
public abstract class ItemModelStandardMixinGlint {

//	/// glint held item
//	@Inject(method = "renderItem", at = @At("TAIL"))
//	private void renderInWorldGlint(
//		Tessellator tessellator, ItemRenderer renderer, ItemStack itemstack, Entity entity,
//		float brightness, boolean handheldTransform, CallbackInfo ci
//	) {
//		EnchantmentMixins.renderEffect2D(tessellator, Minecraft.getMinecraft().textureManager, itemstack);
//	}
//
//	///  glint 3d item entity
//	@Inject(method = "renderAsItemEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/item/model/ItemModelStandard;renderItem(Lnet/minecraft/client/render/tessellator/Tessellator;Lnet/minecraft/client/render/ItemRenderer;Lnet/minecraft/core/item/ItemStack;Lnet/minecraft/core/entity/Entity;FZ)V", shift = At.Shift.AFTER))
//	private void renderItemGlint(
//		Tessellator tessellator, Entity entity, Random random, ItemStack itemstack, int renderCount, float yaw, float brightness, float partialTick, CallbackInfo ci, @Local ItemStack itemStack
//	){
//		EnchantmentMixins.renderEffect2D(tessellator, Minecraft.getMinecraft().textureManager, itemStack);
//	}
//
//	///  glint flat item entity
//	@Inject(method = "renderAsItemEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/item/model/ItemModelStandard;renderFlat(Lnet/minecraft/client/render/tessellator/Tessellator;Lnet/minecraft/client/render/texture/stitcher/IconCoordinate;)V", shift = At.Shift.AFTER))
//	private void renderFlatGlint(
//		Tessellator tessellator, Entity entity, Random random, ItemStack itemstack, int renderCount, float yaw, float brightness, float partialTick, CallbackInfo ci, @Local ItemStack itemStack
//	){
//		EnchantmentMixins.renderEffectFlat(tessellator, Minecraft.getMinecraft().textureManager, itemStack);
//	}
//
//	@Inject(method = "renderItemIntoGui", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/item/model/ItemModelStandard;renderTexturedQuad(Lnet/minecraft/client/render/tessellator/Tessellator;IILnet/minecraft/client/render/texture/stitcher/IconCoordinate;)V", shift = At.Shift.AFTER))
//	private	void renderIntoGuiGlint(
//		Tessellator tessellator, Font font, TextureManager textureManager, ItemStack itemStack, int x, int y, float brightness, float alpha, CallbackInfo ci
//	){
//		if (EnchantmentContainer.hasEnchantments(itemStack)) {
//			EnchantmentMixins.renderEffectGui(tessellator, Minecraft.getMinecraft().textureManager, itemStack, x, y, 16, 16);
//		}
//	}
//
//	///  push the items in front of the hud
//	@ModifyArg(method = "renderTexturedQuad(Lnet/minecraft/client/render/tessellator/Tessellator;IILnet/minecraft/client/render/texture/stitcher/IconCoordinate;ZZ)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/tessellator/Tessellator;addVertexWithUV(DDDDD)V"), index = 2)
//	private double moveForwardSoGlintDoesNotIntersectWithHUD(double v) {
//		return v + 3.0F;
//	}


	///  capture
	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/item/model/ItemModelStandard;renderSingle(Lnet/minecraft/client/render/tessellator/TessellatorGeneral;Lnet/minecraft/core/entity/Entity;Lnet/minecraft/core/item/ItemStack;ZBIFZ)V", shift = At.Shift.AFTER))
	private void renderItemGlint(
		TessellatorGeneral tessellator, Entity holder, ItemStack itemStack, String displayPosId,
		boolean items3d, int clusterSize, byte lightIndex, float partialTick, boolean leftHanded,
		CallbackInfo ci
	){
		if(items3d){
			///  glint 3d item entity - broken, going to think while trying to figure out what to do.
//			MixinsHelperRenderer.renderEffect2D(tessellator, Minecraft.getMinecraft().textureManager, itemStack, lightIndex);
		}else {
			///  glint flat item entity
			MixinsHelperRenderer.renderEffectFlat(tessellator, Minecraft.getMinecraft().textureManager, itemStack);
		}
	}
}
