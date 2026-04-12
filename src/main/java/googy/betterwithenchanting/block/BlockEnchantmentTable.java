package googy.betterwithenchanting.block;

import googy.betterwithenchanting.interfaces.mixins.IEntityPlayer;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogicRotatable;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;

import java.util.Random;

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

	@Override
	public void animationTick(World world, int x, int y, int z, Random rand) {





		double poxX = (double) x + (double) 0.5F;
		double posY = (double) y + (double) 2.0F + (double) (rand.nextFloat() * 6.0F / 16.0F);
		double posZ = (double) z + (double) 0.5F;
		double f3 = 0.52F;
		double f4 = rand.nextFloat() * 0.6F - 0.3F;
		for(int i = 0; i < 3; i++){
			world.spawnParticle("enchant", poxX - f3, posY, posZ + f4, 0.04F, 0.04F, 0.04F, 0, 256);
		}
	}
}
