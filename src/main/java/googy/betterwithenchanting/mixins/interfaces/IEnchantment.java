package googy.betterwithenchanting.mixins.interfaces;

public interface IEnchantment {
	int enchanting$readFlame();
	void enchanting$writeFlame(int level);

	boolean enchanting$readMultiHit();
	void enchanting$writeMultiHit(boolean level);
}
