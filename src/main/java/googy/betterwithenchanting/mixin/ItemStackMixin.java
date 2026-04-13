package googy.betterwithenchanting.mixin;

import googy.betterwithenchanting.enchantment.Enchantments;
import googy.betterwithenchanting.enchantment.enchantments.Unbreaking;
import googy.betterwithenchanting.utils.EnchantmentUtils;
import net.minecraft.core.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(value = ItemStack.class, remap = false)
public class ItemStackMixin {
	@ModifyVariable(method = "damageItem", at = @At("HEAD"), ordinal = 0, argsOnly = true)
	int damageItem(int damage) {
		if (damage <= 0) {
			return damage;
		}
		int unbreakingLevel = EnchantmentUtils.getLevel((ItemStack) (Object) this, Enchantments.unbreaking);
		if (unbreakingLevel <= 0) return damage;
		for (int i = 0; i < damage; i++) {
			if (Unbreaking.shouldNegateDamage((ItemStack) (Object) this, unbreakingLevel)) {
				damage--;
			}
		}
		if (damage < 0) {
			damage = 0;
		}
		return damage;
	}
}
