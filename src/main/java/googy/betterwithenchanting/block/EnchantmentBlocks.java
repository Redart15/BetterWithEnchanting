package googy.betterwithenchanting.block;

import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogic;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.block.material.Materials;
import net.minecraft.core.block.tag.BlockTags;
import net.minecraft.core.data.tag.Tag;
import net.minecraft.core.sound.BlockSound;
import net.minecraft.core.sound.BlockSounds;
import turniplabs.halplibe.mixin.accessors.BlocksAccessor;


import static googy.betterwithenchanting.BetterWithEnchanting.*;

public class EnchantmentBlocks{
	public static Block<?> ENCHANTMENT_TABLE;
	public static Block<?> ENCHANTED_BOOKSHELF;

	private static Block<?> addBlock(Block<?> block) {
		if (BlocksAccessor.hasInit()) {
			block.init();
			block.getLogic().initializeBlock();
			BlocksAccessor.cacheBlock(block);
		}
		return block;
	}

	private static String formatTranslationKey(String key) {
		return String.format("%s.%s", MOD_ID, key);
	}

	private static String formatName(String name) {
		return String.format("%s:block/%s", MOD_ID, name);
	}

	public static void afterBlockInit() {
		int startingId = BLOCK_ID;
		ENCHANTMENT_TABLE = addBlock(
			Blocks.register(
					formatTranslationKey("enchantment.table"),
					formatName("enchantment_table"),
					startingId++,
					BlockEnchantmentTable::new
				)
				.withSound(new BlockSound("step.stone", "step.stone", 1.0f, 1.0f))
				.withHardness(5)
				.withBlastResistance(1200)
				.withLightEmission(7)
				.withTags(BlockTags.MINEABLE_BY_PICKAXE));

		ENCHANTED_BOOKSHELF = addBlock(
			Blocks.register(
					formatTranslationKey("enchanted.bookshelf"),
					formatName("enchanted_bookshelf"),
					startingId++,
					block -> new BlockEnchantedBookShelf(block, Materials.WOOD)
				)
				.withSound(BlockSounds.WOOD)
				.withHardness(1.5F)
				.withTags(new Tag[]{BlockTags.FENCES_CONNECT, BlockTags.MINEABLE_BY_AXE}));
	}
}
