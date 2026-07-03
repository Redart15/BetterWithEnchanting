package googy.betterwithenchanting.mixins.mixin.enchantment;

import com.mojang.nbt.tags.CompoundTag;
import googy.betterwithenchanting.mixins.interfaces.AdditionalNBTTag;
import net.minecraft.core.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Entity.class, remap = false)
public abstract class EntityAdditionalSaves {

	@Inject(method = "load", at = @At("TAIL"))
	private void additionalLoad(CompoundTag tag, CallbackInfo ci){
		Entity asThis = (Entity) (Object) this;
		if(asThis instanceof AdditionalNBTTag additionalNBTTag) {
			additionalNBTTag.enchanting$readAdditionalSaveData(tag);
		}
	}


	@Inject(method = "saveWithoutId", at = @At("TAIL"))
	private void additionalAdd(CompoundTag tag, CallbackInfo ci){
		Entity asThis = (Entity) (Object) this;
		if(asThis instanceof AdditionalNBTTag additionalNBTTag) {
			additionalNBTTag.enchanting$addAdditionalSaveData(tag);
		}
	}

}
