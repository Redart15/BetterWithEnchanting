package googy.betterwithenchanting.block;

import googy.betterwithenchanting.item.EnchantmentItems;
import googy.betterwithenchanting.item.ItemEnchantedBook;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogic;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.entity.Mob;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.enums.EnumDropCause;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.Items;
import net.minecraft.core.util.PackedField;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;
import net.minecraft.core.world.pos.TilePosc;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BlockEnchantedBookShelf extends BlockLogic {

	public static final int VARIANTS_POS = 2;
	private static final PackedField VARIANT_MASK = new PackedField(2, 4);

	public BlockEnchantedBookShelf(@NotNull Block<?> block, @NotNull Material material) {
		super(block, material);
	}


	@Override
	public boolean onInteracted(@NotNull World world, @NotNull TilePosc tilePos, @NotNull Player player, @Nullable Side side, double xHit, double yHit) {
		ItemStack stack = player.getHeldItem();
		if (stack == null || stack.itemID == EnchantmentItems.ENCHANTED_BOOK.id) {
			int metadata = world.getBlockData(tilePos);
			ItemStack book = new ItemStack(EnchantmentItems.ENCHANTED_BOOK, 1, (metadata >> 4) & 15);
			((ItemEnchantedBook)book.getItem()).applyEnchantments(book);
			player.inventory.insertItem(book, true);
			if (book.stackSize == 0) {
				if(((metadata - 1) & 3) == 0){
					world.setBlockDataNotify(tilePos, metadata >> 2);
					world.setBlockTypeNotify(tilePos, Blocks.BOOKSHELF_PLANKS_OAK);
					return true;
				}
				world.setBlockDataNotify(tilePos, metadata - 1);
			}
			return true;
		}
		if (stack.itemID == Items.BOOK.id) {
			int metadata = world.getBlockData(tilePos);
			int value = VARIANT_MASK.get(metadata) + 1;
			world.setBlockDataNotify(tilePos, VARIANT_MASK.set(metadata, value));
			return true;
		}
		return false;
	}

	@Override
	public @NotNull ItemStack @Nullable [] getBreakResult(@NotNull World world, @NotNull EnumDropCause dropCause, @NotNull TilePosc tilePos, int data, @Nullable TileEntity tileEntity) {
		switch (dropCause) {
			case SILK_TOUCH, PICK_BLOCK -> {
				return new ItemStack[]{new ItemStack(EnchantmentBlocks.ENCHANTED_BOOKSHELF, 1, 0)};
			}
			default -> {
				return EnchantmentBlocks.ENCHANTED_BOOKSHELF.getBreakResult(world, dropCause, data, tileEntity);
			}
		}
	}

	@Override
	public void onPlacedByMob(@NotNull World world, @NotNull TilePosc tilePos, @NotNull Side side, @NotNull Mob mob, double xHit, double yHit) {
		int metadata = 0;
		if(mob instanceof Player player){
			ItemStack itemStack = player.getHeldItem();
			if(itemStack != null){
				metadata = itemStack.getMetadata();
			}
		}
		this.onPlacedOnSide(world, tilePos, side, xHit, yHit);
		world.setBlockDataNotify(tilePos, metadata);
	}
}
