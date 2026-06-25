package googy.betterwithenchanting.mixins.mixin.enchantment;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import googy.betterwithenchanting.mixins.MixinsHelperLogic;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.ItemFood;
import net.minecraft.core.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = ItemFood.class, remap = false)
public abstract class ItemFoodMixinLasting {

	@WrapOperation(method = "onUse", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/item/ItemStack;consumeItem(Lnet/minecraft/core/entity/player/Player;)Z"))
	private boolean canConsume(ItemStack instance, Player entityplayer, Operation<Boolean> original) {
		MixinsHelperLogic.applyScore(entityplayer, instance);
		if(MixinsHelperLogic.applyLasting(instance)){
			return true;
		}
		return original.call(instance, entityplayer);
	}
}
