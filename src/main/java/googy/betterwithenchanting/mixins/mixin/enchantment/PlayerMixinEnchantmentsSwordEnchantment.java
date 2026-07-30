package googy.betterwithenchanting.mixins.mixin.enchantment;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import googy.betterwithenchanting.api.EnchantmentContainer;
import googy.betterwithenchanting.api.Enchantments;
import googy.betterwithenchanting.mixins.MixinsHelperLogic;
import googy.betterwithenchanting.mixins.mixin.accessor.EntityAccessor;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.monster.Enemy;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.util.helper.DamageType;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(value = Player.class, remap = false)
public class PlayerMixinEnchantmentsSwordEnchantment {

	@WrapOperation(method = "attackTargetEntityWithCurrentItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/item/ItemStack;getDamageVsEntity(Lnet/minecraft/core/entity/Entity;)I"))
	private int applyCrit(@NotNull ItemStack instance,@NotNull Entity entity, Operation<Integer> original){
		Player asThis = (Player) (Object) this;
		int damage = original.call(instance, entity);
		int level = EnchantmentContainer.getLevel(instance, Enchantments.CRIT);
		if(asThis.yd < 0.0F && level > 0 && asThis.fallDistance > 1.0f){
			damage = (int) Math.ceil(level * 0.1 * damage) + (int) MixinsHelperLogic.log(asThis.fallDistance, 7.0f - level);
		}
		return damage;
	}

	@WrapOperation(method = "attackTargetEntityWithCurrentItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/entity/Entity;hurt(Lnet/minecraft/core/entity/Entity;ILnet/minecraft/core/util/helper/DamageType;)Z"))
	private boolean applyLifeStealAndSharpness(
		@NotNull Entity instance,@NotNull Entity attacker,
		int baseDamage, DamageType type, Operation<Boolean> original
	){
		ItemStack itemstack = ((Player) attacker).getCurrentEquippedItem();
		int bonusDamage = 0;
		if( instance instanceof Enemy){
			bonusDamage += EnchantmentContainer.getLevel(itemstack, Enchantments.SLAYER);
		}
		boolean hasHit = original.call(instance, attacker, baseDamage + bonusDamage, type);
		if(EnchantmentContainer.getLevel(itemstack, Enchantments.LIFESTEAL) > 0 && hasHit){
			((Player) attacker).heal(2);
		}
		if(EnchantmentContainer.getLevel(itemstack, Enchantments.CRIT) > 0 && hasHit){
			MixinsHelperLogic.spawnCritParticles((Player)attacker, instance);
		}
		return hasHit;
	}

	@Inject(method = "attackTargetEntityWithCurrentItem", at = @At("HEAD"))
	private void applyFlame(Entity entity, CallbackInfo info) {
		ItemStack stack = ((Player) (Object) this).getCurrentEquippedItem();
		int flameLevel = EnchantmentContainer.getLevel(stack, Enchantments.FLAME);
		int fireTime = Math.max(flameLevel * 20, 0);  // level * second
		if (flameLevel > 0 && !((EntityAccessor) entity).isFireImmune() && fireTime > entity.remainingFireTicks) {
			entity.remainingFireTicks = Math.max(flameLevel * 20, 0);
			entity.maxFireTicks = entity.remainingFireTicks;
		}
	}
}
