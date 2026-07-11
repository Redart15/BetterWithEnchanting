package googy.betterwithenchanting.mixins.mixin.uselessFixedIt;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.gui.achievements.ScreenAchievements;
import net.minecraft.client.gui.achievements.data.AchievementPage;
import net.minecraft.client.render.item.model.ItemModel;
import net.minecraft.client.render.tessellator.TessellatorGeneral;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = ScreenAchievements.class, remap = false)
public abstract class FixRenderingAchievement {

	@WrapOperation(method = "drawAchievementIcons", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/item/model/ItemModel;renderGui(Lnet/minecraft/client/render/tessellator/TessellatorGeneral;Lnet/minecraft/core/entity/Entity;Lnet/minecraft/core/item/ItemStack;IIBF)V"))
	private void renderFix(
		ItemModel instance, TessellatorGeneral tessellator,
		Entity holder, ItemStack itemStack,
		int x, int y, byte lightIndex, float partialTick,
		Operation<Void> original,
		@Local AchievementPage.AchievementEntry ach
	){
		original.call(instance, tessellator, holder, ach.achievement.iconStack, x, y, lightIndex, partialTick);
	}

}
