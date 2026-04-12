package googy.betterwithenchanting.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import googy.betterwithenchanting.particle.CustomTextureAtlas;
import net.minecraft.client.entity.particle.Particle;
import net.minecraft.client.render.ParticleEngine;
import net.minecraft.client.render.camera.ICamera;
import net.minecraft.client.render.tessellator.Tessellator;
import net.minecraft.core.util.helper.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = ParticleEngine.class, remap = false)
public class ParticleEngineMixin {

	@WrapOperation(method = "renderLitParticles", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/entity/particle/Particle;render(Lnet/minecraft/client/render/tessellator/Tessellator;FDDDFFFFF)V"))
	public void renderCall(
		Particle instance, Tessellator t,
		float partialTick, double xOff, double yOff, double zOff, float xa, float ya, float za, float xa2, float za2,
		Operation<Void> original, ICamera camera
	){
		if(instance instanceof CustomTextureAtlas){
			double yRot = camera.getYRot(partialTick);
			double xRot = camera.getXRot(partialTick);
			xa = MathHelper.cos((float)(yRot * Math.PI) / 180.0F);
			za = MathHelper.sin((float)(yRot * Math.PI) / 180.0F);
			xa2 = -za * MathHelper.sin((float)(xRot * Math.PI / (double)180.0F));
			za2 = xa * MathHelper.sin((float)(xRot * Math.PI / (double)180.0F));
			ya = MathHelper.cos((float)(xRot * Math.PI / (double)180.0F));
		}
		instance.render(t, partialTick, xOff, yOff, zOff, xa, ya, za, xa2, za2);
	}
}
