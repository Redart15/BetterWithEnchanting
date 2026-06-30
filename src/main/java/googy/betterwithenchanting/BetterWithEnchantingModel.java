package googy.betterwithenchanting;

import googy.betterwithenchanting.block.TileEntityEnchantmentTable;
import googy.betterwithenchanting.render.EnchantmentTableRenderer;
import googy.betterwithenchanting.render.ItemModelEnchantmentTable;
import net.minecraft.client.render.EntityRendererDispatcher;
import net.minecraft.client.render.TileEntityRenderDispatcher;
import net.minecraft.client.render.block.color.BlockColorDispatcher;
import net.minecraft.client.render.block.model.BlockModelDispatcher;
import net.minecraft.client.render.block.model.BlockModelStandard;
import net.minecraft.client.render.item.model.ItemModelDispatcher;
import net.minecraft.client.render.item.model.ItemModelStandard;
import net.minecraft.core.item.block.ItemBlock;
import net.minecraft.core.util.helper.Side;
import turniplabs.halplibe.util.ModelEntrypoint;

import static googy.betterwithenchanting.BetterWithEnchanting.*;

public class BetterWithEnchantingModel implements ModelEntrypoint {
	@Override
	public void initBlockModels(BlockModelDispatcher dispatcher) {
		dispatcher.addDispatch(new BlockModelStandard<>( ENCHANTMENT_TABLE)
			.setTex(MOD_ID + ":block/top", Side.TOP)
			.setTex(MOD_ID + ":block/bottom", Side.BOTTOM)
			.setTex(MOD_ID + ":block/side", Side.WEST, Side.NORTH, Side.SOUTH, Side.EAST)
		);
	}

	@Override public void initItemModels(ItemModelDispatcher dispatcher) {
		dispatcher.addDispatch(new ItemModelStandard(SCORE_BOTTLE, true).setIcon(MOD_ID + ":item/score_bottle1"));
		dispatcher.addDispatch(new ItemModelEnchantmentTable((ItemBlock<?>) ENCHANTMENT_TABLE.asItem()));
	}

	@Override public void initTileEntityModels(TileEntityRenderDispatcher dispatcher) {
		dispatcher.assignRenderer(TileEntityEnchantmentTable.class, new EnchantmentTableRenderer());
	}

	@Override public void initEntityModels(EntityRendererDispatcher dispatcher) {/* no need */}
	@Override public void initBlockColors(BlockColorDispatcher dispatcher) {/* no need */}
}
