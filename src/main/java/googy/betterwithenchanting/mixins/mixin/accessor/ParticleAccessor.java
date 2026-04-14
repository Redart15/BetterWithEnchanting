package googy.betterwithenchanting.mixins.mixin.accessor;

import net.minecraft.client.entity.particle.Particle;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Particle.class)
public interface ParticleAccessor {

	@Accessor
	void setBCol(float bCol);

	@Accessor
	void setGCol(float gCol);

	@Accessor
	void setRCol(float rCol);
}
