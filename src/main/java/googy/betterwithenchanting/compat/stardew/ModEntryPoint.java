package googy.betterwithenchanting.compat.stardew;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;
import org.spongepowered.asm.mixin.Mixins;

public class ModEntryPoint implements PreLaunchEntrypoint {
	@Override
	public void onPreLaunch() {
		if(!FabricLoader.getInstance().isModLoaded("stardew")){
			Mixins.addConfiguration("compat/stardew.mixins.json");
		}
	}
}
