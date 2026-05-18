package googy.betterwithenchanting;

import googy.betterwithenchanting.block.TileEntityEnchantmentTable;
import googy.betterwithenchanting.render.EnchantmentTableRenderer;
import googy.betterwithenchanting.render.ItemScoreBottleModel;
import net.minecraft.client.render.EntityRenderDispatcher;
import net.minecraft.client.render.TileEntityRenderDispatcher;
import net.minecraft.client.render.block.color.BlockColorDispatcher;
import net.minecraft.client.render.block.model.BlockModelDispatcher;
import net.minecraft.client.render.block.model.BlockModelStandard;
import net.minecraft.client.render.item.model.ItemModelDispatcher;
import net.minecraft.core.block.BlockLogic;
import net.minecraft.core.util.helper.Side;
import turniplabs.halplibe.helper.ModelHelper;
import turniplabs.halplibe.util.ModelEntrypoint;

import static googy.betterwithenchanting.BetterWithEnchanting.*;

public class BetterWithEnchantingModel implements ModelEntrypoint {
	@Override
	public void initBlockModels(BlockModelDispatcher dispatcher) {
		dispatcher.addDispatch(new BlockModelStandard<BlockLogic>(ENCHANTMENT_TABLE)
			.setTex(BlockModelStandard.BLOCK_TEXTURES, MOD_ID + ":block/top", Side.TOP)
			.setTex(BlockModelStandard.BLOCK_TEXTURES, MOD_ID + ":block/bottom", Side.BOTTOM)
			.setTex(BlockModelStandard.BLOCK_TEXTURES, MOD_ID + ":block/side", Side.WEST, Side.NORTH, Side.SOUTH, Side.EAST)
		);
	}

	@Override public void initItemModels(ItemModelDispatcher dispatcher) {
		dispatcher.addDispatch(new ItemScoreBottleModel(SCORE_BOTTLE, null).setIcon(MOD_ID + ":item/score_bottle1"));
	}

	@Override public void initTileEntityModels(TileEntityRenderDispatcher dispatcher) {
		ModelHelper.setTileEntityModel(TileEntityEnchantmentTable.class, EnchantmentTableRenderer::new);
	}

	@Override public void initEntityModels(EntityRenderDispatcher dispatcher) {/* no need */}
	@Override public void initBlockColors(BlockColorDispatcher dispatcher) {/* no need */}
}
