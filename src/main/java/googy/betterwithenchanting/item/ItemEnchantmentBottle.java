package googy.betterwithenchanting.item;

import googy.betterwithenchanting.api.Enchantments;
import googy.betterwithenchanting.api.EnchantmentContainer;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ItemEnchantmentBottle extends Item {
	public ItemEnchantmentBottle(String name, String namespaceId, int id) {
		super(name, namespaceId, id);
		this.maxStackSize = 1;
	}

	@Override
	public @Nullable ItemStack onUse(@NotNull ItemStack selfStack, @NotNull World world, @NotNull Player player) {
		int level = EnchantmentContainer.getLevel(selfStack, Enchantments.BOTTLED_SCORE);
		if(level > 0){
			player.score += level * 4000;
			return null;
		}
		return selfStack;
	}
}
