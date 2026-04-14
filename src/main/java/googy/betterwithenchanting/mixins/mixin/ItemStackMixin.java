package googy.betterwithenchanting.mixins.mixin;

import googy.betterwithenchanting.api.Enchantments;
import googy.betterwithenchanting.api.EnchantmentContainer;
import googy.betterwithenchanting.mixins.EnchantMixins;
import net.minecraft.core.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(value = ItemStack.class, remap = false)
public abstract class ItemStackMixin {
	@ModifyVariable(method = "damageItem", at = @At("HEAD"), ordinal = 0, argsOnly = true)
	int damageItem(int damage) {
		if (damage <= 0) {
			return damage;
		}
		int unbreakingLevel = EnchantmentContainer.getLevel((ItemStack) (Object) this, Enchantments.UNBREAKING);
		if (unbreakingLevel <= 0) {
			return damage;
		}
		for (int i = 0; i < damage; i++) {
			if (EnchantMixins.shouldNegateDamage((ItemStack) (Object) this, unbreakingLevel)) {
				damage--;
			}
		}
		if (damage < 0) {
			damage = 0;
		}
		return damage;
	}
}
