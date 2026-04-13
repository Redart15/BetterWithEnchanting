package googy.betterwithenchanting.render;

import googy.betterwithenchanting.enchantment.Enchantments;
import googy.betterwithenchanting.utils.EnchantmentUtils;
import net.minecraft.client.render.item.model.ItemModelStandard;
import net.minecraft.client.render.texture.stitcher.IconCoordinate;
import net.minecraft.client.render.texture.stitcher.TextureRegistry;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static googy.betterwithenchanting.BetterWithEnchanting.MOD_ID;

public class ItemScoreBottleModel extends ItemModelStandard {
	public ItemScoreBottleModel(Item item, String namespace) {
		super(item, namespace);
	}

	@Override
	public @NotNull IconCoordinate getIcon(@Nullable Entity entity, ItemStack itemStack) {
		if(EnchantmentUtils.containsEnchantment(itemStack, Enchantments.bottledScore)) {
			return super.getIcon(entity, itemStack);
		}
		return TextureRegistry.getTexture(MOD_ID + ":item/empty_bottle");
	}
}
