package googy.betterwithenchanting.mixins.mixin.accessor;

import net.minecraft.client.player.controller.PlayerController;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(PlayerController.class)
public interface PlayerControllerAccessor {
	@Accessor
	int getBlockHitDelay();
}
