package googy.betterwithenchanting.mixins.mixin.accessor;

import net.minecraft.core.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Random;

@Mixin(Item.class)
public interface ItemAccessor {
	@Accessor
	static Random getItemRand() {
		throw new UnsupportedOperationException();
	}
}
