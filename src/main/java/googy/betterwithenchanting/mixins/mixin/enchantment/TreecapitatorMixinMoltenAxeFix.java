package googy.betterwithenchanting.mixins.mixin.enchantment;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import googy.betterwithenchanting.mixins.EnchantmentMixins;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.data.gamerule.TreecapitatorHelper;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.enums.EnumDropCause;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = {TreecapitatorHelper.class}, remap = false)
public class TreecapitatorMixinMoltenAxeFix {
	@WrapOperation(
		method = {"breakBlock"},
		at = {@At(
			value = "INVOKE",
			target = "Lnet/minecraft/core/block/Block;getBreakResult(Lnet/minecraft/core/world/World;Lnet/minecraft/core/enums/EnumDropCause;IIIILnet/minecraft/core/block/entity/TileEntity;)[Lnet/minecraft/core/item/ItemStack;"
		)}
	)
	private ItemStack[] modifyBlockResults(
		Block<?> instance,
		World world, EnumDropCause dropCause,
		int x, int y, int z, int meta, TileEntity tileEntity, Operation<ItemStack[]> original
	) {
		return EnchantmentMixins.applyMolten(dropCause, ((TreecapitatorHelper) (Object) this).player, original.call(instance, world, dropCause, x, y, z, meta, tileEntity));
	}
}
