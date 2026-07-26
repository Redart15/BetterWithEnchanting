package googy.betterwithenchanting.mixins.mixin.enchantment;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import googy.betterwithenchanting.api.EnchantmentContainer;
import googy.betterwithenchanting.api.Enchantments;
import googy.betterwithenchanting.mixins.MixinsHelperLogic;
import googy.betterwithenchanting.mixins.mixin.accessor.ItemAccessor;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.Mob;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.util.helper.DamageType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

@Mixin(value = Mob.class, remap = false)
public class MobMixinEnchantments {
	@Inject(method = "hurt", at = @At(value = "RETURN"))
	private void applyQuickStrike(Entity attacker, int damage, DamageType type, CallbackInfoReturnable<Boolean> info) {
		if (!(attacker instanceof Player player)) {
			return;
		}
		Mob thisLiving = (Mob) (Object) this;
		MixinsHelperLogic.devLog("Victims timer: " + thisLiving.heartsFlashTime);
		int quickstrikeLevel = EnchantmentContainer.getLevel(player.getHeldItem(), Enchantments.QUICKSTRIKE);
		if (quickstrikeLevel <= 0) {
			return;
		}
		if (thisLiving.heartsFlashTime == thisLiving.heartsHalvesLife) {
			thisLiving.heartsFlashTime = (int) (thisLiving.heartsHalvesLife * 0.75);
		}
	}

	@WrapOperation(method = "onDeath", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/entity/Mob;dropDeathItems()V"))
	private void applyLooting(Mob instance, Operation<Void> original, Entity killer) {
		if (killer instanceof Player player && !(instance instanceof Player)) {
			ItemStack itemStack = player.getCurrentEquippedItem();
			int level = EnchantmentContainer.getLevel(itemStack, Enchantments.LOOTING);
			if (level > 0) {
				for (int i = 0; i < level; i++) {
					if (ItemAccessor.getItemRand().nextInt(4) == 0) {
						original.call(instance);
					}
				}
			}
		}
		original.call(instance);
	}

	@Inject(method = "knockBack", at = @At("HEAD"))
	private void doesKnockbackApply(
		Entity entity, int i, double d, double d1,
		CallbackInfo ci,
		@Share("level")LocalIntRef knockBackLevel
	){
		if(entity instanceof Player player){
			ItemStack itemStack = player.getCurrentEquippedItem();
			int level = EnchantmentContainer.getLevel(itemStack, Enchantments.KNOCKBACK);
			if(level > 0){
				knockBackLevel.set(level);
			}
		}
		knockBackLevel.set(0);
	}


	@ModifyExpressionValue(method = "knockBack", at = @At(value = "CONSTANT", args = "floatValue=0.4F", ordinal = 0))
	private float applyHorizontalKnockbackBonus(float original, @Share("level")LocalIntRef knockBackLevel) {
		float returnValue = 0.4f;
		returnValue += 0.1f * knockBackLevel.get();
		return returnValue;
	}

	@ModifyExpressionValue(method = "knockBack", at = @At(value = "CONSTANT", args = "doubleValue=0.4000000059604645", ordinal = 1))
	private double applyVerticalKnockbackBonus(double original, @Share("level")LocalIntRef knockBackLevel) {
		float returnValue = 0.4f;
		returnValue += 0.2f * knockBackLevel.get();
		return returnValue;
	}


	@Definition(id = "yd", field = "Lnet/minecraft/core/entity/Mob;yd:D")
	@Expression("this.yd > 0.4000000059604645")
	@ModifyExpressionValue(method = "knockBack", at = @At("MIXINEXTRAS:EXPRESSION"))
	private boolean spoofVerticalCheck(boolean original, @Share("level")LocalIntRef knockBackLevel){
		if(knockBackLevel.get() > 0){
			return false;
		}
		return original;
	}

	@Inject(method = "eatFood", at = @At("HEAD"))
	private void cacheResults(
		ItemStack stack, CallbackInfo ci,
		@Share("nourishmentLvL")LocalIntRef nourishmentLvL,
		@Share("fillingLvL")LocalIntRef fillingLvL
	){
		nourishmentLvL.set(EnchantmentContainer.getLevel(stack, Enchantments.NOURISHMENT));
		fillingLvL.set(EnchantmentContainer.getLevel(stack, Enchantments.FILLING));
	}

	@WrapOperation(method = "eatFood", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/item/ItemStack;getItemKey()Ljava/lang/String;"))
	private String changeKey(
		ItemStack instance, Operation<String> original,
		@Share("nourishmentLvL")LocalIntRef nourishmentLvL,
		@Share("fillingLvL")LocalIntRef fillingLvL
	){
		String key = original.call(instance);
		int nourishment = nourishmentLvL.get();
		int filling = fillingLvL.get();
		if(nourishment == 0 && filling == 0){
			return key;
		}
		MixinsHelperLogic.applyInsight((Mob)(Object) this, instance);
		return String.format("%s.%02d", key, Objects.hash(nourishment, filling));
	}


	@WrapOperation(method = "eatFood", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/entity/Mob;heal(I)V"))
	private void healAdditional(
		Mob instance, int i, Operation<Void> original,
		@Share("nourishmentLvL")LocalIntRef nourishmentLvL,
		@Share("fillingLvL")LocalIntRef fillingLvL,
		@Local ItemStack itemStack
	){
		MixinsHelperLogic.applyInsight(instance, itemStack);
		i += MixinsHelperLogic.getAdditionalHealing(i, fillingLvL.get());
		i += nourishmentLvL.get() > 0 ? 1: 0;
		original.call(instance, i);
	}
}
