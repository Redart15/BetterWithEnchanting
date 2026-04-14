package googy.betterwithenchanting.api;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.lang.I18n;

public class Bait extends Enchantment {
	public Bait(String modID, String id) {
		super(modID, id);
	}

	@Override
	public String translationKey() {
		if (FabricLoader.getInstance().isModLoaded("stardew")) {
			return I18n.getInstance().translateKey("disabled");
		}
		return super.translationKey();
	}

}
