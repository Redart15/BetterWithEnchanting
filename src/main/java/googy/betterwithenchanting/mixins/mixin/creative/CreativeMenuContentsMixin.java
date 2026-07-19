package googy.betterwithenchanting.mixins.mixin.creative;

import googy.betterwithenchanting.block.EnchantmentBlocks;
import googy.betterwithenchanting.item.EnchantmentItems;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.player.inventory.CreativeMenuContents;
import net.minecraft.core.util.helper.DyeColor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

import static googy.betterwithenchanting.mixins.mixin.accessor.CreativeMenuContentsAccessor.callAddBlock;
import static googy.betterwithenchanting.mixins.mixin.accessor.CreativeMenuContentsAccessor.getRAINBOW_ORDER;

@Mixin(value = CreativeMenuContents.class, remap = false)
public abstract class CreativeMenuContentsMixin {

	@Inject(method = "addWorkstationsAndGlass", at = @At("TAIL"))
	private static void addMoreWorkstations(List<ItemStack> list, CallbackInfo ci){
		callAddBlock(list, EnchantmentBlocks.ENCHANTMENT_TABLE);
		callAddBlock(list, EnchantmentBlocks.ENCHANTED_BOOKSHELF);
	}


	@Inject(method = "addBasics", at = @At("TAIL"))
	private static void addMoreBasics(List<ItemStack> list, CallbackInfo ci){
		for(DyeColor dyeColor: getRAINBOW_ORDER()){
			list.add(new ItemStack(EnchantmentItems.ENCHANTED_BOOK, 1, dyeColor.itemMeta));
		}
	}
}
