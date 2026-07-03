package googy.betterwithenchanting.mixins.mixin.accessor;

import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogicLeavesBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(BlockLogicLeavesBase.class)
public interface BlockLogicLeavesBaseAccessor {
	@Invoker
	Block<?> callGetSapling();
}
