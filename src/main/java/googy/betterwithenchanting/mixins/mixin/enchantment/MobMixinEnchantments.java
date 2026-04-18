package googy.betterwithenchanting.mixins.mixin.enchantment;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import googy.betterwithenchanting.api.Enchantments;
import googy.betterwithenchanting.api.EnchantmentContainer;
import googy.betterwithenchanting.mixins.EnchantmentMixins;
import googy.betterwithenchanting.mixins.mixin.accessor.ItemAccessor;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.Mob;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.util.helper.DamageType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = Mob.class, remap = false)
public class MobMixinEnchantments {
	@Inject(method = "hurt", at = @At(value = "RETURN"))
	private void enchanting$applyQuickStrike(Entity attacker, int damage, DamageType type, CallbackInfoReturnable<Boolean> info) {
		if (!(attacker instanceof Player)) {
			return;
		}
		Player player = (Player) attacker;
		Mob thisLiving = (Mob) (Object) this;
		EnchantmentMixins.devLog("Victims timer: " + thisLiving.heartsFlashTime);
		int quickstrikeLevel = EnchantmentContainer.getLevel(player.getHeldItem(), Enchantments.QUICKSTRIKE);
		if (quickstrikeLevel <= 0) {
			return;
		}
		if (thisLiving.heartsFlashTime == thisLiving.heartsHalvesLife) {
			thisLiving.heartsFlashTime = (int) (thisLiving.heartsHalvesLife * 0.75);
		}
	}

	@WrapOperation(method = "onDeath", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/entity/Mob;dropDeathItems()V"))
	private void enchanting$applyLooting(Mob instance, Operation<Void> original, Entity killer){
		if(killer instanceof Player && !(instance instanceof Player)){
			Player player = (Player) killer;
			ItemStack itemStack = player.getCurrentEquippedItem();
			int level = EnchantmentContainer.getLevel(itemStack, Enchantments.LOOTING);
			if(level > 0){
				for(int i = 0; i < level; i++){
					if (ItemAccessor.getItemRand().nextInt(4) == 0) {
						original.call(instance);
					}
				}
			}
		}
		original.call(instance);
	}
}
