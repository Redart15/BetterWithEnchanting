package googy.betterwithenchanting.api;

import googy.betterwithenchanting.item.EnchantmentItems;
import net.minecraft.core.data.registry.Registry;
import net.minecraft.core.item.*;
import net.minecraft.core.item.tool.*;

import static googy.betterwithenchanting.BetterWithEnchanting.MOD_ID;
import static googy.betterwithenchanting.item.EnchantmentTags.*;

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
	public static final Enchantment POWER;
	public static final Enchantment MULTI_SHOT;
	public static final Enchantment BUCK_SHOT;
	// ANGLE
	public static final Enchantment HASTE;
	public static final Enchantment BAIT;
	public static final Enchantment HAUL;
	public static final Enchantment REELING;
	// TOOLS
	public static final Enchantment SCAVENGE;
	public static final Enchantment CATALYST;
	public static final Enchantment SEARING;
	public static final Enchantment FORTUNE;
	public static final Enchantment INSIGHT;
	// PICKAXE
	public static final Enchantment CRUSH;
	public static final Enchantment SILKTOUCH;
	// AXE
	public static final Enchantment FELLING;
	// SHEAR
	public static final Enchantment FORAGING;
	// HANDCANON
	public static final Enchantment EXPLOSIVE;
	public static final Enchantment INCENDIARY;
	public static final Enchantment VOLATILE;
	public static final Enchantment PRECISE;
	// HOE
	public static final Enchantment REAP;
	// FOOD
	@Deprecated(since = "1.2.0", forRemoval = true)
	public static final Enchantment BOTTLED_SCORE;
	public static final Enchantment NOURISHMENT;
	public static final Enchantment FILLING;
	public static final Enchantment LASTING;
	// SPECIAL
	public static final Enchantment UNBREAKING;
	public static final Enchantment GLITCHCRAFT;

	// ARMOR -> not sure what to do with those, also not in the mood of animating armour glint
//	public static final Enchantment THORN;
//	public static final Enchantment SPEED; // increases the movementspeed
//	public static final Enchantment VAULT; // increases the stepping hight
//	public static final Enchantment GILLS;

	/**
	 * Rarities:
	 * COMMON: 			10.0f,
	 * UNCOMMON: 		 5.0f,
	 * RARE: 			 2.0f,
	 * VERY_RARE: 	 	 1.0f;
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
			.setTarget(
				item -> item instanceof ItemToolPickaxe
				|| item instanceof ItemToolAxe
				|| item instanceof ItemToolShovel
				|| item.hasTag(ENCHANT_PICKAXE)
				|| item.hasTag(ENCHANT_AXE)
				|| item.hasTag(ENCHANT_SHOVEL)
				|| item.hasTag(ENCHANT_TOOL)
			)
			.setTargetDescriptions("pickaxe", "axe", "shovel", "tool")
			.setMaxLevel(2)
			.setEnchantability(10, 60, 0.5, 2)
			.build();

		UNBREAKING = new EnchantmentBuilder(new Enchantment(MOD_ID, "unbreaking"), 2)
			.setWeight(10.0f)
			.setMaxLevel(2)
			.setTarget(Item::isDamagable)
			.setTargetDescriptions("damageable")
			.setEnchantability(0, 50, 0.5)
			.build();

		QUICKSTRIKE = new EnchantmentBuilder(new Enchantment(MOD_ID, "quickswing"), 3)
			.setWeight(2.0f)
			.setMaxLevel(3)
			.setTarget(item ->
				item instanceof ItemToolSword
					|| item instanceof ItemToolAxe
					|| item.hasTag(ENCHANT_MELEE)
					|| item.hasTag(ENCHANT_AXE)
			)
			.setTargetDescriptions("sword", "axe", "melee")
			.setEnchantability(10, 50, 0.5)
			.build();

		FLAME = new EnchantmentBuilder(new Enchantment(MOD_ID, "flame"), 5)
			.setWeight(1.0f)
			.setMaxLevel(5)
			.setTarget(item -> item instanceof ItemToolSword || item instanceof ItemBow || item.hasTag(ENCHANT_WEAPON))
			.setTargetDescriptions("sword", "bow", "meele", "ranged")
			.setEnchantability(0, 50, 0.75, 4)
			.build();

		// increase ore gain/fishing
		BAIT = new EnchantmentBuilder(new Bait(MOD_ID, "bait"), 8)
			.setWeight(2.0f)
			.setMaxLevel(3)
			.setTarget(item -> item instanceof ItemFishingRod || item.hasTag(ENCHANT_ROD))
			.setTargetDescriptions("rod")
			.setEnchantability(0, 50, 0.5, 2)
			.build();

		// increase fish amount
		HAUL = new EnchantmentBuilder(new Enchantment(MOD_ID, "haul"))
			.setWeight(1.0f)
			.setMaxLevel(4)
			.setTarget(item -> item instanceof ItemFishingRod || item.hasTag(ENCHANT_ROD))
			.setTargetDescriptions("rod")
			.setEnchantability(0, 50, 0.5, 2)
			.build();

		// chance to conserve food
		LASTING = new EnchantmentBuilder(new Enchantment(MOD_ID, "lasting"))
			.setWeight(10.0f)
			.setMaxLevel(5)
			.setTarget(item -> item instanceof ItemFood || item.hasTag(ENCHANT_CONSUMABLES))
			.setTargetDescriptions("food", "consumable")
			.setEnchantability(10, 50, 0.5f, 3)
			.build();

		// increase regeneration speed
		NOURISHMENT = new EnchantmentBuilder(new Enchantment(MOD_ID, "nourishment"))
			.setWeight(5.0f)
			.setMaxLevel(3)
			.setTarget(item -> item instanceof ItemFood || item.hasTag(ENCHANT_CONSUMABLES))
			.setTargetDescriptions("food", "consumable")
			.setEnchantability(0, 50, 0.5f)
			.build();

		// increases the amount of health restored
		FILLING = new EnchantmentBuilder(new Enchantment(MOD_ID, "filling"))
			.setWeight(2.0f)
			.setMaxLevel(2)
			.setTarget(item -> item instanceof ItemFood || item.hasTag(ENCHANT_CONSUMABLES))
			.setTargetDescriptions("food", "consumable")
			.setEnchantability(15, 65, 0.4f, 1)
			.build();

		BOTTLED_SCORE = new EnchantmentBuilder(new Enchantment(MOD_ID, "score"))
			.setWeight(1.0f)
			.setMaxLevel(4)
			.setTarget(item -> item.id == EnchantmentItems.SCORE_BOTTLE.id || item instanceof ItemFood)
			.setTargetDescriptions("food", "consumable")
			.setEnchantability(0, 50, 0.75, 2)
			.setHidden()
			.build();

		CRIT = new EnchantmentBuilder(new Enchantment(MOD_ID, "crit"))
			.setWeight(5.0f)
			.setMaxLevel(3)
			.setTarget(item -> item instanceof ItemToolSword || item.hasTag(ENCHANT_MELEE))
			.setTargetDescriptions("sword", "melee")
			.setEnchantability(0, 50, 0.5f)
			.build();

		SLAYER = new EnchantmentBuilder(new Enchantment(MOD_ID, "slayer"))
			.setWeight(5.0f)
			.setMaxLevel(3)
			.setTarget(item -> item instanceof ItemToolSword || item.hasTag(ENCHANT_MELEE))
			.setTargetDescriptions("sword", "melee")
			.setEnchantability(0, 50, 0.5f)
			.build();

		LIFESTEAL = new EnchantmentBuilder(new Enchantment(MOD_ID, "lifesteal"))
			.setWeight(1.0f)
			.setMaxLevel(1)
			.setTarget(item -> item instanceof ItemToolSword || item.hasTag(ENCHANT_MELEE))
			.setTargetDescriptions("sword", "melee")
			.setMinEnchantability(level -> 20)
			.setMaxEnchatability(level -> 50)
			.build();

		LOOTING = new EnchantmentBuilder(new Enchantment(MOD_ID, "looting"))
			.setWeight(5.0f)
			.setMaxLevel(5)
			.setTarget(item -> item instanceof ItemToolSword || item.hasTag(ENCHANT_MELEE))
			.setTargetDescriptions("sword", "melee")
			.setEnchantability(0, 50, 0.5f, 4)
			.build();

		KNOCKBACK = new EnchantmentBuilder(new Enchantment(MOD_ID, "knockback"))
			.setWeight(2.0f)
			.setMaxLevel(1)
			.setTarget(item -> item instanceof ItemToolSword || item.hasTag(ENCHANT_MELEE))
			.setTargetDescriptions("sword", "melee")
			.setMinEnchantability(level -> 10)
			.setMaxEnchatability(level -> 60)
			.build();

		// projtile speed
		POWER = new EnchantmentBuilder(new Enchantment(MOD_ID, "power"))
			.setWeight(5.0f)
			.setMaxLevel(3)
			.setTarget(
				item -> item instanceof ItemBow
					|| item instanceof ItemHandCannonUnloaded
					|| item instanceof ItemHandCannonLoaded
					|| item.hasTag(ENCHANT_RANGED)
			)
			.setTargetDescriptions("bow", "handcanon", "ranged")
			.setEnchantability(0, 50, 0.5f)
			.build();

		// increase proj(main)
		BUCK_SHOT = new EnchantmentBuilder(new Enchantment(MOD_ID, "buckhot"))
			.setWeight(2.0f)
			.setMaxLevel(2)
			.setTarget(item -> item instanceof ItemBow || item.hasTag(ENCHANT_RANGED))
			.setTargetDescriptions("bow", "ranged")
			.setEnchantability(10, 50, 0.25f)
			.build();

		// increase proj(extra)
		MULTI_SHOT = new EnchantmentBuilder(new Enchantment(MOD_ID, "multishot"))
			.setWeight(2.0f)
			.setMaxLevel(2)
			.setTarget(item -> item instanceof ItemBow || item.hasTag(ENCHANT_RANGED))
			.setTargetDescriptions("bow", "ranged")
			.setEnchantability(10, 50, 0.25f)
			.build();

		INSIGHT = new EnchantmentBuilder(new Enchantment(MOD_ID, "insight"))
			.setWeight(10.0f)
			.setMaxLevel(5)
			.setTarget(item -> (
					item instanceof ItemTool
					|| item instanceof ItemToolPickaxe
					|| item instanceof ItemToolAxe
					|| item instanceof ItemToolShovel
					|| item instanceof ItemFishingRod
					|| item instanceof ItemToolHoe
					|| item instanceof ItemToolShears
					|| item instanceof ItemFood
					|| item.hasTag(ENCHANT_TOOL)
					|| item.hasTag(ENCHANT_PICKAXE)
					|| item.hasTag(ENCHANT_AXE)
					|| item.hasTag(ENCHANT_SHOVEL)
					|| item.hasTag(ENCHANT_ROD)
					|| item.hasTag(ENCHANT_HOE)
					|| item.hasTag(ENCHANT_SHEARS)
				)
			)
			.setTargetDescriptions("pickaxe", "axe", "shovel", "rod", "hoe", "shears", "tool", "food")
			.setEnchantability(0, 50, 0.2f, 4)
			.build();

		// trommel
		SCAVENGE = new EnchantmentBuilder(new Enchantment(MOD_ID, "scavenge"))
			.setWeight(5.0f)
			.setMaxLevel(1)
			.setTarget(item -> item instanceof ItemToolShovel || item.hasTag(ENCHANT_SHOVEL))
			.setTargetDescriptions("shovel")
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
					|| item.hasTag(ENCHANT_PICKAXE)
					|| item.hasTag(ENCHANT_SHOVEL)
					|| item.hasTag(ENCHANT_AXE)
					|| item.hasTag(ENCHANT_TOOL)
			)
			.setTargetDescriptions("pickaxe", "axe", "shovel", "tool")
			.setEnchantability(0, 50, 0.5f, 4)
			.build();

		FORTUNE = new EnchantmentBuilder(new Enchantment(MOD_ID, "fortune"))
			.setWeight(2.0f)
			.setMaxLevel(5)
			.setTarget(item ->
				item instanceof ItemToolPickaxe
					|| item instanceof ItemToolShovel
					|| item.hasTag(ENCHANT_PICKAXE)
					|| item.hasTag(ENCHANT_SHOVEL)
			)
			.setTargetDescriptions("pickaxe", "shovel")
			.setEnchantability(10, 50, 0.75f, 4)
			.build();

		SEARING = new EnchantmentBuilder(new Enchantment(MOD_ID, "searing"))
			.setWeight(1.0f)
			.setMaxLevel(1)
			.setTarget(item -> item instanceof ItemToolPickaxe
				|| item instanceof ItemToolAxe
				|| item instanceof ItemToolShovel
				|| item.hasTag(ENCHANT_PICKAXE)
				|| item.hasTag(ENCHANT_AXE)
				|| item.hasTag(ENCHANT_SHOVEL)
			)
			.setTargetDescriptions("pickaxe", "axe", "shovel")
			.setMinEnchantability(level -> 10)
			.setMaxEnchatability(level -> 60)
			.build();

		// crushes cobble to gravel
		CRUSH = new EnchantmentBuilder(new Enchantment(MOD_ID, "crush"))
			.setWeight(1.0f)
			.setMaxLevel(1)
			.setTarget(item -> item instanceof ItemToolPickaxe || item.hasTag(ENCHANT_PICKAXE))
			.setTargetDescriptions("pickaxe")
			.setMinEnchantability(level -> 10)
			.setMaxEnchatability(level -> 60)
			.build();

		// purely for commands
		GLITCHCRAFT = new EnchantmentBuilder(new Enchantment(MOD_ID, "glitch"))
			.setTarget(item -> false)
			.setTargetDescriptions("all")
			.setHidden(true)
			.build();

		// drops blocks as if mined with silktouch
		SILKTOUCH = new EnchantmentBuilder(new Enchantment(MOD_ID, "silktouch"))
			.setWeight(1.0f)
			.setTarget(item -> (item instanceof ItemToolPickaxe || item.hasTag(ENCHANT_PICKAXE)) && !item.isSilkTouch())
			.setTargetDescriptions("pickaxe")
			.setMinEnchantability(level -> 30)
			.setMaxEnchatability(level -> 60)
			.build();

		// tree capitator
		FELLING = new EnchantmentBuilder(new Enchantment(MOD_ID, "felling"))
			.setWeight(1.0f)
			.setTarget(item -> item instanceof ItemToolAxe || item.hasTag(ENCHANT_AXE))
			.setTargetDescriptions("axe")
			.setMinEnchantability(level -> 15)
			.setMaxEnchatability(level -> 30)
			.build();

		FORAGING = new EnchantmentBuilder(new Enchantment(MOD_ID, "foraging"))
			.setWeight(5.0f)
			.setMaxLevel(4)
			.setTarget(item -> item instanceof ItemToolShears || item.hasTag(ENCHANT_SHEARS))
			.setTargetDescriptions("shears")
			.setEnchantability(10, 60, 0.25, 3)
			.build();

		// increase explosive power
		EXPLOSIVE = new EnchantmentBuilder(new Enchantment(MOD_ID, "explosive"))
			.setWeight(5.0f)
			.setMaxLevel(3)
			.setTarget(
				item -> item instanceof ItemHandCannonLoaded
				|| item instanceof ItemHandCannonUnloaded
				|| item.hasTag(ENCHANT_RANGED)
			)
			.setTargetDescriptions("handcannon", "ranged")
			.setEnchantability(20, 60, 0.8f)
			.build();

		// makes the explosion cause damage to the environment
		VOLATILE = new EnchantmentBuilder(new Enchantment(MOD_ID, "volatile"))
			.setWeight(2.0f)
			.setTarget(
				item -> item instanceof ItemHandCannonLoaded
				|| item instanceof ItemHandCannonUnloaded
				|| item.hasTag(ENCHANT_RANGED)
			)
			.setTargetDescriptions("handcannon", "ranged")
			.setMinEnchantability(level -> 25)
			.setMaxEnchatability(level -> 35)
			.build();

		// makes it hit entities
		PRECISE = new EnchantmentBuilder(new Enchantment(MOD_ID, "precise"))
			.setWeight(1.0f)
			.setMaxLevel(5)
			.setTarget(
				item -> item instanceof ItemHandCannonLoaded
				|| item instanceof ItemHandCannonUnloaded
				|| item.hasTag(ENCHANT_RANGED)
			)
			.setTargetDescriptions("handcannon", "ranged")
			.setEnchantability(10, 50, 0.8f, 3)
			.build();

		// cause fire
		INCENDIARY = new EnchantmentBuilder(new Enchantment(MOD_ID, "incendiary"))
			.setWeight(1.0f)
			.setTarget(item -> item instanceof ItemHandCannonLoaded
				|| item instanceof ItemHandCannonUnloaded
				|| item.hasTag(ENCHANT_RANGED)
			)
			.setTargetDescriptions("handcannon", "ranged")
			.setMinEnchantability(level -> 25)
			.setMaxEnchatability(level -> 35)
			.build();

		// TODO: implement them!
		//------------------------------------------- lang missing --------------------------
		// increase field
		REAP = new EnchantmentBuilder(new Enchantment(MOD_ID, "reap"))
			.setWeight(10.0f)
			.setMaxLevel(2)
			.setTarget(item -> item instanceof ItemToolHoe || item.hasTag(ENCHANT_HOE))
			.setTargetDescriptions("hoe")
			.setEnchantability(10, 50, 0.8f)
			.setHidden()
			.build();

		// TODO: implement them!
		// increase speed of entity beeing yoinked
		REELING = new EnchantmentBuilder(new Enchantment(MOD_ID, "reeling"))
			.setWeight(1.0f)
			.setMaxLevel(2)
			.setTarget(item -> item instanceof ItemFishingRod || item.hasTag(ENCHANT_ROD))
			.setTargetDescriptions("hoe")
			.setEnchantability(10, 50, 0.4f)
			.setHidden()
			.build();

	}

	public static void init() {/* just to load this class*/}
}
