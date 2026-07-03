package googy.betterwithenchanting.mixins.mixin.enchantment;

import com.llamalad7.mixinextras.sugar.Local;
import googy.betterwithenchanting.mixins.MixinsHelperLogic;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.projectile.ProjectileCannonball;
import net.minecraft.core.item.ItemHandCannonLoaded;
import net.minecraft.core.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(value = ItemHandCannonLoaded.class, remap = false)
public class ItemHandCannonMixinEnchantments {

	@ModifyArg(method = "onUse", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/world/World;entityJoinedWorld(Lnet/minecraft/core/entity/Entity;)Z"), index = 0)
	public Entity modifyCannonball(Entity entity, @Local(argsOnly = true)ItemStack itemStack){
		if(entity instanceof ProjectileCannonball cannonball){
			MixinsHelperLogic.setExplosive(cannonball, itemStack);
			MixinsHelperLogic.setIncendiary(cannonball, itemStack);
			MixinsHelperLogic.setVolatile(cannonball, itemStack);
			MixinsHelperLogic.setPrecise(cannonball, itemStack);
			MixinsHelperLogic.setPower(cannonball, itemStack);
		}
		return entity;
	}
}
