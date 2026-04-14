package googy.betterwithenchanting.mixins.mixin;

import googy.betterwithenchanting.BetterWithEnchanting;
import googy.betterwithenchanting.api.Enchantments;
import googy.betterwithenchanting.api.EnchantmentContainer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.Mob;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.util.helper.DamageType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = Mob.class, remap = false)
public class MobMixin {
	@Inject(method = "hurt", at = @At(value = "RETURN"))
	public void getHeartsFlashTime(Entity attacker, int damage, DamageType type, CallbackInfoReturnable<Boolean> info) {
		if (!(attacker instanceof Player)) return;
		Player player = (Player) attacker;

		Mob thisLiving = (Mob) (Object) this;
		if (FabricLoader.getInstance().isDevelopmentEnvironment()) {
			BetterWithEnchanting.LOG.info("Victims timer:{}", thisLiving.heartsFlashTime);
		}

		int quickstrikeLevel = EnchantmentContainer.getLevel(player.getHeldItem(), Enchantments.QUICKSTRIKE);
		if (quickstrikeLevel <= 0) return;
		if (thisLiving.heartsFlashTime == thisLiving.heartsHalvesLife) {
			thisLiving.heartsFlashTime = (int) (thisLiving.heartsHalvesLife * 0.75);
		}

	}
}
