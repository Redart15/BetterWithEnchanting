package googy.betterwithenchanting.mixins.interfaces;

public interface ContainerHotbarLocking {

	void enchanted$lockSlot(int slotID, boolean lockBoolean);
	boolean enchanted$isLocked(int slotID);
}
