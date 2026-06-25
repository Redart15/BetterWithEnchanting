package googy.betterwithenchanting.mixins.mixin.enchantment;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import googy.betterwithenchanting.api.EnchantmentContainer;
import googy.betterwithenchanting.mixins.MixinsHelperLogic;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.data.gamerule.TreecapitatorHelper;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.enums.EnumDropCause;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Mixin(value = {TreecapitatorHelper.class}, remap = false)
public class TreecapitatorMixinMoltenAxeFix {
	@Shadow
	@Final
	public Player player;

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
		ItemStack[] originalDrops = original.call(instance, world, dropCause, x, y, z, meta, tileEntity);
		List<ItemStack> collect = new ArrayList<>(Arrays.asList(MixinsHelperLogic.applyMoltenAndScevange(dropCause, ((TreecapitatorHelper) (Object) this).player, originalDrops)));
		collect.add(MixinsHelperLogic.applyDiscovery(player));
		MixinsHelperLogic.applyInsight(player, 3);
		return collect.toArray(new ItemStack[0]);
	}
}
