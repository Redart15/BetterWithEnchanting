package googy.betterwithenchanting.mixins.mixin.render;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.hud.component.HudComponentHotbar;
import net.minecraft.client.render.TextureManager;
import net.minecraft.client.render.font.FontRenderer;
import net.minecraft.client.render.item.model.ItemModel;
import net.minecraft.client.render.tessellator.TessellatorGeneral;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.player.inventory.container.ContainerInventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = HudComponentHotbar.class, remap = false)
public class HudComponentHotbarMixinLockSlot {

	@ModifyArg(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/component/HudComponentHotbar;drawOrientedBackgrounds(Lnet/minecraft/client/gui/Gui;IIIIZZ)V"), index = 5)
	public boolean modifyLocking(boolean lock, @Local ContainerInventory inv){
		return inv.currentSlotLocked();
	}


	@Inject(method = "renderInventorySlot", at = @At("HEAD"))
	private void cacheLock(
		Minecraft mc,
		int itemIndex, int x, int y, float partialTick,
		CallbackInfo ci,
		@Share("locked")LocalBooleanRef locked
	){
		locked.set(mc.thePlayer.inventory.locked(itemIndex));
	}

	@WrapOperation(method = "renderInventorySlot", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/item/model/ItemModel;renderGui(Lnet/minecraft/client/render/tessellator/TessellatorGeneral;Lnet/minecraft/core/entity/Entity;Lnet/minecraft/core/item/ItemStack;IIBF)V"))
	private void blockRenderGui(
		ItemModel instance, TessellatorGeneral tessellator,
		Entity holder, ItemStack itemStack,
		int x, int y, byte lightIndex, float partialTick,
		Operation<Void> original,
		@Share("locked") LocalBooleanRef locked
	){
		if(locked.get()) return;
		original.call(instance, tessellator, holder, itemStack, x, y, lightIndex, partialTick);
	}

	@WrapOperation(method = "renderInventorySlot", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/item/model/ItemModel;renderItemOverlayIntoGUI(Lnet/minecraft/client/render/tessellator/TessellatorGeneral;Lnet/minecraft/client/render/font/FontRenderer;Lnet/minecraft/client/render/TextureManager;Lnet/minecraft/core/item/ItemStack;IILjava/lang/String;F)V"))
	private void blockRenderOverlay(
		ItemModel instance, TessellatorGeneral tessellatorGeneral,
		FontRenderer fontRenderer, TextureManager textureManager,
		ItemStack itemStack, int x, int y, String s, float v,
		Operation<Void> original,
		@Share("locked") LocalBooleanRef locked
	){
		if(locked.get()) return;
		original.call(instance, tessellatorGeneral, fontRenderer, textureManager, itemStack, x, y, s, v);
	}

}
