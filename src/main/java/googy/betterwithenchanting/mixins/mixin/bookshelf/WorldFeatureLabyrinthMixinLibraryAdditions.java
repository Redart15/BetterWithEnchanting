package googy.betterwithenchanting.mixins.mixin.bookshelf;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import googy.betterwithenchanting.BetterWithEnchanting;
import googy.betterwithenchanting.block.BlockEnchantmentTable;
import googy.betterwithenchanting.block.EnchantmentBlocks;
import googy.betterwithenchanting.block.TileEntityEnchantmentTable;
import googy.betterwithenchanting.mixins.MixinsHelperLogic;
import it.unimi.dsi.fastutil.objects.ObjectIntImmutablePair;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.util.helper.Direction;
import net.minecraft.core.world.World;
import net.minecraft.core.world.generate.feature.WorldFeatureLabyrinth;
import net.minecraft.core.world.pos.TilePos;
import net.minecraft.core.world.pos.TilePosc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;
import java.util.Random;

@Mixin(value = WorldFeatureLabyrinth.class, remap = false)
public abstract class WorldFeatureLabyrinthMixinLibraryAdditions {

	@WrapOperation(method = "generateLibrary", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/world/World;setBlockWithNotify(IIII)Z"))
	private boolean addEnchantedBook(World world, int x, int y, int z, int id, Operation<Boolean> original){
		if(Blocks.BOOKSHELF_PLANKS_OAK.id() == id){
			ObjectIntImmutablePair<Block<?>> blockdata = MixinsHelperLogic.getRandomBlockData(world.rand);
			boolean type = world.setBlockTypeNotify(new TilePos(x, y, z), blockdata.left());
			boolean data = world.setBlockDataNotify(new TilePos(x, y, z), blockdata.rightInt());
			return type && data;
		}
		return original.call(world, x, y, z, id);
	}

	@Inject(method = "generateLibrary", at = @At("TAIL"))
	private void addEnchantingTables(
		World world, Random random,
		int blockX, int blockY, int blockZ, CallbackInfo ci
	){
		if(BetterWithEnchanting.DESTRUCTIBLE){
			return;
		}
		Random rand = new Random(Objects.hash(blockX, blockY, blockZ));
		boolean canPlace = rand.nextInt(BetterWithEnchanting.CHANCE) == 0;
		if (!canPlace) {
			return;
		}
		TilePos tilePos = new TilePos(blockX, blockY - 1, rand.nextBoolean() ? blockZ - 6 : blockZ + 6);
		int blockID = world.getBlockType(new TilePos(tilePos.x(), tilePos.y() - 1, tilePos.z())).id();
		if(blockID == Blocks.AIR.id()){
			return;
		}
		world.setBlockType(tilePos, EnchantmentBlocks.ENCHANTMENT_TABLE);
		TileEntity tileEntity = world.getTileEntity(tilePos);
		if(tileEntity instanceof TileEntityEnchantmentTable table){
			table.setRandomLabel();
		}
	}
}
