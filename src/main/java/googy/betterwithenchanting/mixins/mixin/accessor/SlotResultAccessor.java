package googy.betterwithenchanting.mixins.mixin.accessor;

import net.minecraft.core.entity.player.Player;
import net.minecraft.core.player.inventory.slot.SlotResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(SlotResult.class)
public interface SlotResultAccessor {
	@Accessor
	Player getThePlayer();
}
