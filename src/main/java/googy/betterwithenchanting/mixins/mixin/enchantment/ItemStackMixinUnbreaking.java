package googy.betterwithenchanting.mixins.mixin.enchantment;

import googy.betterwithenchanting.api.Enchantments;
import googy.betterwithenchanting.api.EnchantmentContainer;
import googy.betterwithenchanting.mixins.mixin.accessor.ItemAccessor;
import net.minecraft.core.item.ItemArmor;
import net.minecraft.core.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.Random;

@Mixin(value = ItemStack.class, remap = false)
public abstract class ItemStackMixinUnbreaking {
	@ModifyVariable(method = "damageItem", at = @At("HEAD"), ordinal = 0, argsOnly = true)
	private int enchanting$applyUnbreaking(int damage) {
		int unbreakingLevel = EnchantmentContainer.getLevel((ItemStack) (Object) this, Enchantments.UNBREAKING);
		if (damage <= 0 || unbreakingLevel <= 0) {
			return damage;
		}
		ItemStack asThis = (ItemStack) (Object) this;
		Random random = ItemAccessor.getItemRand();
		for (int i = 0; i < damage; i++) {
			if (asThis.getItem() instanceof ItemArmor && unbreakingLevel > random.nextInt(Enchantments.UNBREAKING.maxLevel() * 2)) {
				damage--;
			}
		}
		return Math.max(damage, 0);
	}
}
