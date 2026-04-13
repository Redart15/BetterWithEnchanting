package googy.betterwithenchanting.mixin;

import googy.betterwithenchanting.enchantment.Enchantments;
import googy.betterwithenchanting.utils.EnchantmentUtils;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Player.class, remap = false)
public abstract class PlayerMixin {

	@Inject(method = "attackTargetEntityWithCurrentItem", at = @At("HEAD"))
	public void attackTargetEntityWithCurrentItem(Entity entity, CallbackInfo info) {
		if (!(entity instanceof Player)) {
			return;
		}

		ItemStack stack = ((Player) (Object) this).getCurrentEquippedItem();

		int flameLevel = EnchantmentUtils.getLevel(stack, Enchantments.flame);
		int fireTime = flameLevel * 20; // level * second

		if (entity.remainingFireTicks < fireTime) {
			entity.remainingFireTicks = fireTime;
		}
	}
}
