package googy.betterwithenchanting.item;

import googy.betterwithenchanting.mixins.interfaces.PlayerAdditionalGui;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ItemEnchantedBook extends Item {


	public ItemEnchantedBook(@NotNull String translationKey, @NotNull String namespaceId, int id) {
		super(translationKey, namespaceId, id);
	}

	@Override
	public @Nullable ItemStack onUse(@NotNull ItemStack selfStack, @NotNull World world, @NotNull Player player) {
		if(!world.isClientSide){
			((PlayerAdditionalGui)player).displayGuiEnchantmentBook(selfStack);
		}
		return selfStack;
	}
}
