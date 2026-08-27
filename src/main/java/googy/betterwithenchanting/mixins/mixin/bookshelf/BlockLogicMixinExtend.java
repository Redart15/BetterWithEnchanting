package googy.betterwithenchanting.mixins.mixin.bookshelf;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.core.block.BlockLogic;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;
import net.minecraft.core.world.pos.TilePosc;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = BlockLogic.class, remap = false)
public abstract class BlockLogicMixinExtend {

	@WrapMethod(method = "onInteracted")
	protected boolean extendedOnInteracted(
		@NotNull World world,
		@NotNull TilePosc tilePos,
		@NotNull Player player,
		@Nullable Side side,
		double xHit, double yHit,
		@NotNull Operation<Boolean> original
	) {
		return original.call(world, tilePos, player, side, xHit, yHit);
	}
}
