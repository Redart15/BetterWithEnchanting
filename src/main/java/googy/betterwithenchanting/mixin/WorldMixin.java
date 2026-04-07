package googy.betterwithenchanting.mixin;

import googy.betterwithenchanting.enchantment.Enchantments;
import googy.betterwithenchanting.utils.EnchantmentUtils;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.projectile.ProjectileArrow;
import net.minecraft.core.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = World.class, remap = false)
public class WorldMixin {

	@Inject(method = "entityJoinedWorld", at = @At("HEAD"))
	void entityJoinedWorld(Entity entity, CallbackInfoReturnable<Boolean> info) {
		if (entity instanceof ProjectileArrow) {
			ProjectileArrow arrow = (ProjectileArrow) entity;
			if (arrow.owner == null) return;

			int flameLevel = EnchantmentUtils.getLevel(arrow.owner.getHeldItem(), Enchantments.flame);
			int fireTime = flameLevel * 20; // level * second

			if (entity.remainingFireTicks < fireTime) {
				entity.remainingFireTicks = fireTime;
			}
		}
	}
}
