package googy.betterwithenchanting.mixins.interfaces;

public interface ContainerHotbarLocking {

	int enchanted$getValue();
	void enchanted$setValue(int lockState);
	void enchanted$lockSlot(int slotID, boolean lockBoolean);
	boolean enchanted$isLocked(int slotID);
}
