package googy.betterwithenchanting.mixins.mixin.enchantment;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import googy.betterwithenchanting.api.Enchantments;
import googy.betterwithenchanting.api.EnchantmentContainer;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.material.ToolMaterial;
import net.minecraft.core.item.tool.ItemTool;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = ItemTool.class, remap = false)
public abstract class ItemToolMixinHaste {

	@WrapOperation(method = "getStrVsBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/item/material/ToolMaterial;getEfficiency(Z)F"))
	private float enchanting$applyHaste(ToolMaterial instance, boolean haste, Operation<Float> original, ItemStack itemStack){
		float ret = original.call(instance, haste);
		int hasteLevel = EnchantmentContainer.getLevel(itemStack, Enchantments.HASTE);
		if(haste){
			return ret * (float)Math.pow(1.25f, hasteLevel);
		}
		return original.call(instance, true);
	}

}
