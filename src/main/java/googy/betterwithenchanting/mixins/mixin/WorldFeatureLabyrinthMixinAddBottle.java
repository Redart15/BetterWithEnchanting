package googy.betterwithenchanting.mixins.mixin;

import googy.betterwithenchanting.api.EnchantmentContainer;
import googy.betterwithenchanting.api.Enchantments;
import googy.betterwithenchanting.item.EnchantmentItems;
import net.minecraft.core.WeightedRandomBag;
import net.minecraft.core.WeightedRandomLootObject;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.world.World;
import net.minecraft.core.world.generate.feature.WorldFeatureLabyrinth;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Random;

@Mixin(value = WorldFeatureLabyrinth.class, remap = false)
public abstract class WorldFeatureLabyrinthMixinAddBottle {
	@Shadow
	public WeightedRandomBag<WeightedRandomLootObject> chestLoot;
	@Inject(method = "place", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/WeightedRandomBag;addEntry(Ljava/lang/Object;D)V", ordinal = 7, shift = At.Shift.AFTER))
	private void addCustomLoot(World world, Random random, int x, int y, int z, CallbackInfoReturnable<Boolean> cir) {
		for(int i = 0; i < Enchantments.BOTTLED_SCORE.maxLevel(); i++){
			ItemStack bottle = EnchantmentItems.SCORE_BOTTLE.getDefaultStack();
			EnchantmentContainer.rawAddEnchantment(bottle, Enchantments.BOTTLED_SCORE.getDefaultStack().setLevel(i + 1));
			this.chestLoot.addEntry(new WeightedRandomLootObject(bottle), 16.0F / Math.pow(2, i));
		}
	}

}
