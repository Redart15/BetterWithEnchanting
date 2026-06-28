package googy.betterwithenchanting.render;

import googy.betterwithenchanting.BetterWithEnchanting;
import googy.betterwithenchanting.block.TileEntityEnchantmentTable;
import net.minecraft.client.option.GameSettings;
import net.minecraft.client.render.TextureManager;
import net.minecraft.client.render.TileEntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRendererItem;
import net.minecraft.client.render.block.model.BlockModelDispatcher;

import net.minecraft.client.render.renderer.GLRenderer;
import net.minecraft.client.render.renderer.State;
import net.minecraft.client.render.tessellator.TessellatorGeneral;
import net.minecraft.client.render.tileentity.TileEntityRenderer;
import net.minecraft.core.entity.EntityItem;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.block.ItemBlock;
import net.minecraft.core.util.helper.MathHelper;
import org.useless.dragonfly.data.entity.mojang.EntityGeometryMojangData;
import org.useless.dragonfly.models.entity.StaticEntityModel;

public class EnchantmentTableRenderer extends TileEntityRenderer<TileEntityEnchantmentTable> {
	private static final String TEXTURE_PATH = "/assets/" + BetterWithEnchanting.MOD_ID + "/textures/entity/book.png";
	private final EntityItem entityItem = new EntityItem(null);
	private final EntityRendererItem renderer = new EntityRendererItem();

	@Override
	public void doRender(TessellatorGeneral tessellator, TileEntityEnchantmentTable tileEntity, double x, double y, double z, float partialTicks) {
		if (tileEntity.worldObj == null) {
			return;
		}
		this.renderBook(tileEntity, (float) x, (float) y, (float) z, partialTicks);
		this.renderItem(tessellator, tileEntity, x, y, z, partialTicks);
	}

	private void renderBook(TileEntityEnchantmentTable tileEntity, float x, float y, float z, float partialTicks) {
		float t = tileEntity.ticks() + partialTicks;
		float f = tileEntity.bookRot() - tileEntity.prevBookRot();
		while (f >= (float) Math.PI) {f -= ((float) Math.PI * 2.0F);}
		while (f < -(float) Math.PI) {f += ((float) Math.PI * 2.0F);}
		float f2 = tileEntity.prevBookRot() + f * partialTicks;
		float f3 = tileEntity.prevPageFlip() + (tileEntity.pageFlip() - tileEntity.prevPageFlip()) * partialTicks + 0.25F;
		float f4 = tileEntity.prevPageFlip() + (tileEntity.pageFlip() - tileEntity.prevPageFlip()) * partialTicks + 0.75F;
		f3 = (f3 - MathHelper.floor_float(f3)) * 1.6F - 0.3F;
		f4 = (f4 - MathHelper.floor_float(f4)) * 1.6F - 0.3F;
		if (f3 < 0.0F) f3 = 0.0F;
		if (f4 < 0.0F) f4 = 0.0F;
		if (f3 > 1.0F) f3 = 1.0F;
		if (f4 > 1.0F) f4 = 1.0F;
		float f5 = tileEntity.prevBookSpread() + (tileEntity.bookSpread() - tileEntity.prevBookSpread()) * partialTicks;
		TextureManager manager = TileEntityRenderDispatcher.instance.textureManager;
		GLRenderer.pushFrame();
		StaticEntityModel model = EntityGeometryMojangData.Cache.getModel("geometry.book", 0.01F);
		GLRenderer.modelM4f().translate(x + 0.5F, y + 0.75F, z + 0.5F);
		GLRenderer.modelM4f().translate(0.0F, 0.1F + MathHelper.sin(t * 0.1F) * 0.01F, 0.0F);
		GLRenderer.modelM4f().scale(0.0625F, -0.0625F, 0.0625F);
		GLRenderer.modelM4f().rotate(-f2 + MathHelper.toRadians(180), 0.0F, 1.0F, 0.0F);
		GLRenderer.modelM4f().rotate(MathHelper.toRadians(80.0F), 0.0F, 0.0F, 1.0F);
		manager.bindTexture(manager.loadTexture(TEXTURE_PATH));
		GLRenderer.enableState(State.CULL_FACE);
		float f1 = (MathHelper.sin(t * 0.02F) * 0.1F + 1.25F) * f5;
		model.getTransform("left").rotY = f1;
		model.getTransform("right").rotY = -f1;
		model.getTransform("flippingPageRight").rotY = f1 - f1 * 2.0F * f3;
		model.getTransform("flippingPageLeft").rotY = f1 - f1 * 2.0F * f4;
		model.getTransform("pagesLeft").posX = MathHelper.sin(f1 * 0.65f);
		model.getTransform("pagesRight").posX = MathHelper.sin(f1 * 0.65f);
		model.getTransform("flippingPageRight").posX = MathHelper.sin(-f1);
		model.getTransform("flippingPageLeft").posX = MathHelper.sin(-f1);
		model.getTransform("pagesLeft").posZ = MathHelper.sin(-f1 * 0.2f);
		model.getTransform("pagesRight").posZ = MathHelper.sin(f1 * 0.2f);
		model.render();
		GLRenderer.popFrame();
	}

	private void renderItem(TessellatorGeneral tessellator, TileEntityEnchantmentTable tileEntity, double x, double y, double z, float partialTicks) {
		ItemStack stack = tileEntity.getItem(0);
		if (stack == null) return;
		GLRenderer.pushFrame();
		GLRenderer.modelM4f().translate((float) (x + 0.5D), (float) (y + 1.5D), (float) (z + 0.5D));
		if (GameSettings.ITEMS_3D.value
			|| (stack.getItem() instanceof ItemBlock<?> itemBlock
			&& BlockModelDispatcher.getInstance().getDispatch(itemBlock.getBlock()).shouldItemRender3d())
		) {
			GLRenderer.modelM4f().rotate(-(tileEntity.itemRot() + 0.01F * partialTicks), 0.0F, 1.0F, 0.0F);
		}
		this.entityItem.item = new ItemStack(stack.getItem(), 1, stack.getMetadata(), stack.getData());
		this.entityItem.entityBrightness = tileEntity.worldObj.getLightBrightness(tileEntity.tilePos);
		this.entityItem.world = tileEntity.worldObj;
		this.entityItem.x = tileEntity.tilePos.x();
		this.entityItem.y = tileEntity.tilePos.y();
		this.entityItem.z = tileEntity.tilePos.z();
		this.renderer.render(tessellator, this.entityItem, 0D, -0.3D, 0D, 0F, 0F);
		GLRenderer.popFrame();
	}
}
