package googy.betterwithenchanting.mixins.mixin.uselessFixedIt;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import googy.betterwithenchanting.mixins.mixin.accessor.AchievementToastAccessor;
import net.minecraft.client.gui.toasts.AchievementToast;
import net.minecraft.core.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = AchievementToast.class, remap = false)
public abstract class AchievementToastMixinFix {

	@WrapMethod(method = "getIcon")
	private ItemStack returnCorrectStack(long runtime, Operation<ItemStack> original){
		return ((AchievementToastAccessor)this).getAchievement().iconStack;
	}

}
