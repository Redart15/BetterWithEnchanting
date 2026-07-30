package googy.betterwithenchanting.render;

import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogic;
import org.jetbrains.annotations.NotNull;
import org.useless.dragonfly.data.block.BlockModelData;
import org.useless.dragonfly.models.block.StaticBlockModel;

public class BlockModelEnchantedBookShelf<T extends BlockLogic> extends BlockModelBookShelf<T>{
	public BlockModelEnchantedBookShelf(@NotNull Block<T> block, @NotNull StaticBlockModel staticModel, String path) {
		super(block, staticModel, path);
	}

	public BlockModelEnchantedBookShelf(@NotNull Block<T> block, @NotNull BlockModelData staticModel, String path) {
		super(block, staticModel, path);
	}

	@Override
	public @NotNull StaticBlockModel getModelFromData(int data) {
		return super.getModelFromData((data >> 2) & 15);
	}
}
