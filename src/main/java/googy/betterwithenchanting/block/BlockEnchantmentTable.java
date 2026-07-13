package googy.betterwithenchanting.block;

import googy.betterwithenchanting.mixins.interfaces.PlayerAdditionalGui;
import googy.betterwithenchanting.particle.ParticleGlyph;
import net.minecraft.core.Global;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogicRotatable;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.block.material.Materials;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;
import net.minecraft.core.world.pos.TilePos;
import net.minecraft.core.world.pos.TilePosc;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

public class BlockEnchantmentTable extends BlockLogicRotatable {

	public BlockEnchantmentTable(Block<?> block) {
		super(block, Materials.STONE);
		setBlockBounds(0, 0, 0, 1, 12f / 16, 1);
		block.withEntity(TileEntityEnchantmentTable::new);
	}

	@Override
	public boolean isSolidRender() {
		return false;
	}

	@Override
	public boolean onInteracted(@NotNull World world, @NotNull TilePosc tilePos, @NotNull Player player, @Nullable Side side, double xHit, double yHit) {
		if (!world.isClientSide) {
			TileEntity tile = world.getTileEntity(tilePos);
			if(tile instanceof TileEntityEnchantmentTable table){
				((PlayerAdditionalGui) player).displayGuiEnchantmentTable(table);
			}
		}
		return true;
	}

	@Override
	public void animationTick(@NotNull World world, @NotNull TilePosc tilePos, @NotNull Random random) {
		int x = tilePos.x();
		int y = tilePos.y();
		int z = tilePos.z();
		for (int ix = -1; ix <= 1; ix++) {
			for (int iz = -1; iz <= 1; iz++) {
				if ((x == 0 && z == 0)
					|| !world.isAirBlock(new TilePos(x + ix, y, z + iz))
					|| !world.isAirBlock(new TilePos(x + ix, y + 1, z + iz))
				) {
					continue; // something obstructing the bookshelf
				}
				int oppX = ix * 2;

				int oppZ = iz * 2;
				this.spawnParticle(world, random, x + oppX, y, 		z + oppZ, 	x, y, z);
				this.spawnParticle(world, random, x + oppX, y + 1, 	z + oppZ, 	x, y, z);
				this.spawnParticle(world, random, x + oppX, y, 		z + iz, 	x, y, z);
				this.spawnParticle(world, random, x + oppX, y + 1, 	z + iz, 	x, y, z);
				this.spawnParticle(world, random, x + ix, 	y, 		z + oppZ, 	x, y, z);
				this.spawnParticle(world, random, x + ix, 	y + 1, 	z + oppZ, 	x, y, z);
			}
		}
	}

	@SuppressWarnings("java:S107")
	public void spawnParticle(World world, Random random, int bx, int by, int bz, int tx, int ty, int tz) {
		boolean pass = random.nextInt(Global.TICKS_PER_SECOND * 4) != 0;
		Block<?> block = world.getBlockType(new TilePos(bx, by, bz));
		if (block.id() != Blocks.BOOKSHELF_PLANKS_OAK.id() || pass) {
			return;
		}
		double dx = (double)tx - bx;
		double dy = (double)ty - by + 0.5f;
		double dz = (double)tz - bz;
		double vx = dx / ParticleGlyph.TIME;
		double vz = dz / ParticleGlyph.TIME;
		double vy = dy / ParticleGlyph.TIME + 0.06f;
		world.spawnParticle(
			"enchant",
			bx + 0.5f + (random.nextFloat() - 0.5f) * 2f * vx,
			by + 0.5f + (random.nextFloat() - 0.5f) * 2f * vy,
			bz + 0.5f + (random.nextFloat() - 0.5f) * 2f * vz,
			vx, vy, vz, 0, 30.0f, false
		);
	}
}
