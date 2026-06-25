package googy.betterwithenchanting.mixins.mixin.render;


import net.minecraft.client.render.item.model.ItemModel;
import net.minecraft.client.render.renderer.GLRenderer;
import net.minecraft.client.render.tessellator.TessellatorGeneral;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ItemModel.class, remap = false)
public class ItemModelMixin {

	@Inject(method = "renderGui", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/item/model/ItemModel;render(Lnet/minecraft/client/render/tessellator/TessellatorGeneral;Lnet/minecraft/core/entity/Entity;Lnet/minecraft/core/item/ItemStack;Ljava/lang/String;ZIBFZ)V"))
	public void moveItemInFrontOfGui(TessellatorGeneral tessellator, Entity holder, ItemStack itemStack, int x, int y, byte lightIndex, float partialTick, CallbackInfo ci){
		GLRenderer.modelM4f().translate(0, 0, 0.001f);
	}
}
