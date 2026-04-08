package googy.betterwithenchanting.enchantment.enchantments;

import googy.betterwithenchanting.enchantment.Enchantment;
import googy.betterwithenchanting.enchantment.EnchantmentTarget;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.lang.I18n;

public class BaitEnchantment extends Enchantment {
	public BaitEnchantment(String name, int id, Rarity weight, EnchantmentTarget target) {
		super(name, id, weight, target);
	}

	@Override
	public int getMaxLevel() {
		return 3;
	}

	@Override
	public int getMinEnchantability(int level) {
		return 15 + (level - 1) * 9;
	}

	@Override
	public int getMaxEnchantability(int level) {
		return super.getMaxEnchantability(level) + 50;
	}

	@Override
	public String getName() {
		if(FabricLoader.getInstance().isModLoaded("stardew")){
			return I18n.getInstance().translateKey("disabled");
		}
		return super.getName();
	}
}
