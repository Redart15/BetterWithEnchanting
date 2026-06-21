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

public class EnchantmentTableRenderer extends TileEntityRenderer<TileEntityEnchantmentTable> {
//	private static final BookModel BOOK_MODEL = new BookModel();
	private static final String TEXTURE_PATH = "/assets/" + BetterWithEnchanting.MOD_ID + "/textures/model/book.png";
	private final EntityItem entityItem = new EntityItem(null);
	private final EntityRendererItem renderer = new EntityRendererItem();

	@Override
	public void doRender(TessellatorGeneral tessellator, TileEntityEnchantmentTable tileEntity, double x, double y, double z, float partialTicks) {
		if (tileEntity.worldObj == null) {
			return;
		}
		GLRenderer.pushFrame();
		GLRenderer.modelM4f().translate((float) x + 0.5F, (float) y + 0.75F, (float) z + 0.5F);
		float t = tileEntity.ticks() + partialTicks;
		float f;
		GLRenderer.modelM4f().translate(0.0F, 0.1F + MathHelper.sin(t * 0.1F) * 0.01F, 0.0F);
		for (f = tileEntity.bookRot() - tileEntity.prevBookRot(); f >= (float) Math.PI; f -= ((float) Math.PI * 2.0F));
		while (f < -(float) Math.PI) f += ((float) Math.PI * 2.0F);

		float f2 = tileEntity.prevBookRot() + f * partialTicks;
		GLRenderer.modelM4f().rotate(MathHelper.toRadians(-f2 * (180.0F / (float) Math.PI)), 0.0F, 1.0F, 0.0F);
		GLRenderer.modelM4f().rotate(MathHelper.toRadians(80.0F), 0.0F, 0.0F, 1.0F);
		TextureManager textureManager = TileEntityRenderDispatcher.instance.textureManager;
		textureManager.bindTexture(textureManager.loadTexture(TEXTURE_PATH));
		float f3 = tileEntity.prevPageFlip() + (tileEntity.pageFlip() - tileEntity.prevPageFlip()) * partialTicks + 0.25F;
		float f4 = tileEntity.prevPageFlip() + (tileEntity.pageFlip() - tileEntity.prevPageFlip()) * partialTicks + 0.75F;
		f3 = (f3 - MathHelper.floor_float(f3)) * 1.6F - 0.3F;
		f4 = (f4 - MathHelper.floor_float(f4)) * 1.6F - 0.3F;

		if (f3 < 0.0F) f3 = 0.0F;
		if (f4 < 0.0F) f4 = 0.0F;
		if (f3 > 1.0F) f3 = 1.0F;
		if (f4 > 1.0F) f4 = 1.0F;

		float f5 = tileEntity.prevBookSpread() + (tileEntity.bookSpread() - tileEntity.prevBookSpread()) * partialTicks;
		GLRenderer.enableState(State.CULL_FACE);
//		BOOK_MODEL.render(t, f3, f4, f5, 0.0F, 0.0625F);
		GLRenderer.popFrame();

		ItemStack stack = tileEntity.getItem(0);
		if (stack == null) return;
		GLRenderer.pushFrame();
		GLRenderer.modelM4f().translate((float) (x + 0.5D), (float) (y + 1.5D), (float) (z + 0.5D));
		if (GameSettings.ITEMS_3D.value
			|| 	(
					stack.getItem() instanceof ItemBlock<?> itemBlock
					&& BlockModelDispatcher.getInstance().getDispatch(itemBlock.getBlock()).shouldItemRender3d()
				)
		) {
			GLRenderer.modelM4f().rotate(MathHelper.toRadians(-(tileEntity.itemRot() + 0.01F * partialTicks) * (180.0F / (float) Math.PI)), 0.0F, 1.0F, 0.0F);
		}
		this.entityItem.item = new ItemStack(stack.itemID, 1, stack.getMetadata(), stack.getData());
		this.entityItem.entityBrightness = tileEntity.worldObj.getLightBrightness(tileEntity.tilePos);
		if(this.entityItem.world == null){
			GLRenderer.popFrame();
			return;
		}
		this.renderer.render(tessellator, this.entityItem, 0D, -0.3D, 0D, 0F, 0F);

		GLRenderer.popFrame();
	}
}
