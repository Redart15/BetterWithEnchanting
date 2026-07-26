package googy.betterwithenchanting.mixins.mixin.bookshelf;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import googy.betterwithenchanting.mixins.MixinsHelperLogic;
import it.unimi.dsi.fastutil.objects.ObjectIntImmutablePair;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.world.World;
import net.minecraft.core.world.generate.feature.WorldFeatureLabyrinth;
import net.minecraft.core.world.pos.TilePos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = WorldFeatureLabyrinth.class, remap = false)
public abstract class WorldFeatureLabyrinthMixinAddBottle {

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

}
