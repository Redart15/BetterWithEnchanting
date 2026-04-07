package googy.betterwithenchanting.block;

import googy.betterwithenchanting.interfaces.mixins.IEntityPlayer;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogicRotatable;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;

public class BlockEnchantmentTable extends BlockLogicRotatable {

	public BlockEnchantmentTable(Block<?> block) {
		super(block, Material.stone);
		setBlockBounds(0, 0, 0, 1, 12f / 16, 1);
		block.withEntity(TileEntityEnchantmentTable::new);
	}

	@Override
	public boolean isSolidRender() {
		return false;
	}

	@Override
	public boolean onBlockRightClicked(World world, int x, int y, int z, Player player, Side side, double xPlaced, double yPlaced) {
		if (!world.isClientSide) {
			TileEntityEnchantmentTable tile = (TileEntityEnchantmentTable) world.getTileEntity(x, y, z);
			((IEntityPlayer) player).displayGUIEnchantmentTable(tile);
		}
		return true;
	}
}
