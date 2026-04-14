package googy.betterwithenchanting.api;

import googy.betterwithenchanting.BetterWithEnchanting;
import net.minecraft.core.data.registry.Registry;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemBow;
import net.minecraft.core.item.ItemFishingRod;
import net.minecraft.core.item.tool.*;

import java.util.ArrayList;
import java.util.List;

import static googy.betterwithenchanting.BetterWithEnchanting.MOD_ID;
import static googy.betterwithenchanting.item.BetterWithEnchantingTags.*;

public class Enchantments extends Registry<Enchantment> {
	// WEAPONS
	public static final Enchantment FLAME;
	// MEELE
	public static final Enchantment QUICKSTRIKE;
//	public static final Enchantment KNOCKBACK;
//	public static final Enchantment LOOTING;
//	public static final Enchantment SLAYER;
//	public static final Enchantment BUTCHER;
	// RANGE
//	public static final Enchantment POWER;
//	public static final Enchantment MULTI_SHOT;
	// TOOLS
	public static final Enchantment HASTE;
	public static final Enchantment BAIT;
//	public static final Enchantment EFFICIENCY;
//	public static final Enchantment FORTUNE;
//	public static final Enchantment SILKTOUCH;
//	public static final Enchantment GOLDSEEKER;
	// SPECIAL
	public static final Enchantment UNBREAKING;
	public static final Enchantment BOTTLED_SCORE;

	/**
	 Rarities:
	 COMMON: 		10.0f,
	 UNCOMMON: 		 5.0f,
	 RARE: 			 2.0f,
	 VERY_RARE: 		 1.0f;
	 */

	static{
		HASTE = new Enchantment(MOD_ID, "haste")
			.setWeight(10.0f)
			.setTarget(item -> item instanceof ItemToolPickaxe || item instanceof ItemToolAxe || item instanceof ItemToolShovel)
			.setMinEnchantability(level -> (level - 1) + 11)
			.setMaxEnchatability(level -> (level - 1) + 61);

		UNBREAKING = new Enchantment(MOD_ID, "unbreaking")
			.setWeight(10.0f)
			.setMaxLevel(2)
			.setTarget(Item::isDamagable)
			.setMinEnchantability(level -> (level - 1) * 8 + 5)
			.setMaxEnchatability(level -> (level - 1) * 8 + 55);

		QUICKSTRIKE = new Enchantment(MOD_ID, "quickswing")
			.setWeight(2.0f)
			.setMaxLevel(3)
			.setTarget(item -> item instanceof ItemTool || item instanceof ItemToolSword)
			.setMinEnchantability(level -> (level - 1) + 11)
			.setMaxEnchatability(level -> (level - 1) + 61);

		FLAME = new Enchantment(MOD_ID, "flame")
			.setWeight(2.0f)
			.setMaxLevel(5)
			.setTarget(item -> item instanceof ItemToolSword || item instanceof ItemBow)
			.setMinEnchantability(level -> (level - 1) * 20 + 10)
			.setMaxEnchatability(level -> (level - 1) * 20 + 60);

		BAIT = new Bait(MOD_ID, "bait")
			.setWeight(2.0f)
			.setMaxLevel(3)
			.setTarget(item -> item instanceof ItemFishingRod)
			.setMinEnchantability(level -> (level - 1) * 9 + 15)
			.setMaxEnchatability(level -> (level - 1) * 9 + 65);

		BOTTLED_SCORE = new Enchantment(MOD_ID, "score")
			.setWeight(10.0f)
			.setMaxLevel(3)
			.setTarget(item -> item.id == BetterWithEnchanting.SCORE_BOTTLE.id);
	}

	public Enchantments(){
		this.register(HASTE.id(), HASTE);
		this.register(UNBREAKING.id(), UNBREAKING);
		this.register(QUICKSTRIKE.id(), QUICKSTRIKE);
		this.register(FLAME.id(), FLAME);
		this.register(BAIT.id(), BAIT);
		this.register(BOTTLED_SCORE.id(), BOTTLED_SCORE);
	}

	private static final Enchantments INSTANCE = new Enchantments();
	public static Enchantments getInstance(){
		return INSTANCE;
	}
}
