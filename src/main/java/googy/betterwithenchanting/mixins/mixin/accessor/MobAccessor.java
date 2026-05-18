package googy.betterwithenchanting.mixins.mixin.accessor;

import net.minecraft.core.entity.Mob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Mob.class)
public interface MobAccessor {
	@Accessor
	int getLastDamage();
}
