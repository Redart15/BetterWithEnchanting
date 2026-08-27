package googy.betterwithenchanting.mixins.mixin.enchantment;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import googy.betterwithenchanting.mixins.EnchantedArrow;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.EntityDispatcher;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.entity.projectile.Projectile;
import net.minecraft.core.item.ItemBow;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = ItemBow.class, remap = false)
public class ItemBowMixinEnchantment {

	@WrapOperation(method = "onUse", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/world/World;entityJoinedWorld(Lnet/minecraft/core/entity/Entity;)Z"))
	private boolean applyFlameMultiBuck(
		World instance, Entity entity, Operation<Boolean> original,
		ItemStack itemStack, @Local(argsOnly = true) Player player
	) {
		EnchantedArrow arrows = new EnchantedArrow(player, itemStack, entity);
		arrows.setOnFire((Projectile) entity);
		arrows.setMultiHit((Projectile) entity);
		arrows.setIncreasedSpeed((Projectile) entity);
		boolean returnValues = original.call(instance, entity);
		returnValues &= arrows.doMultiShot(instance, original);
		returnValues &= arrows.doBuckShot(instance, original);
		return returnValues;
	}
}
