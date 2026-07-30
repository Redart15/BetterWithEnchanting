package googy.betterwithenchanting.render;

import net.minecraft.client.render.block.model.BlockModelDispatcher;
import net.minecraft.client.render.block.model.generic.BlockModelGeneric;
import net.minecraft.client.render.texture.stitcher.IconCoordinate;
import net.minecraft.client.render.texture.stitcher.TextureRegistry;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogic;
import org.jetbrains.annotations.NotNull;
import org.useless.dragonfly.data.block.BlockModelData;
import org.useless.dragonfly.models.block.StaticBlockModel;

import static googy.betterwithenchanting.BetterWithEnchanting.MOD_ID;

public class BlockModelBookShelf<T extends BlockLogic> extends BlockModelGeneric<T> {
	protected final IconCoordinate[] texCoords = new IconCoordinate[1];
	protected final StaticBlockModel[] models = new StaticBlockModel[texCoords.length];

	public BlockModelBookShelf(@NotNull Block<T> block, @NotNull StaticBlockModel staticModel, String path) {
		super(block, staticModel);
		for(int i = 0; i < texCoords.length; i++) {
			this.texCoords[i] = TextureRegistry.getTexture(MOD_ID + path + i);
			this.models[i] = BlockModelDispatcher.loadDataModel(MOD_ID + path + i).asModel();
		}
	}

	public BlockModelBookShelf(@NotNull Block<T> block, @NotNull BlockModelData staticModel, String path) {
		super(block, staticModel);
		for(int i = 0; i < texCoords.length; i++) {
			this.texCoords[i] = TextureRegistry.getTexture(MOD_ID + path + i);
			this.models[i] = BlockModelDispatcher.loadDataModel(MOD_ID + path + i).asModel();
		}
	}

	@Override
	public @NotNull StaticBlockModel getModelFromData(int data) {
		return this.models[0];
	}
}
