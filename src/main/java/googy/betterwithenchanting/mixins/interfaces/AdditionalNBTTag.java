package googy.betterwithenchanting.mixins.interfaces;

import com.mojang.nbt.tags.CompoundTag;

public interface AdditionalNBTTag {
	void enchanting$readAdditionalSaveData(CompoundTag tag);

	void enchanting$addAdditionalSaveData(CompoundTag tag);
}
