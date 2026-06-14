package googy.betterwithenchanting.api;

import googy.betterwithenchanting.BetterWithEnchanting;
import net.minecraft.core.data.registry.Registry;
import net.minecraft.core.item.Item;
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
	// ANGLE
	public static final Enchantment HASTE; // haste
	public static final Enchantment BAIT;// increase ore gain/fishing
	public static final Enchantment HAUL;// increase fish amount
	// TOOLS
	public static final Enchantment SCAVENGE; // find random extra loot from mining
	public static final Enchantment CATALYST;
	public static final Enchantment SEARING;
	public static final Enchantment FORTUNE;
	public static final Enchantment INSIGHT;
	public static final Enchantment CRUSH;
	// SPECIAL
	public static final Enchantment UNBREAKING;
	public static final Enchantment BOTTLED_SCORE;
	public static final Enchantment GLITCHCRAFT;
	// UNIQUE ENCHANTMENT FOR AXE

	// FUTURE UPDATES
	// MORE ENCHANTMENT FOR HOE
	// MORE ENCHANTMENT FOR SHEAR

	// ARMOR -> not sure what to do with those, also not in the mood of animating armour glint
//	public static final Enchantment THORN;
//	public static final Enchantment SPEED; // increases the movementspeed
//	public static final Enchantment VAULT; // increases the stepping hight
//	public static final Enchantment GILLS;

	/**
	 * Rarities:
	 * COMMON: 		10.0f,
	 * UNCOMMON: 		 5.0f,
	 * RARE: 			 2.0f,
	 * VERY_RARE: 	 1.0f;
	 */

	private Enchantments() {
	}

	private static final Enchantments INSTANCE = new Enchantments();

	public static Enchantments getInstance() {
		return INSTANCE;
	}

	static {
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
			.setTarget(item ->
				item instanceof ItemToolSword
				|| item instanceof ItemToolAxe
				|| item.hasTag(ENCHANT_TOOL)
			)
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
			.setTarget(ItemFishingRod.class::isInstance)
			.setEnchantability(0, 50, 0.5, 2)
			.build();

		HAUL = new EnchantmentBuilder(new Enchantment(MOD_ID, "haul"))
			.setWeight(1.0f)
			.setMaxLevel(4)
			.setTarget(ItemFishingRod.class::isInstance)
			.setEnchantability(0, 50, 0.5, 2)
			.build();

		BOTTLED_SCORE = new EnchantmentBuilder(new Enchantment(MOD_ID, "score"))
			.setWeight(10.0f)
			.setMaxLevel(4)
			.setTarget(item -> item.id == BetterWithEnchanting.SCORE_BOTTLE.id)
			.setEnchantability(0, 50, 0.75, 2)
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

		INSIGHT = new EnchantmentBuilder(new Enchantment(MOD_ID, "insight"))
			.setWeight(10.0f)
			.setMaxLevel(5)
			.setTarget(item ->
				item instanceof ItemToolPickaxe
					|| item instanceof ItemToolAxe
					|| item instanceof ItemToolShovel
					|| item instanceof ItemFishingRod
					|| item.hasTag(ENCHANT_DIGGER)
			)
			.setEnchantability(0, 50, 0.2f, 4)
			.build();

		/// trommel
		SCAVENGE = new EnchantmentBuilder(new Enchantment(MOD_ID, "scavenge"))
			.setWeight(5.0f)
			.setMaxLevel(1)
			.setTarget(item -> item instanceof ItemToolShovel || item.hasTag(ENCHANT_DIGGER))
			.setMinEnchantability(level -> 10)
			.setMaxEnchatability(level -> 60)
			.build();

		CATALYST = new EnchantmentBuilder(new Enchantment(MOD_ID, "catalyst"))
			.setWeight(2.0f)
			.setMaxLevel(5)
			.setTarget(item ->
				item instanceof ItemToolPickaxe
					|| item instanceof ItemToolAxe
					|| item instanceof ItemToolShovel
					|| item.hasTag(ENCHANT_DIGGER)
			)
			.setEnchantability(0, 50, 0.5f, 4)
			.build();

		FORTUNE = new EnchantmentBuilder(new Enchantment(MOD_ID, "fortune"))
			.setWeight(2.0f)
			.setMaxLevel(5)
			.setTarget(item ->
				item instanceof ItemToolPickaxe
					|| item instanceof ItemToolShovel
					|| item.hasTag(ENCHANT_DIGGER)
			)
			.setEnchantability(10, 50, 0.75f, 4)
			.build();

		SEARING = new EnchantmentBuilder(new Enchantment(MOD_ID, "searing"))
			.setWeight(1.0f)
			.setMaxLevel(1)
			.setTarget(item -> item instanceof ItemToolPickaxe
				|| item instanceof ItemToolAxe
				|| item instanceof ItemToolShovel
				|| item.hasTag(ENCHANT_DIGGER))
			.setMinEnchantability(level -> 10)
			.setMaxEnchatability(level -> 60)
			.build();

		// crushes cobble to gravel
		CRUSH = new EnchantmentBuilder(new Enchantment(MOD_ID, "crush"))
			.setWeight(1.0f)
			.setMaxLevel(1)
			.setTarget(item -> item instanceof ItemToolPickaxe || item.hasTag(ENCHANT_PICKAXE))
			.setMinEnchantability(level -> 10)
			.setMaxEnchatability(level -> 60)
			.build();

		// purely for commands
		GLITCHCRAFT = new EnchantmentBuilder(new Enchantment(MOD_ID, "glitch"))
			.setTarget(item -> false)
			.setHidden(true)
			.build();
	}

	public static void init() {/* just to load this class*/}
}
