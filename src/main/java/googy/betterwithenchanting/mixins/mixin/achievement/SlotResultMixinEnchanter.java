package googy.betterwithenchanting.mixins.mixin.achievement;

import googy.betterwithenchanting.api.EnchantmentAchievements;
import googy.betterwithenchanting.block.EnchantmentBlocks;
import googy.betterwithenchanting.mixins.mixin.accessor.SlotResultAccessor;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.player.inventory.slot.SlotResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = SlotResult.class, remap = false)
public abstract class SlotResultMixinEnchanter {

	@Inject(method = "onTake", at = @At("TAIL"))
	private void addEnchantmentTable(ItemStack itemStack, CallbackInfo ci){
		Item item = itemStack.getItem();
		if (item.id == EnchantmentBlocks.ENCHANTMENT_TABLE.id()) {
			((SlotResultAccessor) this).getThePlayer().addStat(EnchantmentAchievements.CRAFT_ENCHANTER, 1);
		}

	}
}
