package googy.betterwithenchanting.mixins.mixin.enchantment;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import googy.betterwithenchanting.api.EnchantmentContainer;
import googy.betterwithenchanting.api.Enchantments;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.tool.ItemToolAxe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = ItemToolAxe.class, remap = false)
public class ItemToolAxeMixin {

	@ModifyExpressionValue(method = "beforeBlockDestroyed", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/world/World;getGameRuleValue(Lnet/minecraft/core/data/gamerule/GameRule;)Ljava/lang/Object;"))
	public Object canFelling(Object original, @Local(argsOnly = true) ItemStack itemStack){
		if(original instanceof Boolean && EnchantmentContainer.contains(itemStack,  Enchantments.FELLING)){
				return true;
			}
		return original;
	}
}
