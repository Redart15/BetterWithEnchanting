package googy.betterwithenchanting.mixins.mixin.enchantment;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import googy.betterwithenchanting.api.EnchantmentContainer;
import googy.betterwithenchanting.api.Enchantments;
import googy.betterwithenchanting.mixins.mixin.accessor.PlayerControllerAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.controller.PlayerController;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = PlayerController.class, remap = false)
public abstract class PlayerControllerMixinQuickStrikeMining {
	@Shadow
	@Final
	protected Minecraft mc;

	@Definition(id = "destroyDelay", field = "Lnet/minecraft/client/player/controller/PlayerController;destroyDelay:I")
	@Expression("this.destroyDelay > 0")
	@ModifyExpressionValue(method = "continueDestroyBlock", at = @At("MIXINEXTRAS:EXPRESSION"))
	private boolean applyQuickStrike(boolean original){
		int quickstrikeLevel = EnchantmentContainer.getLevel(mc.thePlayer.getHeldItem(), Enchantments.QUICKSTRIKE);
		return quickstrikeLevel > 0 ? ((PlayerControllerAccessor)this).getDestroyDelay() > 1 : original;
	}
}
