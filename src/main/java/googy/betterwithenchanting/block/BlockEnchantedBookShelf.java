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
import net.minecraft.core.entity.Mob;
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
	public BlockEnchantedBookShelf(@NotNull Block<?> block, @NotNull Material material) {
		super(block, material);
	}


	@Override
	public boolean onInteracted(@NotNull World world, @NotNull TilePosc tilePos, @NotNull Player player, @Nullable Side side, double xHit, double yHit) {
		int metadata = world.getBlockData(tilePos);
		if (this.canGiveBook(metadata)) {
			this.givePlayerEnchantedBook(world, tilePos, player, metadata);
			return true;
		}
		return false;
	}

	private boolean canGiveBook(int metadata) {
		return (metadata >> 7) == 0;
	}

	private static void givePlayerEnchantedBook(@NotNull World world, @NotNull TilePosc tilePos, @NotNull Player player, int metadata) {
		ItemStack book = new ItemStack(EnchantmentItems.ENCHANTED_BOOK);
		Set<EnchantmentStack> optionList = new HashSet<>();
		WeightedRandomBag<EnchantmentStack> bag = new WeightedRandomBag<>();
		for (Enchantment enchantment : Enchantments.getInstance()) {
			if (!enchantment.hidden()) {
				for (int i = enchantment.minLevel(); i < enchantment.maxLevel() - enchantment.minLevel(); i++) {
					bag.addEntry(new EnchantmentStack(enchantment, i), enchantment.getWeight(i));
				}
			}
		}
		ListTag listTag = new ListTag();
		for (int option = 0; option < 2; option++) {
			optionList.clear();
			for (int i = world.rand.nextInt(4) + 1; i > 0; i--) {
				EnchantmentStack addStack = bag.getRandom(world.rand);
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
		player.inventory.insertItem(book, true); //insert into player inventory
		if (book.stackSize == 0) {
			if ((metadata & 11) > 0) {
				world.setBlockDataNotify(tilePos, metadata - 1);
			} else {
				world.setBlockDataNotify(tilePos, world.rand.nextInt(16));
				world.setBlockTypeNotify(tilePos, Blocks.BOOKSHELF_PLANKS_OAK);
			}
		}
	}

	@Override
	public @NotNull ItemStack @Nullable [] getBreakResult(@NotNull World world, @NotNull EnumDropCause dropCause, @NotNull TilePosc tilePos, int data, @Nullable TileEntity tileEntity) {
		switch (dropCause) {
			case SILK_TOUCH, PICK_BLOCK -> {
				return new ItemStack[]{new ItemStack(this.block, 1, 0b1000_0000)};
			}
			default -> {
				return Blocks.BOOKSHELF_PLANKS_OAK.getBreakResult(world, dropCause, data, tileEntity);
			}
		}
	}


	@Override
	public void onDestroyedByExplosion(@NotNull World world, @NotNull TilePosc tilePos) {
		int metadata = world.getBlockData(tilePos);
		world.dropItem(tilePos, new ItemStack(Items.BOOK, Math.min(4, metadata)));
		Blocks.BOOKSHELF_PLANKS_OAK.onDestroyedByExplosion(world, tilePos);
	}

	@Override
	public void onPlacedByMob(@NotNull World world, @NotNull TilePosc tilePos, @NotNull Side side, @NotNull Mob mob, double xHit, double yHit) {
		int metadata = 0b1000_0000;
		if (mob instanceof Player player && player.getHeldItem() != null) {
			metadata = player.getHeldItem().getMetadata() | metadata;
		}
		super.onPlacedByMob(world, tilePos, side, mob, xHit, yHit);
		world.setBlockDataNotify(tilePos, metadata);
	}


	@Override
	public void animationTick(@NotNull World world, @NotNull TilePosc tilePos, @NotNull Random rand) {
		super.animationTick(world, tilePos, rand);
	}
}
