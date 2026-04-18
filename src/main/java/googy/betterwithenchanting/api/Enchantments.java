package googy.betterwithenchanting.api;

import googy.betterwithenchanting.BetterWithEnchanting;
import net.minecraft.core.data.registry.Registry;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemArmor;
import net.minecraft.core.item.ItemBow;
import net.minecraft.core.item.ItemFishingRod;
import net.minecraft.core.item.tool.*;

import static googy.betterwithenchanting.BetterWithEnchanting.MOD_ID;
import static googy.betterwithenchanting.item.EnchantingTags.*;

public class Enchantments extends Registry<Enchantment> {
	// WEAPONS
	public static final Enchantment FLAME;
	// MEELE
	public static final Enchantment QUICKSTRIKE;
	public static final Enchantment KNOCKBACK;
	public static final Enchantment LOOTING;
	public static final Enchantment SLAYER;
	public static final Enchantment CRIT;
	public static final Enchantment LIFESTEAL;
	// RANGE
	public static final Enchantment POWER; // projtile speed
	public static final Enchantment MULTI_SHOT; // increase proj(main)
	public static final Enchantment BUCK_SHOT; // increase proj(extra)
	// TOOLS
	public static final Enchantment HASTE; // haste
	public static final Enchantment BAIT;
	public static final Enchantment FORTUNE;  // increase ore gain/fishing
	public static final Enchantment SCAVANGE; // find random extra loot from mining
	// SPECIAL
	public static final Enchantment UNBREAKING;
	public static final Enchantment BOTTLED_SCORE;
	// ARMOR
	public static final Enchantment THORN;
	public static final Enchantment SPEED; // increases the movementspeed
	public static final Enchantment VAULT; // increases the stepping hight
	public static final Enchantment GILLS;

	/**
	 Rarities:
	 COMMON: 		10.0f,
	 UNCOMMON: 		 5.0f,
	 RARE: 			 2.0f,
	 VERY_RARE: 	 1.0f;
	 */

	private Enchantments(){}
	private static Enchantments INSTANCE = new Enchantments();
	public static Enchantments getInstance(){
		return INSTANCE;
	}

	static{
		HASTE = new EnchantmentBuilder(new Enchantment(MOD_ID, "haste"), 1)
			.setWeight(10.0f)
			.setTarget(item ->
				item instanceof ItemToolPickaxe
				|| item instanceof ItemToolAxe
				|| item instanceof ItemToolShovel
				|| item.hasTag(ENCHANT_DIGGER)
			)
			.setMinEnchantability(level -> 10)
			.setMaxEnchatability(level -> 60)
			.build();

		UNBREAKING = new EnchantmentBuilder(new Enchantment(MOD_ID, "unbreaking"), 2)
			.setWeight(10.0f)
			.setMaxLevel(2)
			.setTarget(Item::isDamagable)
			.setEnchantability(0, 50, 0.5)
			.build();

		QUICKSTRIKE = new EnchantmentBuilder(new Enchantment(MOD_ID, "quickswing"), 3)
			.setWeight(2.0f)
			.setMaxLevel(3)
			.setTarget(item -> item instanceof ItemTool || item instanceof ItemToolSword || item.hasTag(ENCHANT_TOOL))
			.setEnchantability(10, 50, 0.5)
			.build();

		FLAME = new EnchantmentBuilder(new Enchantment(MOD_ID, "flame"), 5)
			.setWeight(1.0f)
			.setMaxLevel(5)
			.setTarget(item -> item instanceof ItemToolSword || item instanceof ItemBow || item.hasTag(ENCHANT_WEAPON))
			.setEnchantability(0, 50, 0.75, 4)
			.build();

		BAIT = new EnchantmentBuilder(new Bait(MOD_ID, "bait"), 8)
			.setWeight(2.0f)
			.setMaxLevel(3)
			.setTarget(item -> item instanceof ItemFishingRod)
			.setEnchantability(0, 50, 0.5)
			.build();

		BOTTLED_SCORE = new EnchantmentBuilder(new Enchantment(MOD_ID, "score"))
			.setWeight(10.0f)
			.setMaxLevel(4)
			.setTarget(item -> item.id == BetterWithEnchanting.SCORE_BOTTLE.id)
			.setEnchantability(0, 50, 0.75, 3)
			.build();

		CRIT = new EnchantmentBuilder(new Enchantment(MOD_ID, "crit"))
			.setWeight(5.0f)
			.setMaxLevel(3)
			.setTarget(item -> item instanceof ItemToolSword || item.hasTag(ENCHANT_MEELE))
			.setEnchantability(0, 50, 0.5f)
			.build();

		SLAYER = new EnchantmentBuilder(new Enchantment(MOD_ID, "slayer"))
			.setWeight(5.0f)
			.setMaxLevel(3)
			.setTarget(item -> item instanceof ItemToolSword || item.hasTag(ENCHANT_MEELE))
			.setEnchantability(0, 50, 0.5f)
			.build();

		LIFESTEAL = new EnchantmentBuilder(new Enchantment(MOD_ID, "lifesteal"))
			.setWeight(1.0f)
			.setMaxLevel(1)
			.setTarget(item -> item instanceof ItemToolSword || item.hasTag(ENCHANT_MEELE))
			.setMinEnchantability(level -> 20)
			.setMaxEnchatability(level -> 50)
			.build();

		LOOTING = new EnchantmentBuilder(new Enchantment(MOD_ID, "looting"))
			.setWeight(5.0f)
			.setMaxLevel(5)
			.setTarget(item -> item instanceof ItemToolSword || item.hasTag(ENCHANT_MEELE))
			.setEnchantability(0, 50, 0.5f, 4)
			.build();

		/// up here need to be implemented

		KNOCKBACK = new EnchantmentBuilder(new Enchantment(MOD_ID, "knockback"))
			.setWeight(2.0f)
			.setMaxLevel(1)
			.setTarget(item -> item instanceof ItemToolSword || item.hasTag(ENCHANT_MEELE))
			.setMinEnchantability(level -> 10)
			.setMaxEnchatability(level -> 60)
			.build();

		POWER = new EnchantmentBuilder(new Enchantment(MOD_ID, "power"))
			.setWeight(5.0f)
			.setMaxLevel(3)
			.setTarget(item -> item instanceof ItemBow || item.hasTag(ENCHANT_RANGED))
			.setEnchantability(0, 50, 0.5f)
			.build();

		BUCK_SHOT = new EnchantmentBuilder(new Enchantment(MOD_ID, "buckhot"))
			.setWeight(2.0f)
			.setMaxLevel(2)
			.setTarget(item -> item instanceof ItemBow || item.hasTag(ENCHANT_RANGED))
			.setEnchantability(10, 50, 0.25f)
			.build();

		MULTI_SHOT = new EnchantmentBuilder(new Enchantment(MOD_ID, "multishot"))
			.setWeight(2.0f)
			.setMaxLevel(2)
			.setTarget(item -> item instanceof ItemBow || item.hasTag(ENCHANT_RANGED))
			.setEnchantability(10, 50, 0.25f)
			.build();


		FORTUNE = new EnchantmentBuilder(new Enchantment(MOD_ID, "fortune"))
			.setWeight(1.0f)
			.setMaxLevel(3)
			.setTarget(item -> item instanceof ItemToolPickaxe || item.hasTag(ENCHANT_PICKAXE))
			.setEnchantability(20, 50, 0.5f, 2)
			.build();

		SCAVANGE = new EnchantmentBuilder(new Enchantment(MOD_ID, "scavange"))
			.setWeight(5.0f)
			.setMaxLevel(5)
			.setTarget(item ->
				item instanceof ItemToolPickaxe
					|| item instanceof ItemToolAxe
					|| item instanceof ItemToolShovel
					|| item.hasTag(ENCHANT_DIGGER)
			)
			.setEnchantability(0, 50, 0.75f, 4)
			.build();

		THORN = new EnchantmentBuilder(new Enchantment(MOD_ID, "thorn"))
			.setWeight(5.0f)
			.setMaxLevel(1)
			.setTarget(item -> item instanceof ItemArmor || item.hasTag(ENCHANT_ARMOR))
			.setMinEnchantability(level -> 20)
			.setMaxEnchatability(level -> 60)
			.build();

		SPEED = new EnchantmentBuilder(new Enchantment(MOD_ID, "swiftness"))
			.setWeight(5.0f)
			.setMaxLevel(3)
			.setTarget(item -> item instanceof ItemArmor || item.hasTag(ENCHANT_ARMOR))
			.setEnchantability(0, 50, 0.6f, 2)
			.build();

		VAULT = new EnchantmentBuilder(new Enchantment(MOD_ID, "vault"))
			.setWeight(5.0f)
			.setMaxLevel(3)
			.setTarget(item -> item instanceof ItemArmor || item.hasTag(ENCHANT_ARMOR))
			.setEnchantability(0, 50, 0.6f, 2)
			.build();

		GILLS = new EnchantmentBuilder(new Enchantment(MOD_ID, "gills"))
			.setWeight(1.0f)
			.setMaxLevel(1)
			.setTarget(item -> item instanceof ItemArmor || item.hasTag(ENCHANT_ARMOR))
			.setMinEnchantability(level -> 30)
			.setMaxEnchatability(level -> 60)
			.build();
	}

	public static void init(){
		if(INSTANCE == null){
			INSTANCE = new Enchantments();
		}
	}
}
