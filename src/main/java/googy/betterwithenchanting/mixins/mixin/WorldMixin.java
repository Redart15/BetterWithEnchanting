package googy.betterwithenchanting.mixins.mixin;

import googy.betterwithenchanting.api.Enchantments;
import googy.betterwithenchanting.api.EnchantmentContainer;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.projectile.ProjectileArrow;
import net.minecraft.core.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = World.class, remap = false)
public abstract class WorldMixin {

	@Inject(method = "entityJoinedWorld", at = @At("HEAD"))
	void entityJoinedWorld(Entity entity, CallbackInfoReturnable<Boolean> info) {
		if (entity instanceof ProjectileArrow) {
			ProjectileArrow arrow = (ProjectileArrow) entity;
			if (arrow.owner == null) return;

			int flameLevel = EnchantmentContainer.getLevel(arrow.owner.getHeldItem(), Enchantments.FLAME);
			int fireTime = flameLevel * 20; // level * second

			if (entity.remainingFireTicks < fireTime) {
				entity.remainingFireTicks = fireTime;
			}
		}
	}
}
