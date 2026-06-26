package googy.betterwithenchanting.api;

import googy.betterwithenchanting.item.EnchantingTags;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemStack;

import java.util.Objects;
import java.util.function.IntUnaryOperator;
import java.util.function.Predicate;

import static googy.betterwithenchanting.BetterWithEnchanting.TRANSLATE;

public class Enchantment {
	private final String id;
	private final String translationKey;
	private double weight = 10.0f;
	private int maxLevel = 1;
	private int minLevel = 1;
	private Predicate<Item> target = (item -> true);
	private IntUnaryOperator minEnchantability = level -> (level - 1) * 12;
	private IntUnaryOperator maxEnchatability = level -> this.minEnchantability.applyAsInt(level) + 12;
	private boolean hidden = false; // prevents the enchantment from appearing in table

	/**
	 * @deprecated Old enchantment system used numerical id, that are only here for compatibility reason.
	 * Do not use them!
	 */
	@Deprecated
	private int intID = -1;

	public Enchantment(String modID, String id) {
		this(modID, id, id.replace('_', '.'));
	}

	public Enchantment(String modID, String id, String translationKey) {
		this.id = String.format("%s:%s", modID, id);
		this.translationKey = String.format("enchantment.%s.%s", modID, translationKey);
	}

	public EnchantmentStack getDefaultStack(){
		return new EnchantmentStack(this,this.maxLevel);
	}

	public boolean canApply(Item item) {
		return target.test(item);
	}

	public String id() {
		return id;
	}

	public String translationKey() {
		return translationKey;
	}

	public double weight() {
		return weight;
	}

	public double getWeight(int level){
		return weight / Math.pow(1.25, Math.max(level - 1, 1));
	}

	public int maxLevel() {
		return maxLevel;
	}

	public int minLevel() {
		return minLevel;
	}

	public int getMinEnchantability(int level) {
		return this.minEnchantability.applyAsInt(level);
	}

	public int getMaxEnchantability(int level) {
		return this.maxEnchatability.applyAsInt(level);
	}

	public boolean hidden(){
		return this.hidden;
	}

	public final boolean canEnchant(ItemStack itemStack) {
		if (itemStack == null || itemStack.getItem().hasTag(EnchantingTags.UNECHANT)) {
			return false;
		}
		return this.canApply(itemStack.getItem());
	}

	Enchantment setWeight(double chance) {
		this.weight = chance;
		return this;
	}

	Enchantment setMaxLevel(int maxLevel) {
		this.maxLevel = maxLevel;
		return this;
	}

	Enchantment setMinLevel(int minLevel) {
		this.minLevel = minLevel;
		return this;
	}

	Enchantment setTarget(Predicate<Item> target) {
		this.target = target;
		return this;
	}

	Enchantment setMinEnchantability(IntUnaryOperator minEnchantability) {
		this.minEnchantability = minEnchantability;
		return this;
	}

	Enchantment setMaxEnchatability(IntUnaryOperator maxEnchatability) {
		this.maxEnchatability = maxEnchatability;
		return this;
	}

	Enchantment setHidden(boolean hidden) {
		this.hidden = hidden;
		return this;
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof Enchantment)) return false;
		Enchantment that = (Enchantment) object;
		return Objects.equals(id, that.id);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(id);
	}


	public String prettyToString() {
		String level = String.format(" %s", this.maxLevel() <= 1 ? "" : String.format("(%s)", this.maxLevel()));
		return String.format("%s%s", TRANSLATE.translateNameKey(this.translationKey()), level);
	}

	/**
	 * @deprecated Old enchantment system used numerical id, that are only here for compatibility reason.
	 * Do not use them!
	 */
	@Deprecated
	Enchantment setIntID(int intID) {
		this.intID = intID;
		return this;
	}

	/**
	 * @deprecated Old enchantment system used numerical id, that are only here for compatibility reason.
	 * Do not use them!
	 */
	@Deprecated
	public int getIntID(){
		return this.intID;
	}
}

