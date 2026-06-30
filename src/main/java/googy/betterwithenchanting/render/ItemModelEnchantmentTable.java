package googy.betterwithenchanting.render;

import googy.betterwithenchanting.BetterWithEnchanting;
import net.minecraft.client.render.TextureManager;
import net.minecraft.client.render.TileEntityRenderDispatcher;
import net.minecraft.client.render.item.model.ItemModelBlock;
import net.minecraft.client.render.renderer.GLRenderer;
import net.minecraft.client.render.tessellator.TessellatorGeneral;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.block.ItemBlock;
import net.minecraft.core.util.helper.MathHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.useless.dragonfly.data.entity.mojang.EntityGeometryMojangData;
import org.useless.dragonfly.models.entity.StaticEntityModel;

import java.util.Random;

public class ItemModelEnchantmentTable extends ItemModelBlock {
	private static final String TEXTURE_PATH = "/assets/" + BetterWithEnchanting.MOD_ID + "/textures/entity/book.png";
	private float pageFlip = 0.0f;
	private float prevPageFlip = 0.0f;
	private float bookSpread = 0.0f;
	private float prevBookSpread = 0.0f;
	private float flipT = 0.0f;
	private float flipA = 0.0f;
	private int tickCount = 0;
	private final Random random = new Random();


	public ItemModelEnchantmentTable(ItemBlock<?> itemBlock) {
		super(itemBlock);
	}

	@Override
	public void render(@NotNull TessellatorGeneral tessellator, @Nullable Entity holder, @NotNull ItemStack itemStack, @NotNull String displayPosId, boolean items3d, int clusterSize, byte lightIndex, float partialTick, boolean leftHanded) {
		super.render(tessellator, holder, itemStack, displayPosId, items3d, clusterSize, lightIndex, partialTick, leftHanded);
		if("gui".equals(displayPosId)){
			GLRenderer.modelM4f().rotateY(MathHelper.toRadians(180.0F));
		}
		TextureManager manager = TileEntityRenderDispatcher.instance.textureManager;
		StaticEntityModel model = EntityGeometryMojangData.Cache.getModel("geometry.book", 0.01F);
		GLRenderer.pushFrame();
		manager.bindTexture(manager.loadTexture(TEXTURE_PATH));
		GLRenderer.modelM4f().translate(0.0F, 0.375F, 0.0F);
		GLRenderer.modelM4f().scale(0.0625F, -0.0625F, 0.0625F);
		GLRenderer.modelM4f().rotate(MathHelper.toRadians(80.0F), 0.0F, 0.0F, 1.0F);
		float randianAngle = MathHelper.toRadians(75);
		model.getTransform("left").rotY = randianAngle;
		model.getTransform("right").rotY = -randianAngle;
		model.getTransform("pagesLeft").posX = MathHelper.sin(randianAngle * 0.65f);
		model.getTransform("pagesRight").posX = MathHelper.sin(randianAngle * 0.65f);
		model.getTransform("flippingPageRight").posX = MathHelper.sin(-randianAngle);
		model.getTransform("flippingPageLeft").posX = MathHelper.sin(-randianAngle);
		model.getTransform("pagesLeft").posZ = MathHelper.sin(-randianAngle * 0.2f);
		model.getTransform("pagesRight").posZ = MathHelper.sin(randianAngle * 0.2f);
		if("gui".equals(displayPosId)){
			model.getTransform("flippingPageRight").rotY = MathHelper.toRadians(45);
			model.getTransform("flippingPageLeft").rotY = MathHelper.toRadians(45);
		}else{
			this.animatePages(model, pageFlip);
		}
		model.render();
		GLRenderer.popFrame();
	}

	private void animatePages(StaticEntityModel model, float partialTick) {
//		animate model
		this.tickCount++;
		int t = this.tickCount / 20;
		// animating the pages flipping
		if(this.tickCount % 20 == 0) {
			this.adjustRotation();
		}
		float f5 = this.prevBookSpread + (this.bookSpread - this.prevBookSpread) * partialTick;
		float f1 = (MathHelper.sin(t * 0.02F) * 0.1F + 1.25F) * f5;
		float f3 = this.prevPageFlip + (this.pageFlip - this.prevPageFlip) * partialTick + 0.25F;
		float f4 = this.prevPageFlip + (this.pageFlip - this.prevPageFlip) * partialTick + 0.75F;
		f3 = (f3 - MathHelper.floor_float(f3)) * 1.6F - 0.3F;
		f4 = (f4 - MathHelper.floor_float(f4)) * 1.6F - 0.3F;
		if (f3 < 0.0F) f3 = 0.0F;
		if (f4 < 0.0F) f4 = 0.0F;
		if (f3 > 1.0F) f3 = 1.0F;
		if (f4 > 1.0F) f4 = 1.0F;
		model.getTransform("flippingPageRight").rotY = f1 - f1 * 2.0F * f4;
		model.getTransform("flippingPageLeft").rotY = f1 - f1 * 2.0F * f3;
		this.prevPageFlip = this.pageFlip;
		this.prevBookSpread = this.bookSpread;

		GLRenderer.modelM4f().translate(0.1F + MathHelper.sin((this.tickCount + partialTick) * 0.003F) * 0.23f, 0.0F, 0.0F);
	}

	private void adjustRotation() {
		this.bookSpread += 0.1F;
		if((this.bookSpread < 0.5F || this.random.nextInt(40) == 0)){
			float f = this.flipT;
			while (true) {
				this.flipT += (this.random.nextInt(4) - this.random.nextInt(4));
				if (f != this.flipT) {
					break;
				}
			}
		}
		this.bookSpread = MathHelper.clamp(this.bookSpread, 0.0F, 1.0F);
		float f2 = (this.flipT - this.pageFlip) * 0.4F;
		f2 = MathHelper.clamp(f2, -0.2F, 0.2F);
		this.flipA += (f2 - this.flipA) * 0.9F;
		this.pageFlip += this.flipA;
	}
}
