package googy.betterwithenchanting.mixins.mixin.hudcomponent;

import googy.betterwithenchanting.BetterWithEnchantingClient;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.option.GameSettings;
import org.spongepowered.asm.mixin.Mixin;

@Environment(EnvType.CLIENT)
@Mixin(value = GameSettings.class)
public class GameSettingsMixin {
	static {
		BetterWithEnchantingClient.gameSettingsRegistry();
	}
}
