package googy.betterwithenchanting.mixins.mixin.enchantment.render;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import googy.betterwithenchanting.api.EnchantmentContainer;
import googy.betterwithenchanting.api.Enchantments;
import googy.betterwithenchanting.mixins.MixinsHelperLogic;
import net.minecraft.client.gui.hud.component.HudComponentHealthBar;
import net.minecraft.core.item.ItemFood;
import net.minecraft.core.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = HudComponentHealthBar.class, remap = false)
public class HudComponentHealthBarMixinPreview {

	@WrapOperation(method = "getPotentialHealing", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/item/ItemFood;getHealAmount(Lnet/minecraft/core/item/ItemStack;)I"))
	private int increaseHealing(ItemFood instance, ItemStack stack, Operation<Integer> original){
		int healingAmount = original.call(instance, stack);
		int lvl = EnchantmentContainer.getLevel(stack, Enchantments.FILLING);
		return healingAmount + MixinsHelperLogic.getAdditionalHealing(lvl, healingAmount);
	}
}
