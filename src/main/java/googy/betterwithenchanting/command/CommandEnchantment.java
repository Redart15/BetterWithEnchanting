package googy.betterwithenchanting.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.arguments.ArgumentTypeInteger;
import com.mojang.brigadier.builder.ArgumentBuilderLiteral;
import com.mojang.brigadier.builder.ArgumentBuilderRequired;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import googy.betterwithenchanting.BetterWithEnchanting;
import googy.betterwithenchanting.api.Enchantment;
import googy.betterwithenchanting.api.EnchantmentContainer;
import googy.betterwithenchanting.api.EnchantmentStack;
import googy.betterwithenchanting.api.Enchantments;
import it.unimi.dsi.fastutil.ints.IntIntPair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.render.font.FontDefault;
import net.minecraft.client.render.font.FontRendererDefault;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.lang.I18n;
import net.minecraft.core.net.command.CommandManager;
import net.minecraft.core.net.command.CommandSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static googy.betterwithenchanting.BetterWithEnchanting.TRANSLATE;
import static googy.betterwithenchanting.command.ReturnValues.*;

public class CommandEnchantment implements CommandManager.CommandRegistry {

	public static SimpleCommandExceptionType NOT_APPLICABLE;
	private static final Random RANDOM = new Random();

	@Override
	public void register(CommandDispatcher<CommandSource> commandDispatcher) {
		NOT_APPLICABLE = new SimpleCommandExceptionType(new LiteralMessage(I18n.getInstance().translateKey("enchantment.command.not.applicable")));
		commandDispatcher.register(
			ArgumentBuilderLiteral.<CommandSource>literal("enchant")
				.then(buildListCommand())
				.then(buildInfoCommand())
				.then(buildRandomEnchantmentCommand())
				.then(buildAddCommand())
				.then(buildIncreaseCommand())
				.then(buildRemoveCommand())
		);
	}

	private static ArgumentBuilderLiteral<CommandSource> buildListCommand() {
		return ArgumentBuilderLiteral.<CommandSource>literal("list")
			.then(ArgumentBuilderLiteral.<CommandSource>literal("all")
				.executes(CommandEnchantment::listAllEnchantments)
			)
			.then(ArgumentBuilderLiteral.<CommandSource>literal("applicable")
				.executes(CommandEnchantment::listApplicableEnchantments)
			);
	}

	private static int listAllEnchantments(CommandContext<CommandSource> ctx) {
		ctx.getSource().sendTranslatableMessage("enchantment.command.list.all");
		for (Enchantment enchantment : Enchantments.getInstance()) {
			ctx.getSource().sendMessage("- " + enchantment.prettyToString());
		}
		return code(OK);
	}

	private static int listApplicableEnchantments(CommandContext<CommandSource> ctx) {
		final Player player = ctx.getSource().getSender();
		if (player == null) {
			return code(FAIL);
		}
		final ItemStack stack = ctx.getSource().getSender().getHeldItem();
		if (stack == null) {
			ctx.getSource().sendTranslatableMessage("enchantment.command.none");
			return code(FAIL);
		}
		List<Enchantment> applicable = new ArrayList<>();
		for (Enchantment enchantment : Enchantments.getInstance()) {
			if (enchantment.canEnchant(stack)) {
				applicable.add(enchantment);
			}
		}
		if (applicable.isEmpty()) {
			ctx.getSource().sendTranslatableMessage("enchantment.command.cannot");
			return code(FAIL);
		}
		if (applicable.size() == 1) {
			ctx.getSource().sendTranslatableMessage("enchantment.command.list.applicable.single");
		} else {
			ctx.getSource().sendTranslatableMessage("enchantment.command.list.applicable.multi");
		}
		for (Enchantment enchantment : applicable) {
			ctx.getSource().sendMessage("- " + enchantment.prettyToString());
		}
		return applicable.size();
	}

	private static ArgumentBuilderLiteral<CommandSource> buildInfoCommand() {
		return ArgumentBuilderLiteral.<CommandSource>literal("info")
			.then(ArgumentBuilderRequired.<CommandSource, Enchantment>argument("name", ArgumentTypeEnchantment.enchantments())
				.then(ArgumentBuilderLiteral.<CommandSource>literal("verbose")
					.executes(CommandEnchantment::listFullInfo)
				)
			)
			.then(ArgumentBuilderLiteral.<CommandSource>literal("upgrading")
				.then(ArgumentBuilderLiteral.<CommandSource>literal("verbose")
					.executes(CommandEnchantment::explainUpgrading)
				)
			)
			.then(ArgumentBuilderRequired.<CommandSource, Enchantment>argument("name", ArgumentTypeEnchantment.enchantments())
				.executes(CommandEnchantment::listInfo)
			);
	}

	private static int explainUpgrading(CommandContext<CommandSource> ctx) throws CommandSyntaxException{
		final Player player = ctx.getSource().getSender();
		if (player == null) {
			throw NOT_APPLICABLE.create();
		}
		StringBuilder message = new StringBuilder("§r§a")
			.append(TRANSLATE.translateKey("enchantment.command.upgrading.name"))
			.append(":§r\n")
			.append(TRANSLATE.translateKey("enchantment.command.upgrading.desc"))
			.append("\n\n");
		message.append("Example:\n")
			.append("For example the process rolls two enchantments ")
			.append("§r§9")
			.append(new EnchantmentStack(Enchantments.BOTTLED_SCORE, 1).prettyToString())
			.append("§r and §r§9")
			.append(new EnchantmentStack(Enchantments.BOTTLED_SCORE, 2).prettyToString())
			.append("§r than those level combine to §r§9")
			.append(new EnchantmentStack(Enchantments.BOTTLED_SCORE, 3).prettyToString())
			.append(".§r");
		ctx.getSource().sendMessage(message.toString());
		return Command.SINGLE_SUCCESS;
	}

	private static int listFullInfo(CommandContext<CommandSource> ctx) throws CommandSyntaxException {
		final Player player = ctx.getSource().getSender();
		if (player == null) {
			throw NOT_APPLICABLE.create();
		}
		// TODO: Once Archiments are made check if the player can get this infomation.
		final Enchantment enchantment = ctx.getArgument("name", Enchantment.class);
		StringBuilder message = new StringBuilder()
			.append("§r§n§9") // formating name
			.append(TRANSLATE.translateKey(enchantment.translationKeyName()))
			.append("(").append(enchantment.minLevel()).append("-").append(enchantment.maxLevel()).append(")")
			.append("§r").append('\n')
			.append(TRANSLATE.translateKey(enchantment.translationKeyDesc()))
			.append("\n\n");

		String[] targets = enchantment.getTargetDescKeys();
		message.append("The following item can be enchanted:\n");
		for(int i = 0; i < targets.length; i++){
			message.append("- ").append(TRANSLATE.translateKey("enchantment.target." + targets[i])).append("\n");
		}
		message.append("\n");
		message.append(TRANSLATE.translateKey("enchantment.command.info.upgrading")).append("\n");
		for (int level = enchantment.minLevel(); level <= enchantment.maxLevel(); level++) {
			int minScore = EnchantmentContainer.calcCostFromEnchantability(enchantment.getMinEnchantability(level), false);
			int maxScore = EnchantmentContainer.calcCostFromEnchantability(enchantment.getMaxEnchantability(level), true);
			boolean colorRed = minScore > BetterWithEnchanting.MAX_ENCHANTMENT_COST;
			if (level > enchantment.minLevel()) {
				message.append("\n");
			}
			message.append("level ")
				.append(extracted(colorRed))
				.append(level)
				.append("§r:[min= ")
				.append(extracted(colorRed))
				.append(padding(Integer.toString(Math.max(0, minScore)), 5, '_'))
				.append("§r, max= ")
				.append(extracted(colorRed))
				.append(padding(Integer.toString(Math.max(0, maxScore)), 5, '_'))
				.append("§r]");
		}
		ctx.getSource().sendMessage(message.toString());
		return Command.SINGLE_SUCCESS;
	}

	private static String padding(String number, int lenth, char paddignChar){
		if(number.length() >= lenth){
			return number;
		}
		return String.valueOf(paddignChar).repeat(lenth - number.length()) + number;
	}

	private static String extracted(boolean colorRed) {
		if(colorRed){
			return  "§r§e";
		}
		return "";
	}

	private static int listInfo(CommandContext<CommandSource> ctx) throws CommandSyntaxException {
		final Player player = ctx.getSource().getSender();
		if (player == null) {
			throw NOT_APPLICABLE.create();
		}
		final Enchantment enchantment = ctx.getArgument("name", Enchantment.class);
		String message = String.format("%s: %s", enchantment.prettyToString(), TRANSLATE.translateKey(enchantment.translationKeyDesc()));
		ctx.getSource().sendMessage(message);
		return Command.SINGLE_SUCCESS;
	}

	private static ArgumentBuilderLiteral<CommandSource> buildRandomEnchantmentCommand() {
		return ArgumentBuilderLiteral.<CommandSource>literal("random")
			.requires(src -> src.hasAdmin() && src.getSender() != null)
			.then(ArgumentBuilderRequired.<CommandSource, Integer>argument("cost", ArgumentTypeInteger.integer())
				.executes(CommandEnchantment::randomEnchant)
			);
	}

	private static int randomEnchant(CommandContext<CommandSource> ctx) throws CommandSyntaxException {
		Player player = ctx.getSource().getSender();
		if (player == null) {
			throw NOT_APPLICABLE.create();
		}
		final int cost = ctx.getArgument("cost", Integer.class);
		final ItemStack itemStack = player.getHeldItem();
		if (itemStack == null) {
			throw NOT_APPLICABLE.create();
		}
		if (EnchantmentContainer.hasEnchantments(itemStack) || !EnchantmentContainer.hasApplicable(itemStack)) {
			ctx.getSource().sendTranslatableMessage("enchantment.command.cannot");
			return code(FAIL);
		}
		List<EnchantmentStack> enchantmentStackList = EnchantmentContainer.generateEnchantmentsList(RANDOM, itemStack, cost);
		if (enchantmentStackList.isEmpty()) {
			ctx.getSource().sendTranslatableMessage("enchantment.command.none");
			return code(FAIL);
		}
		EnchantmentContainer.addEnchantments(itemStack, enchantmentStackList);
		ctx.getSource().sendTranslatableMessage("enchantment.command.enchant", itemStack.getDisplayName(), EnchantmentContainer.prettyPrint(itemStack));
		return enchantmentStackList.size();
	}

	private static ArgumentBuilderLiteral<CommandSource> buildAddCommand() {
		return ArgumentBuilderLiteral.<CommandSource>literal("add")
			.requires(src -> src.hasAdmin() && src.getSender() != null)
			.then(ArgumentBuilderRequired.<CommandSource, Enchantment>argument("name", ArgumentTypeEnchantment.enchantments())
				.then(ArgumentBuilderRequired.<CommandSource, Integer>argument("level", ArgumentTypeInteger.integer())
					.executes(CommandEnchantment::addEnchantment)
				)
			);
	}

	private static int addEnchantment(CommandContext<CommandSource> ctx) throws CommandSyntaxException {
		return CommandEnchantment.addEnchantment(ctx, 0);
	}

	private static int addEnchantment(CommandContext<CommandSource> ctx, int counter) throws CommandSyntaxException {
		Player player = ctx.getSource().getSender();
		if (player == null) {
			throw NOT_APPLICABLE.create();
		}
		final Enchantment enchantment = ctx.getArgument("name", Enchantment.class);
		final int level = ctx.getArgument("level", Integer.class);
		final ItemStack itemStack = player.getHeldItem();
		if (itemStack == null || counter > 1) {
			throw NOT_APPLICABLE.create();
		}
		if (EnchantmentContainer.contains(itemStack, enchantment)) {
			return increaseEnchantmentLevel(ctx, counter + 1);
		}
		if (!enchantment.canEnchant(itemStack)) {
			ctx.getSource().sendTranslatableMessage("enchantment.command.cannot");
			return code(FAIL);
		}
		EnchantmentContainer.rawAddEnchantment(itemStack, new EnchantmentStack(enchantment, level));
		ctx.getSource().sendTranslatableMessage("enchantment.command.add", TRANSLATE.translateKeyAndFormat(enchantment.translationKeyName()));
		return Command.SINGLE_SUCCESS;
	}

	private static ArgumentBuilderLiteral<CommandSource> buildIncreaseCommand() {
		return ArgumentBuilderLiteral.<CommandSource>literal("increase")
			.requires(src -> src.hasAdmin() && src.getSender() != null)
			.then(ArgumentBuilderRequired.<CommandSource, Enchantment>argument("name", ArgumentTypeEnchantment.enchantments())
				.then(ArgumentBuilderRequired.<CommandSource, Integer>argument("level", ArgumentTypeInteger.integer())
					.executes(CommandEnchantment::increaseEnchantmentLevel)
				)
			);
	}

	private static int increaseEnchantmentLevel(CommandContext<CommandSource> ctx) throws CommandSyntaxException {
		return CommandEnchantment.increaseEnchantmentLevel(ctx, 0);
	}

	private static int increaseEnchantmentLevel(CommandContext<CommandSource> ctx, int counter) throws CommandSyntaxException {
		Player player = ctx.getSource().getSender();
		if (player == null) {
			throw NOT_APPLICABLE.create();
		}
		final Enchantment enchantment = ctx.getArgument("name", Enchantment.class);
		final int level = ctx.getArgument("level", Integer.class);
		final ItemStack itemStack = player.getHeldItem();
		if (itemStack == null || counter > 1) {
			throw NOT_APPLICABLE.create();
		}
		if (!EnchantmentContainer.contains(itemStack, enchantment)) {
			return addEnchantment(ctx, counter + 1);
		}
		int currentLevel = EnchantmentContainer.getLevel(itemStack, enchantment);
		int nextLevel = currentLevel + level;
		if (nextLevel > enchantment.maxLevel()) {
			ctx.getSource().sendTranslatableMessage("enchantment.command.max", enchantment.maxLevel());
			nextLevel = enchantment.maxLevel();
		}
		if (nextLevel < enchantment.minLevel()) {
			ctx.getSource().sendTranslatableMessage("enchantment.command.min", enchantment.minLevel());
			nextLevel = enchantment.minLevel();
		}
		EnchantmentContainer.increaseLevel(itemStack, enchantment, level);
		String message = "";
		if (nextLevel > currentLevel) {
			message = TRANSLATE.translateKeyAndFormat("enchantment.command.increased", TRANSLATE.translateKeyAndFormat(enchantment.translationKeyName()), currentLevel, nextLevel);
		} else if (nextLevel < currentLevel) {
			message = TRANSLATE.translateKeyAndFormat("enchantment.command.decreased", TRANSLATE.translateKeyAndFormat(enchantment.translationKeyName()), currentLevel, nextLevel);
		} else {
			message = TRANSLATE.translateKey("enchantment.command.no.change");
		}
		ctx.getSource().sendMessage(message);
		return Command.SINGLE_SUCCESS;
	}

	private static ArgumentBuilderLiteral<CommandSource> buildRemoveCommand() {
		return ArgumentBuilderLiteral.<CommandSource>literal("remove")
			.requires(src -> src.hasAdmin() && src.getSender() != null)
			.then(ArgumentBuilderLiteral.<CommandSource>literal("all")
				.executes(CommandEnchantment::removeAllEnchantments)
			)
			.then(ArgumentBuilderRequired.<CommandSource, Enchantment>argument("name", ArgumentTypeEnchantment.enchantments())
				.executes(CommandEnchantment::removeEnchantment)
			);
	}

	private static int removeAllEnchantments(CommandContext<CommandSource> ctx) throws CommandSyntaxException {
		final Player player = ctx.getSource().getSender();
		if (player == null) {
			throw NOT_APPLICABLE.create();
		}
		final ItemStack itemStack = player.getHeldItem();
		if (itemStack == null) {
			throw NOT_APPLICABLE.create();
		}
		if (!EnchantmentContainer.hasEnchantments(itemStack)) {
			ctx.getSource().sendTranslatableMessage("enchantment.command.not.enchanted", itemStack.getDisplayName());
			return code(FAIL);
		}
		int levelCount = EnchantmentContainer.getEnchantments(itemStack).stream().mapToInt(EnchantmentStack::getLevel).sum();
		int count = EnchantmentContainer.removeAllEnchantment(itemStack);
		ctx.getSource().sendTranslatableMessage("enchantment.command.remove.all", count, levelCount);
		return count;
	}

	private static int removeEnchantment(CommandContext<CommandSource> ctx) throws CommandSyntaxException {
		final Player player = ctx.getSource().getSender();
		if (player == null) {
			throw NOT_APPLICABLE.create();
		}
		final Enchantment enchantment = ctx.getArgument("name", Enchantment.class);
		final ItemStack itemStack = player.getHeldItem();
		if (itemStack == null) {
			throw NOT_APPLICABLE.create();
		}
		if (!EnchantmentContainer.hasEnchantments(itemStack)) {
			ctx.getSource().sendTranslatableMessage("enchantment.command.not.enchanted", itemStack.getDisplayName());
			return code(FAIL);
		}
		if (!EnchantmentContainer.contains(itemStack, enchantment)) {
			ctx.getSource().sendTranslatableMessage("enchantment.command.not.fit", itemStack.getDisplayName(), TRANSLATE.translateKeyAndFormat(enchantment.translationKeyName()));
			return code(FAIL);
		}
		final EnchantmentStack enchantmentStack = EnchantmentContainer.removeEnchantment(itemStack, enchantment);
		if (enchantmentStack == null) {
			ctx.getSource().sendTranslatableMessage("enchantment.command.remove.cannot", TRANSLATE.translateKeyAndFormat(enchantment.translationKeyName()));
			return code(FAIL);
		}
		ctx.getSource().sendTranslatableMessage("enchantment.command.remove.single", enchantmentStack.prettyToString());
		return Command.SINGLE_SUCCESS;
	}
}
