package googy.betterwithenchanting.item;

import com.mojang.nbt.tags.CompoundTag;
import com.mojang.nbt.tags.ListTag;
import googy.betterwithenchanting.api.Enchantment;
import googy.betterwithenchanting.api.EnchantmentContainer;
import googy.betterwithenchanting.api.EnchantmentStack;
import googy.betterwithenchanting.api.Enchantments;
import googy.betterwithenchanting.mixins.interfaces.ContainerHotbarLocking;
import googy.betterwithenchanting.mixins.interfaces.PlayerAdditionalGui;
import googy.betterwithenchanting.network.UpdateLockState;
import net.minecraft.core.WeightedRandomBag;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.lang.I18n;
import net.minecraft.core.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import turniplabs.halplibe.helper.network.NetworkHandler;

import java.util.*;

import static googy.betterwithenchanting.mixins.mixin.accessor.ItemAccessor.getItemRand;

public class ItemEnchantedBook extends Item {
	private static final int VARIANTS_POS = 2;

	public ItemEnchantedBook(@NotNull String translationKey, @NotNull String namespaceId, int id) {
		super(translationKey, namespaceId, id);
	}

	@Override
	public @Nullable ItemStack onUse(@NotNull ItemStack selfStack, @NotNull World world, @NotNull Player player) {
		if(!world.isClientSide){
			if(!selfStack.getData().containsKey("id")){
				this.applyEnchantments(selfStack);
			}
			((PlayerAdditionalGui)player).displayGuiEnchantmentBook(selfStack);
			ContainerHotbarLocking inventory = (ContainerHotbarLocking) player.inventory;
			inventory.enchanted$lockSlot(player.inventory.getCurrentSlot(), true);
			player.inventory.setItem(player.inventory.getCurrentSlot(), null);
			NetworkHandler.sendToPlayer(player, new UpdateLockState(inventory.enchanted$getValue()));
		}
		return selfStack;
	}

	public void applyEnchantments(ItemStack book) {
		WeightedRandomBag<EnchantmentStack> bag = new WeightedRandomBag<>();
		for (Enchantment enchantment : Enchantments.getInstance()) {
			if (!enchantment.hidden()) {
				for (int i1 = enchantment.minLevel(); i1 < enchantment.maxLevel() - enchantment.minLevel(); i1++) {
					bag.addEntry(new EnchantmentStack(enchantment, i1), 1);
				}
			}
		}
		@NotNull Random random = getItemRand();
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

	@Override
	public @NotNull String getTranslatedName(@NotNull ItemStack selfStack) {
		if(!selfStack.getData().containsKey("id")){
			return I18n.getInstance().translateKey(selfStack.getItemKey() + ".unid.name");
		}
		return super.getTranslatedName(selfStack);
	}

	@Override
	public @NotNull String getTranslatedDescription(@NotNull ItemStack selfStack) {
		if(!selfStack.getData().containsKey("id")){
			return I18n.getInstance().translateKey(selfStack.getItemKey() + ".unid.desc");
		}
		return I18n.getInstance().translateKeyAndFormat(selfStack.getItemKey() + ".desc");
	}
}
