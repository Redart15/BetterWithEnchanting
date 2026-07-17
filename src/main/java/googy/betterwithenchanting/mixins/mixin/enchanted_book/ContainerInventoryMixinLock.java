package googy.betterwithenchanting.mixins.mixin.enchanted_book;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import googy.betterwithenchanting.mixins.interfaces.ContainerHotbarLocking;
import net.minecraft.core.player.inventory.container.ContainerInventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(value = ContainerInventory.class, remap = false)
public class ContainerInventoryMixinLock implements ContainerHotbarLocking {

	@Unique
	private int lock = 0;


	@Unique
	public void enchanted$lockSlot(int slotID, boolean lockBoolean){
		if(slotID > 9 || slotID < 0){
			return;
		}
		if (lockBoolean) {
			this.lock = this.lock | (1 << slotID);
		}else{
			this.lock = this.lock & ~(1 << slotID);
		}
	}

	@Unique
	public boolean enchanted$isLocked(int slotID){
		if(slotID > 9 || slotID < 0){
			return false;
		}
		return enchanted$getBit(slotID) == 1;
	}

	@Unique
	private int enchanted$getBit(int slotID) {
		return ((this.lock & 0b1111_1111_1) >>> slotID) & 1;
	}


	@WrapMethod(method = "currentSlotLocked")
	private boolean improvedCurrentSlotLock(Operation<Boolean> original){
		ContainerInventory asThis = (ContainerInventory) (Object) this;
		return original.call() || this.enchanted$isLocked(asThis.getCurrentSlot());
	}
}
