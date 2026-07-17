package googy.betterwithenchanting.mixins.mixin.accessor;

import net.minecraft.client.util.dispatch.Dispatcher;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(Dispatcher.class)
public interface DispatcherAccessor<T, U> {
	@Mutable
	@Accessor
	Map<T, U> getDispatches();
}
