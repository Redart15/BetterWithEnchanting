package googy.betterwithenchanting.mixins.mixin.accessor;

import net.minecraft.core.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Random;

@Mixin(Entity.class)
public interface EntityAccessor {
	@Accessor
	boolean isFireImmune();

	@Accessor
	Random getRandom();
}
