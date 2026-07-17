package googy.betterwithenchanting.render;

import net.minecraft.client.render.item.model.ItemModelStandard;
import net.minecraft.client.render.texture.stitcher.IconCoordinate;
import net.minecraft.client.render.texture.stitcher.TextureRegistry;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.util.helper.DyeColor;
import org.jetbrains.annotations.NotNull;

import static googy.betterwithenchanting.BetterWithEnchanting.MOD_ID;

public class ItemModelEnchantedBook extends ItemModelStandard {
	public static IconCoordinate[] BOOK = new IconCoordinate[16];

	public ItemModelEnchantedBook(Item item) {
		super(item, false);
	}

	@Override
	public @NotNull IconCoordinate getIcon(Entity entity, @NotNull ItemStack itemStack) {
		int meta = itemStack.getMetadata();
		return BOOK[meta & 15];
	}

	static {
		for(DyeColor c : DyeColor.itemOrderedColors()) {
			BOOK[c.itemMeta] = TextureRegistry.getTexture(MOD_ID + ":item/enchanted_book/" + c.colorID);
		}

	}

}
