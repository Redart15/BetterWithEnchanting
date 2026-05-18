package googy.betterwithenchanting.mixins.mixin.enchantment;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.nbt.tags.CompoundTag;
import googy.betterwithenchanting.mixins.mixin.accessor.MobAccessor;
import googy.betterwithenchanting.mixins.mixin.accessor.EntityAccessor;
import googy.betterwithenchanting.mixins.interfaces.IEnchantment;
import net.minecraft.core.Global;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.Mob;
import net.minecraft.core.entity.projectile.ProjectileArrow;
import net.minecraft.core.util.helper.DamageType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ProjectileArrow.class, remap = false)
public class ProjectileArrowMixin implements IEnchantment {

    @Unique
    int isAFlame = 0;
    @Unique
    boolean multiHit = false;

    @Override
    public int enchanting$readFlame() {
        return this.isAFlame;
    }

    @Override
    public void enchanting$writeFlame(int level) {
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
        if (tag.containsKey("isAFlame")) {
            this.isAFlame = tag.getInteger("isAFlame");
        }
        if (tag.containsKey("isMultiHit")) {
            this.multiHit = tag.getBoolean("isMultiHit");
        }
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void addIsAFlame(CompoundTag tag, CallbackInfo ci) {
        tag.putInt("isAFlame", this.isAFlame);
        tag.putBoolean("isMultiHit", this.multiHit);
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
        if (this.enchanting$readMultiHit() && instance instanceof Mob && instance.heartsFlashTime > ((Mob) instance).heartsHalvesLife / 2.0F) {
            int lastDamage = ((MobAccessor) instance).getLastDamage();
            if (baseDamage <= lastDamage) {
                return original.call(instance, attacker, baseDamage + lastDamage, type);
            }
        }
        return original.call(instance, attacker, baseDamage, type);
    }
}
