package googy.betterwithenchanting.mixins.mixin.enchantment;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import googy.betterwithenchanting.api.EnchantmentContainer;
import googy.betterwithenchanting.api.Enchantments;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.monster.Enemy;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.player.inventory.container.ContainerInventory;
import net.minecraft.core.util.helper.DamageType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(value = Player.class, remap = false)
public class PlayerMixinEnchantments {

	@WrapOperation(method = "attackTargetEntityWithCurrentItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/player/inventory/container/ContainerInventory;getDamageVsEntity(Lnet/minecraft/core/entity/Entity;)I"))
	private int enchanting$$applyCrit(ContainerInventory instance, Entity entity, Operation<Integer> original){
		int damage = original.call(instance, entity);
		int level = EnchantmentContainer.getLevel(((Player)entity).getCurrentEquippedItem(), Enchantments.CRIT);
		if(entity.yd < 0.0F && level > 0){
			damage = (int) Math.ceil(level * 0.1 * damage);
		}
		return damage;
	}

	@WrapOperation(method = "attackTargetEntityWithCurrentItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/entity/Entity;hurt(Lnet/minecraft/core/entity/Entity;ILnet/minecraft/core/util/helper/DamageType;)Z"))
	private boolean enchanting$$applyLifeStealAndSharpness(Entity instance, Entity attacker, int baseDamage, DamageType type, Operation<Boolean> original){
		ItemStack itemstack = ((Player) attacker).getCurrentEquippedItem();
		int bonusDamage = 0;
		if( instance instanceof Enemy){
			bonusDamage += EnchantmentContainer.getLevel(itemstack, Enchantments.SLAYER);
		}
		boolean hasHit = original.call(instance, attacker, baseDamage + bonusDamage, type);
		if(EnchantmentContainer.getLevel(itemstack, Enchantments.LIFESTEAL) > 0 && hasHit){
			((Player) attacker).heal(2);
		}
		return hasHit;
	}

	@Inject(method = "attackTargetEntityWithCurrentItem", at = @At("HEAD"))
	private void enchanting$applyFlame(Entity entity, CallbackInfo info) {
		ItemStack stack = ((Player) (Object) this).getCurrentEquippedItem();
		int flameLevel = EnchantmentContainer.getLevel(stack, Enchantments.FLAME);
		int fireTime = Math.max(flameLevel * 20, 0);  // level * second
		if (entity.remainingFireTicks < fireTime) {
			entity.remainingFireTicks = fireTime;
		}
	}
}
