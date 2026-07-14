package googy.betterwithenchanting.mixins.mixin.enchanted_book;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.gui.hud.component.HudComponentHotbar;
import net.minecraft.core.player.inventory.container.ContainerInventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(value = HudComponentHotbar.class, remap = false)
public class HudComponentHotbarMixinLockSlot {

	@ModifyArg(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/component/HudComponentHotbar;drawOrientedBackgrounds(Lnet/minecraft/client/gui/Gui;IIIIZZ)V"), index = 5)
	public boolean modifyLocking(boolean lock, @Local ContainerInventory inv){
		return inv.currentSlotLocked();
	}
}
