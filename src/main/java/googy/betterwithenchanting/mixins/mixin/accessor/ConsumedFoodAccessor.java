package googy.betterwithenchanting.mixins.mixin.accessor;

import net.minecraft.core.entity.ConsumedFood;
import net.minecraft.core.entity.Mob;
import net.minecraft.core.item.ItemFood;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ConsumedFood.class)
public interface ConsumedFoodAccessor {
	@Accessor
	Mob getEntity();

	@Mutable
	@Accessor
	void setEntity(Mob entity);

	@Accessor
	ItemFood getFood();

	@Mutable
	@Accessor
	void setFood(ItemFood food);

	@Accessor
	void setHealRemaining(int healRemaining);

	@Accessor
	int getTickCounter();

	@Accessor
	void setTickCounter(int tickCounter);
}
