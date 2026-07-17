package googy.betterwithenchanting.block;

import com.mojang.nbt.tags.CompoundTag;
import com.mojang.nbt.tags.ListTag;
import googy.betterwithenchanting.api.Enchantment;
import googy.betterwithenchanting.api.EnchantmentContainer;
import googy.betterwithenchanting.api.EnchantmentStack;
import googy.betterwithenchanting.api.Enchantments;
import googy.betterwithenchanting.item.EnchantmentItems;
import net.minecraft.core.WeightedRandomBag;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogic;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.enums.EnumDropCause;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.Items;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;
import net.minecraft.core.world.pos.TilePosc;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class BlockEnchantedBookShelf extends BlockLogic {

	public static final int VARIANTS = 15;
	public static final int VARIANTS_POS = 2;

	public BlockEnchantedBookShelf(@NotNull Block<?> block, @NotNull Material material) {
		super(block, material);
	}


	@Override
	public boolean onInteracted(@NotNull World world, @NotNull TilePosc tilePos, @NotNull Player player, @Nullable Side side, double xHit, double yHit) {
		ItemStack stack = player.getHeldItem();
		if(stack == null || stack.itemID == EnchantmentItems.ENCHANTED_BOOK.id){
			int metadata = world.getBlockData(tilePos);
			if (this.canGiveBook(metadata)) {
				this.givePlayerEnchantedBook(world, tilePos, player, metadata);
				return true;
			}
			return false;
		}
		if (stack.itemID == Items.BOOK.id) {
			int metadata = this.cycleVariantData(world, tilePos);
			world.setBlockDataNotify(tilePos, metadata);
			return true;
		}
		return false;
	}

	private int cycleVariantData(@NotNull World world, @NotNull TilePosc tilePos) {
		int metadata = world.getBlockData(tilePos);
		int value = (((metadata >> VARIANTS_POS) & VARIANTS) + 1) % (VARIANTS + 1);
		int mask = ((1 << 4) - 1) << VARIANTS_POS;
		return (metadata & ~mask) | ((value << VARIANTS_POS) & mask);
	}

	private boolean canGiveBook(int metadata) {
		return (metadata >> 7) == 0;
	}

	private void givePlayerEnchantedBook(@NotNull World world, @NotNull TilePosc tilePos, @NotNull Player player, int metadata) {
		ItemStack book = this.generateEnchantedBook(world, metadata);
		this.updateBookCount(world, tilePos, player, metadata, book);
	}

	private @NotNull ItemStack generateEnchantedBook(@NotNull World world, int metadata) {
		ItemStack book = new ItemStack(EnchantmentItems.ENCHANTED_BOOK, 1, (metadata >> 4) & 15);
		WeightedRandomBag<EnchantmentStack> bag = this.generateEnchantmentBag();
		this.applyEnchantments(book, world.rand, bag);
		return book;
	}

	private @NotNull WeightedRandomBag<EnchantmentStack> generateEnchantmentBag() {
		WeightedRandomBag<EnchantmentStack> bag = new WeightedRandomBag<>();
		for (Enchantment enchantment : Enchantments.getInstance()) {
			if (!enchantment.hidden()) {
				for (int i = enchantment.minLevel(); i < enchantment.maxLevel() - enchantment.minLevel(); i++) {
					bag.addEntry(new EnchantmentStack(enchantment, i), 1);
				}
			}
		}
		return bag;
	}

	private void applyEnchantments(@NotNull ItemStack book, @NotNull Random random, WeightedRandomBag<EnchantmentStack> bag) {
		Set<EnchantmentStack> optionList = new HashSet<>();
		ListTag listTag = new ListTag();
		for (int option = 0; option < VARIANTS_POS; option++) {
			optionList.clear();
			int count = random.nextInt(3);
			for (int i = count + 1; i > 0; i--) {
				EnchantmentStack addStack = bag.getRandom(random);
				if (EnchantmentContainer.adjustsLevel(optionList, addStack)) {
					continue;
				}

				optionList.add(addStack);
			}
			CompoundTag optionTag = new CompoundTag();
			listTag.addTag(optionTag);
			EnchantmentContainer.addEnchantments(optionTag, new ArrayList<>(optionList));
		}
		book.getData().put(EnchantmentContainer.ENCHANTMENT_DATA_LIST, listTag);
		book.getData().putLong("id", random.nextLong());
	}

	private void updateBookCount(@NotNull World world, @NotNull TilePosc tilePos, @NotNull Player player, int metadata, ItemStack book) {
		player.inventory.insertItem(book, true); //insert into player inventory
		if (book.stackSize == 0) {
			if ((metadata & 11) == 0) {
				world.setBlockDataNotify(tilePos, metadata >> 4);
				world.setBlockTypeNotify(tilePos, Blocks.BOOKSHELF_PLANKS_OAK);
			} else {
				world.setBlockDataNotify(tilePos, metadata - 1);
			}
		}
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
	public void onDestroyedByExplosion(@NotNull World world, @NotNull TilePosc tilePos) {
		int metadata = world.getBlockData(tilePos);
		world.dropItem(tilePos, new ItemStack(Items.BOOK, Math.min(4, metadata)));
		Blocks.BOOKSHELF_PLANKS_OAK.onDestroyedByExplosion(world, tilePos);
	}

}
