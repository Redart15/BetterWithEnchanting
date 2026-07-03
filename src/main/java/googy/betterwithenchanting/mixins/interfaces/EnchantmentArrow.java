package googy.betterwithenchanting.mixins.interfaces;

public interface EnchantmentArrow {
	int enchanting$readFlame();
	void enchanting$writeFlame(byte level);

	boolean enchanting$readMultiHit();
	void enchanting$writeMultiHit(boolean level);
}
