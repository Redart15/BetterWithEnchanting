package googy.betterwithenchanting.mixin;

import googy.betterwithenchanting.enchantment.Enchantments;
import googy.betterwithenchanting.utils.EnchantmentUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.controller.PlayerController;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = PlayerController.class, remap = false)
public abstract class PlayerControllerMixin {
	@Shadow
	@Final
	protected Minecraft mc;
	@Shadow
	protected int blockHitDelay;

	@Redirect(method = "continueDestroyBlock", at = @At(value = "FIELD", target = "Lnet/minecraft/client/player/controller/PlayerController;blockHitDelay:I", opcode = Opcodes.GETFIELD))
	public int getBlockHitDelay(PlayerController instance) {
		int quickstrikeLevel = EnchantmentUtils.getLevel(mc.thePlayer.getHeldItem(), Enchantments.quickstrike);
		if (quickstrikeLevel > 0) {
			return 0;
		}

		return this.blockHitDelay;
	}
}
