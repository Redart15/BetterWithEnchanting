package googy.betterwithenchanting.block;

import net.minecraft.client.render.model.Cube;
import net.minecraft.client.render.model.ModelBase;
import net.minecraft.core.util.helper.MathHelper;

public class BookModel extends ModelBase {
	protected Cube coverRight = new Cube(0, 0);
	protected Cube coverLeft = new Cube(16, 0);
	protected Cube pagesRight = new Cube(0, 10);
	protected Cube pagesLeft = new Cube(12, 10);
	protected Cube flippingPageRight = new Cube(24, 10);
	protected Cube flippingPageLeft = new Cube(24, 10);
	protected Cube bookSpine = new Cube(12, 0);

	public BookModel() {
		coverRight.addBox(-6.0F, -5.0F, 0.0F, 6, 10, 0);
		coverLeft.addBox(0.0F, -5.0F, 0.0F, 6, 10, 0);
		pagesRight.addBox(0.0F, -4.0F, -0.99F, 5, 8, 1);
		pagesLeft.addBox(0.0F, -4.0F, -0.01F, 5, 8, 1);
		flippingPageRight.addBox(0.0F, -4.0F, 0.0F, 5, 8, 0);
		flippingPageLeft.addBox(0.0F, -4.0F, 0.0F, 5, 8, 0);
		bookSpine.addBox(-1.0F, -5.0F, 0.0F, 2, 10, 0);

		coverRight.setRotationPoint(0.0F, 0.0F, -1.0F);
		coverLeft.setRotationPoint(0.0F, 0.0F, 1.0F);
		bookSpine.yRot = ((float)Math.PI / 2.0F);
	}

	@Override
	public void render(float limbSwing, float limbYaw, float limbPitch, float headYaw, float headPitch, float scale) {
		this.setupAnimation(limbSwing, limbYaw, limbPitch, headYaw, headPitch, scale);
		coverRight.render(scale);
		coverLeft.render(scale);
		bookSpine.render(scale);
		pagesRight.render(scale);
		pagesLeft.render(scale);
		flippingPageRight.render(scale);
		flippingPageLeft.render(scale);
	}

	@Override
	public void setupAnimation(float limbSwing, float limbYaw, float limbPitch, float headYaw, float headPitch, float scale) {
		float f = (MathHelper.sin(limbSwing * 0.02F) * 0.1F + 1.25F) * headYaw;
		coverRight.yRot = ((float)Math.PI + f);
		coverLeft.yRot = -f;
		pagesRight.yRot = f;
		pagesLeft.yRot = -f;
		flippingPageRight.yRot = f - f * 2.0F * limbYaw;
		flippingPageLeft.yRot = f - f * 2.0F * limbPitch;
		pagesRight.x = MathHelper.sin(f);
		pagesLeft.x = MathHelper.sin(f);
		flippingPageRight.x = MathHelper.sin(f);
		flippingPageLeft.x = MathHelper.sin(f);
	}
}
