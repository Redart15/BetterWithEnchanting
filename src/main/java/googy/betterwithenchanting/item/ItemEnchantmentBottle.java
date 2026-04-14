package googy.betterwithenchanting.item;

import googy.betterwithenchanting.BetterWithEnchanting;
import googy.betterwithenchanting.api.Enchantments;
import googy.betterwithenchanting.api.EnchantmentContainer;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.lang.I18n;
import net.minecraft.core.world.World;

public class ItemEnchantmentBottle extends Item {
	public ItemEnchantmentBottle(String name, String namespaceId, int id) {
		super(name, namespaceId, id);
		this.maxStackSize = 1;
	}

	@Override
	public ItemStack onUseItem(ItemStack itemstack, World world, Player player) {
		if(EnchantmentContainer.contains(itemstack, Enchantments.BOTTLED_SCORE)){
			int level = EnchantmentContainer.getLevel(itemstack, Enchantments.BOTTLED_SCORE);
			player.score += level * 3500;
			return new ItemStack(BetterWithEnchanting.SCORE_BOTTLE.getDefaultStack());
		}
		return itemstack;
	}

	@Override
	public String getTranslatedName(ItemStack itemstack) {
		if(EnchantmentContainer.contains(itemstack, Enchantments.BOTTLED_SCORE)){
			return I18n.getInstance().translateKey(itemstack.getItemKey() + ".enchanted.name");
		}
		return super.getTranslatedName(itemstack);
	}

	@Override
	public String getTranslatedDescription(ItemStack itemstack) {
		if(EnchantmentContainer.contains(itemstack, Enchantments.BOTTLED_SCORE)){
			return I18n.getInstance().translateKey(itemstack.getItemKey() + ".enchanted.desc");
		}
		return super.getTranslatedDescription(itemstack);
	}
}
