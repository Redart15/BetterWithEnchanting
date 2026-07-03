package googy.betterwithenchanting.mixins.mixin.enchantment;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.nbt.tags.CompoundTag;
import googy.betterwithenchanting.mixins.MixinsHelperLogic;
import googy.betterwithenchanting.mixins.interfaces.AdditionalNBTTag;
import googy.betterwithenchanting.mixins.interfaces.EnchantmentCannonball;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.projectile.ProjectileCannonball;
import net.minecraft.core.util.phys.HitResult;
import net.minecraft.core.world.Explosion;
import net.minecraft.core.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ProjectileCannonball.class, remap = false)
public class ProjectileCannonballMixinEnchantment implements EnchantmentCannonball, AdditionalNBTTag {

	@Unique
	private static final String IS_VOLATILE = "isVolatile";
	@Unique
	private static final String IS_INCENDIARY = "isIncendiary";
	@Unique
	private static final String IS_PRECISE = "isPrecise";
	@Unique
	private static final String IS_EXPLOSIVE = "isExplosive";
	@Unique
	private byte isExplosive = 0;
	@Unique
	private boolean isIncendiary = false;
	@Unique
	private boolean isVolatile = false;
	@Unique
	private boolean isPrecice = false;

	@Override
	public void enchanting$writeExplosive(byte level) {
		this.isExplosive = level;
	}

	@Override
	public void enchanting$writeIncendiary() {
		this.isIncendiary = true;
	}

	@Override
	public void enchanting$writeVolatile() {
		this.isVolatile = true;
	}

	@Override
	public void enchanting$writeprecise() {
		this.isPrecice = true;
	}

	@WrapOperation(method = "onHit", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/world/World;createExplosion(Lnet/minecraft/core/entity/Entity;DDDFZZ)Lnet/minecraft/core/world/Explosion;"))
	private Explosion modifyExplosion(
		World instance, Entity owner,
		double x, double y, double z, float explosionSize,
		boolean flaming, boolean isCannonball,
		Operation<Explosion> original,
		@Local HitResult hitResult
		) {
		float newExplosionSize = explosionSize + (float) Math.pow(1.2f, this.isExplosive);
		boolean newFlaming = (flaming || this.isIncendiary);
		boolean newVolatile = (isCannonball && !this.isVolatile);
		Explosion returnValue = original.call(instance, owner, x, y, z, newExplosionSize, newFlaming, newVolatile);
		if((newFlaming && newVolatile)){
			MixinsHelperLogic.createFire(instance, ((HitResult.Tile) hitResult).tilePos, newExplosionSize);
		}
		return returnValue;
	}

	@Inject(method = "onHit", at = @At("TAIL"))
	private void preciseHitting(HitResult hitResult, CallbackInfo ci) {
		if (this.isPrecice && hitResult instanceof HitResult.Entity hitEntity) {
			ProjectileCannonball asThis = (ProjectileCannonball) (Object) this;
			float newExplosionSize = 1.5F + (float) Math.pow(1.2f, this.isExplosive);
			asThis.world.createExplosion(asThis.owner, asThis.x, asThis.y + (asThis.bbHeight / 2.0F), asThis.z, newExplosionSize, this.isIncendiary, !this.isVolatile);
			if(this.isIncendiary && !this.isVolatile){
				MixinsHelperLogic.createFire(asThis.world, hitEntity.entity.x, hitEntity.entity.y, hitEntity.entity.z, newExplosionSize);
			}
		}
	}

	@Override
	public void enchanting$readAdditionalSaveData(CompoundTag tag) {
		if (tag.containsKey(IS_VOLATILE)) {
			this.isVolatile = tag.getByte(IS_VOLATILE) == 0;
		}
		if (tag.containsKey(IS_INCENDIARY)) {
			this.isIncendiary = tag.getByte(IS_INCENDIARY) == 0;
		}
		if (tag.containsKey(IS_PRECISE)) {
			this.isPrecice = tag.getByte(IS_PRECISE) == 0;
		}
		if (tag.containsKey(IS_EXPLOSIVE)) {
			this.isExplosive = tag.getByte(IS_EXPLOSIVE);
		}
	}

	@Override
	public void enchanting$addAdditionalSaveData(CompoundTag tag) {
		tag.putByte(IS_VOLATILE, this.isVolatile ? (byte) 1 : (byte) 0);
		tag.putByte(IS_INCENDIARY, this.isIncendiary ? (byte) 1 : (byte) 0);
		tag.putByte(IS_PRECISE, this.isPrecice ? (byte) 1 : (byte) 0);
		tag.putByte(IS_EXPLOSIVE, this.isExplosive);
	}
}
