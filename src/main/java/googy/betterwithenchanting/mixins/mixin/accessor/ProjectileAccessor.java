package googy.betterwithenchanting.mixins.mixin.accessor;

import net.minecraft.core.entity.projectile.Projectile;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Projectile.class)
public interface ProjectileAccessor {

	@Accessor
	void setDefaultGravity(float defaultGravity);

	@Accessor
	void setDefaultProjectileSpeed(float defaultProjectileSpeed);

	@Accessor
	float getDefaultGravity();

	@Accessor
	float getDefaultProjectileSpeed();
}
