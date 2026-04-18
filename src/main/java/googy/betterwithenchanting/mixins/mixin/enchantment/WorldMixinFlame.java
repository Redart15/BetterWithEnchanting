package googy.betterwithenchanting.mixins.mixin.enchantment;

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
public abstract class WorldMixinFlame {

	@Inject(method = "entityJoinedWorld", at = @At("HEAD"))
	private void enchanting$applyFlameToArrows(Entity entity, CallbackInfoReturnable<Boolean> info) {
		if (!(entity instanceof ProjectileArrow)) {
			return;
		}
		ProjectileArrow arrow = (ProjectileArrow) entity;
		if (arrow.owner == null) {
			return;
		}
		int flameLevel = EnchantmentContainer.getLevel(arrow.owner.getHeldItem(), Enchantments.FLAME);
		int fireTime = Math.max(flameLevel * 20, 0); // level * second
		if (entity.remainingFireTicks < fireTime) {
			entity.remainingFireTicks = fireTime;
		}
	}
}
