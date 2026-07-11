package googy.betterwithenchanting.mixins.mixin.accessor;

import net.minecraft.client.gui.toasts.AchievementToast;
import net.minecraft.core.achievement.Achievement;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AchievementToast.class)
public interface AchievementToastAccessor {
	@Accessor
	Achievement getAchievement();
}
