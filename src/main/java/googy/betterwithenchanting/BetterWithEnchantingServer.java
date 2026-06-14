package googy.betterwithenchanting;

import googy.betterwithenchanting.api.command.CommandEnchantments;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.minecraft.core.net.command.CommandManager;

public class BetterWithEnchantingServer implements DedicatedServerModInitializer {
	@Override
	public void onInitializeServer() {
		CommandManager.registerCommand(new CommandEnchantments());
	}
}

