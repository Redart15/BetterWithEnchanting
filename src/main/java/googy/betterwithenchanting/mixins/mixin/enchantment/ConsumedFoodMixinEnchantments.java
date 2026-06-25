package googy.betterwithenchanting.mixins.mixin.enchantment;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import googy.betterwithenchanting.mixins.mixin.accessor.ConsumedFoodAccessor;
import net.minecraft.core.entity.ConsumedFood;
import net.minecraft.core.entity.Mob;
import net.minecraft.core.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import googy.betterwithenchanting.mixins.MixinsHelperLogic;

@Mixin(value = ConsumedFood.class, remap = false)
public abstract class ConsumedFoodMixinEnchantments {

	@Unique
	private int additionalHealing;
	@Unique
	private int reducedDuration;
		/* Food values:
	Name		Amount		TickPerHeal
	Apple		4			8
	Bread		5			12
	PorkRaw		3			16
	Pork		8			16
	GoldApple	42			0
	FishRaw		2			12
	Fish		5			12
	Cookie		1			0
	Cherry		2			2
	VanisonRaw	3			16
	Venison		12			8
	*/

	@Inject(method = "<init>", at = @At("TAIL"))
	private void addExtras(Mob entity, ItemStack stack, CallbackInfo ci) {
		ConsumedFoodAccessor asThis = (ConsumedFoodAccessor) this;
		this.additionalHealing = 	MixinsHelperLogic.calcAdditionalHealing(asThis);
		this.reducedDuration = 		MixinsHelperLogic.calcAdditionalDuration(asThis);
		asThis.setHealRemaining(asThis.getHealRemaining() + this.additionalHealing);
	}

	@Inject(method = "addFood", at = @At("TAIL"))
	public void addAdditioanlHealing(ItemStack additionalStack, CallbackInfo ci) {
		ConsumedFoodAccessor asThis = (ConsumedFoodAccessor) this;
		asThis.setHealRemaining(asThis.getHealRemaining() + this.additionalHealing);
	}

	@ModifyExpressionValue(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/item/ItemFood;getTicksPerHeal(Lnet/minecraft/core/item/ItemStack;)I"))
	private int adjustTickCount(int original) {
		return original - this.reducedDuration;
	}

}
