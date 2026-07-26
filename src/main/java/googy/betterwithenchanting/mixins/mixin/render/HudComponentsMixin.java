package googy.betterwithenchanting.mixins.mixin.render;

import googy.betterwithenchanting.BetterWithEnchantingClient;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.hud.component.HudComponents;
import org.spongepowered.asm.mixin.Mixin;

@Environment(EnvType.CLIENT)
@Mixin(value = HudComponents.class, remap = false)
public abstract class HudComponentsMixin {
	static {
		BetterWithEnchantingClient.hudComponentRegistry();
	}
}
