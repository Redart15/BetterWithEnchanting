package googy.betterwithenchanting.block;

import googy.betterwithenchanting.interfaces.mixins.IEntityPlayer;
import googy.betterwithenchanting.particle.ParticleGlyph;
import net.minecraft.core.Global;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogicRotatable;
import net.minecraft.core.block.Blocks;
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
	public void animationTick(World world, int x, int y, int z, Random random) {
		for (int ix = -1; ix <= 1; ix++) {
			for (int iz = -1; iz <= 1; iz++) {
				if ((x == 0 && z == 0)
					|| !world.isAirBlock(x + ix, y, z + iz)
					|| !world.isAirBlock(x + ix, y + 1, z + iz)
				) {
					continue; // something obstructing the bookshelf
				}
				this.spawnParticle(world, random, x + ix * 2, y, z + iz * 2, x, y, z);
				this.spawnParticle(world, random, x + ix * 2, y + 1, z + iz * 2, x, y, z);
				this.spawnParticle(world, random, x + ix * 2, y, z + iz, x, y, z);
				this.spawnParticle(world, random, x + ix * 2, y + 1, z + iz, x, y, z);
				this.spawnParticle(world, random, x + ix, y, z + iz * 2, x, y, z);
				this.spawnParticle(world, random, x + ix, y + 1, z + iz * 2, x, y, z);
			}
		}
	}

	public void spawnParticle(World world, Random random, int bx, int by, int bz, int tx, int ty, int tz) {
		boolean pass = random.nextInt(Global.TICKS_PER_SECOND * 4) != 0;
		if (world.getBlockId(bx, by, bz) != Blocks.BOOKSHELF_PLANKS_OAK.id() || pass) {
			return;
		}
		double dx = (double)tx - bx;
		double dy = (double)ty - by + 0.75f;
		double dz = (double)tz - bz;
		double vx = dx / ParticleGlyph.TIME;
		double vz = dz / ParticleGlyph.TIME;
		double vy = dy / ParticleGlyph.TIME + 0.06f;
		world.spawnParticle(
			"enchant",
			bx + 0.5f + (random.nextFloat() - 0.5f) * 2f * vx,
			by + 0.5f + (random.nextFloat() - 0.5f) * 2f * vy,
			bz + 0.5f + (random.nextFloat() - 0.5f) * 2f * vz,
			vx, vy, vz, 0, 30.0f
		);
	}
}
