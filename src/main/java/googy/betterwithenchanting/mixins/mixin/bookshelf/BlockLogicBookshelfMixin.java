package googy.betterwithenchanting.mixins.mixin.bookshelf;

import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogic;
import net.minecraft.core.block.BlockLogicBookshelf;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.Items;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;
import net.minecraft.core.world.pos.TilePosc;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = BlockLogicBookshelf.class)
public abstract class BlockLogicBookshelfMixin extends BlockLogic {

	private BlockLogicBookshelfMixin(@NotNull Block<?> block, @NotNull Material material) {
		super(block, material);
	}

	@Override
	public boolean onInteracted(@NotNull World world, @NotNull TilePosc tilePos, @NotNull Player player, @Nullable Side side, double xHit, double yHit) {
		ItemStack stack = player.getHeldItem();
		if (stack != null && stack.itemID == Items.BOOK.id) {
			int metadata = world.getBlockData(tilePos);
			metadata = (metadata + 1) & 15;
			world.setBlockDataNotify(tilePos, metadata);
			return true;
		}
		return false;
	}
}
