package googy.betterwithenchanting.mixins;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import googy.betterwithenchanting.api.EnchantmentContainer;
import googy.betterwithenchanting.api.Enchantments;
import googy.betterwithenchanting.mixins.interfaces.EnchantmentArrow;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.entity.projectile.Projectile;
import net.minecraft.core.entity.projectile.ProjectileArrowPurple;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.world.World;

public class EnchantedArrow {
	Player player;
	int flameLevel = 0;
	int multiShot = 0;
	boolean multiHit = false;
	int buckShot = 0;
	int power = 0;

	public EnchantedArrow(Player player, ItemStack itemStack) {
		this.player = player;
		this.flameLevel = EnchantmentContainer.getLevel(itemStack, Enchantments.FLAME);
		this.multiShot = EnchantmentContainer.getLevel(itemStack, Enchantments.MULTI_SHOT);
		this.buckShot = EnchantmentContainer.getLevel(itemStack, Enchantments.BUCK_SHOT);
		this.multiHit = this.multiShot > 0 || this.buckShot > 0;
		this.power = EnchantmentContainer.getLevel(itemStack, Enchantments.POWER);
	}

	public boolean doMultiShot(World instance, Operation<Boolean> original) {
		boolean returnValues = true;
		for (int i = 1; i <= this.multiShot; i++) {
			Projectile projectile = new ProjectileArrowPurple(instance, this.player, false);
			this.setOnFire(projectile);
			this.setMultiHit(projectile);
			this.setIncreasedSpeed(projectile);
			double dx = projectile.xd;
			double dy = projectile.yd;
			double dz = projectile.zd;
			while (dx * dx + dy * dy + dz * dz < 1.0E-4) {
				dx = (Math.random() - Math.random()) * 0.01;
				dy = (Math.random() - Math.random()) * 0.01;
				dz = (Math.random() - Math.random()) * 0.01;
			}
			double length = Math.sqrt(dx * dx + dy * dy + dz * dz);
			dx /= length;
			dy /= length;
			dz /= length;
			double spacing = 1.0f;
			projectile.x += dx * spacing * i;
			projectile.y += dy * spacing * i;
			projectile.z += dz * spacing * i;
			returnValues &= original.call(instance, projectile);
		}
		return returnValues;
	}

	public boolean doBuckShot(World instance, Operation<Boolean> original) {
		boolean returnValues = true;
		double randomness = 5.0f;
		double speed = 2.5;
		for (int i = 0; i < (int) Math.ceil(this.buckShot * 1.5f); i++) {
			Projectile projectile = new ProjectileArrowPurple(instance, this.player, false);
			this.setOnFire(projectile);
			this.setMultiHit(projectile);
			this.setIncreasedSpeed(projectile);
			double dx = -Math.sin(Math.toRadians(player.yRot)) * Math.cos(Math.toRadians(player.xRot));
			double dy = -Math.sin(Math.toRadians(player.xRot));
			double dz = Math.cos(Math.toRadians(player.yRot)) * Math.cos(Math.toRadians(player.xRot));
			projectile.setHeading(dx, dy, dz, (float) speed, (float) randomness);
			returnValues &= original.call(instance, projectile);
		}

		return returnValues;
	}

	public void setOnFire(Projectile projectile) {
		if (flameLevel > 0 && projectile instanceof EnchantmentArrow iEnchantment) {
			iEnchantment.enchanting$writeFlame((byte)this.flameLevel);
			projectile.fireHurt();
		}
	}

	public void setMultiHit(Projectile projectile) {
		if (projectile instanceof EnchantmentArrow iEnchantment) {
			iEnchantment.enchanting$writeMultiHit(multiHit);
		}
	}

	public void setIncreasedSpeed(Projectile projectile) {
		if (this.power > 0) {
			projectile.xd = projectile.xd * (1.0f + this.power * 0.2);
			projectile.yd = projectile.yd * (1.0f + this.power * 0.2);
			projectile.zd = projectile.zd * (1.0f + this.power * 0.2);
		}
	}
}
