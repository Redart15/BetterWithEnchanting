package googy.betterwithenchanting.mixins.mixin.enchantment;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.nbt.tags.CompoundTag;
import googy.betterwithenchanting.mixins.mixin.accessor.MobAccessor;
import googy.betterwithenchanting.mixins.mixin.accessor.EntityAccessor;
import googy.betterwithenchanting.mixins.interfaces.EnchantmentArrow;
import net.minecraft.core.Global;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.block.material.MaterialLiquid;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.Mob;
import net.minecraft.core.entity.projectile.ProjectileArrow;
import net.minecraft.core.util.helper.DamageType;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.util.phys.HitResult;
import net.minecraft.core.world.pos.TilePos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ProjectileArrow.class, remap = false)
public abstract class ProjectileArrowMixinBowEnchantment implements EnchantmentArrow {

	@Unique
	public static final String IS_MULTI_HIT = "isMultiHit";
	@Unique
	public static final String IS_A_FLAME = "isAFlame";
	@Unique
    private byte isAFlame = 0;
    @Unique
    private boolean multiHit = false;

    @Override
    public int enchanting$readFlame() {
        return this.isAFlame;
    }

    @Override
    public void enchanting$writeFlame(byte level) {
        this.isAFlame = level;
    }

    @Override
    public boolean enchanting$readMultiHit() {
        return this.multiHit;
    }

    @Override
    public void enchanting$writeMultiHit(boolean multiHit) {
        this.multiHit = multiHit;
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void readIsAFlame(CompoundTag tag, CallbackInfo ci) {
        if (tag.containsKey(IS_A_FLAME)) {
            this.isAFlame = tag.getByte(IS_A_FLAME);
        }
        if (tag.containsKey(IS_MULTI_HIT)) {
            this.multiHit = tag.getBoolean(IS_MULTI_HIT);
        }
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void addIsAFlame(CompoundTag tag, CallbackInfo ci) {
        tag.putByte(IS_A_FLAME, this.isAFlame);
        tag.putBoolean(IS_MULTI_HIT, this.multiHit);
    }

    @WrapOperation(method = "onHit", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/entity/Entity;fireHurt()V"))
    private void setAFlame(Entity instance, Operation<Void> original) {
        int flameLevel = this.enchanting$readFlame();
        int fireTime = Math.max(flameLevel * Global.TICKS_PER_SECOND, 0);
        if (flameLevel > 0 && !((EntityAccessor) instance).isFireImmune() && fireTime > instance.remainingFireTicks) {
            instance.remainingFireTicks = Math.max(flameLevel * 20, 0);
            instance.maxFireTicks = instance.remainingFireTicks;
        } else {
            original.call(instance);
        }
    }


    @WrapOperation(method = "onHit", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/entity/Entity;hurt(Lnet/minecraft/core/entity/Entity;ILnet/minecraft/core/util/helper/DamageType;)Z"))
    private boolean setMultiHit(Entity instance, Entity attacker, int baseDamage, DamageType type, Operation<Boolean> original) {
        if (this.enchanting$readMultiHit() && instance instanceof Mob mob && instance.heartsFlashTime > mob.heartsHalvesLife / 2.0F) {
            int lastDamage = ((MobAccessor) instance).getLastDamage();
            if (baseDamage <= lastDamage) {
                return original.call(instance, attacker, baseDamage + lastDamage, type);
            }
        }
        return original.call(instance, attacker, baseDamage, type);
    }

	@Inject(method = "onHit", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/entity/projectile/ProjectileArrow;inGroundAction()V", shift = At.Shift.AFTER))
	private void setTileOnFire(CallbackInfo ci, @Local HitResult.Tile hitResult){
		ProjectileArrow arrow = (ProjectileArrow) (Object) this;
		if (this.isAFlame <= 0 || arrow.world == null) {
			return;
		}
		Side side = hitResult.side;
		int blockX = hitResult.tilePos.x() + side.offsetX();
		int blockY = hitResult.tilePos.y() + side.offsetY();
		int blockZ = hitResult.tilePos.z() + side.offsetZ();
		TilePos tilePos = new TilePos(blockX, blockY, blockZ);
		Block<?> block = arrow.world.getBlockType(tilePos);
		Material material = block.getMaterial();
		if(block.id() == 0 ||(material.isReplaceable() && !(material instanceof MaterialLiquid))){
			arrow.world.setBlockTypeNotify(tilePos, Blocks.FIRE);
		}
	}
}
