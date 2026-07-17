package googy.betterwithenchanting.util;

import googy.betterwithenchanting.block.EnchantmentBlocks;
import it.unimi.dsi.fastutil.ints.IntIntMutablePair;
import net.minecraft.core.Global;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.world.World;
import net.minecraft.core.world.pos.TilePos;
import net.minecraft.core.world.pos.TilePosc;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class FunctionArchive {

	private FunctionArchive(){}

	public static void enchantABookShelf(TilePosc tilePos, World worldObj, Random random, IntIntMutablePair tickAndCount) {
		if (worldObj == null) {
			return;
		}
		if(tickAndCount.leftInt() - tickAndCount.rightInt() <= (20 * Global.TICKS_PER_SECOND)){
			return;
		}
		class Cache { private static final @NotNull TilePos pos = new TilePos();}
		int posX = tilePos.x();
		int posY = tilePos.y();
		int posZ = tilePos.z();
		for(int sample = 3; sample > 0; sample--){
			int xOff = (int) Math.round(boundGaussian(random) * 5.0f);
			int yOff = (int) Math.round(boundGaussian(random) * 5.0f);
			int zOff = (int) Math.round(boundGaussian(random) * 5.0f);
			if(FunctionArchive.convertBookShelf(Cache.pos.set(posX + xOff, posY + yOff, posZ + zOff), worldObj)){
				break;
			}
		}
		tickAndCount.right(tickAndCount.leftInt());
	}


	public static double boundGaussian(Random random) {
		return Math.tanh(random.nextGaussian() / 1.5);
	}

	public static boolean convertBookShelf(TilePosc tilepos,@NotNull World worldObj) {
		Block<?> blockType = worldObj.getBlockType(tilepos);
		if (blockType.id() == Blocks.BOOKSHELF_PLANKS_OAK.id()) {
			int metadata = worldObj.getBlockData(tilepos);
			metadata = metadata << 2;
			metadata += 1;
			worldObj.setBlockTypeNotify(tilepos, EnchantmentBlocks.ENCHANTED_BOOKSHELF_ACTIVE);
			worldObj.setBlockDataNotify(tilepos, metadata);
			return true;
		}
		if (blockType.id() == EnchantmentBlocks.ENCHANTED_BOOKSHELF_ACTIVE.id()) {
			int metadata = worldObj.getBlockData(tilepos);
			if ((metadata & 0b11) == 3) {
				return false;
			}
			metadata += 1;
			worldObj.setBlockDataNotify(tilepos, metadata);
			return true;
		}
		return false;
	}
}
