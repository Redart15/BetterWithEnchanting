package googy.betterwithenchanting;

import googy.betterwithenchanting.command.CommandEnchantment;
import googy.betterwithenchanting.command.CommandScore;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.minecraft.core.net.command.CommandManager;

public class BetterWithEnchantingServer implements DedicatedServerModInitializer {
	@Override
	public void onInitializeServer() {
		CommandManager.registerCommand(new CommandEnchantment());
		CommandManager.registerCommand(new CommandScore());
	}
}

