package googy.betterwithenchanting.api.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.arguments.ArgumentTypeInteger;
import com.mojang.brigadier.builder.ArgumentBuilderLiteral;
import com.mojang.brigadier.builder.ArgumentBuilderRequired;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import googy.betterwithenchanting.api.Enchantment;
import googy.betterwithenchanting.api.EnchantmentContainer;
import googy.betterwithenchanting.api.EnchantmentStack;
import googy.betterwithenchanting.api.Enchantments;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.net.command.CommandManager;
import net.minecraft.core.net.command.CommandSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static googy.betterwithenchanting.BetterWithEnchanting.TRANSLATE;
import static googy.betterwithenchanting.api.command.CommandEnchantments.ReturnValues.*;

public class CommandEnchantments implements CommandManager.CommandRegistry {

	public enum ReturnValues {
		CANNOT(-2),
		FAIL(-1),
		OK(0);
		private final int code;

		ReturnValues(int code) {
			this.code = code;
		}

		public static int code(ReturnValues v) {
			return v.code;
		}
	}


	public static SimpleCommandExceptionType NOT_APPLICABLE;
	private static final Random RANDOM = new Random();

	@Override
	public void register(CommandDispatcher<CommandSource> commandDispatcher) {
		NOT_APPLICABLE = new SimpleCommandExceptionType(new LiteralMessage(TRANSLATE.translateKey("enchantment.command.not.applicable")));

		//TODO: add way to apply the enchantmenttable enchanting
		commandDispatcher.register(
			ArgumentBuilderLiteral.<CommandSource>literal("enchant")
				.then(ArgumentBuilderLiteral.<CommandSource>literal("list")
					.then(ArgumentBuilderLiteral.<CommandSource>literal("all")
						.executes(CommandEnchantments::listAllEnchantments)
					)
					.then(ArgumentBuilderLiteral.<CommandSource>literal("applicable")
						.executes(CommandEnchantments::listApplicableEnchantments)
					)
				)
				.then(ArgumentBuilderLiteral.<CommandSource>literal("info")
					.then(ArgumentBuilderRequired.<CommandSource, Enchantment>argument("name", ArgumentTypeEnchantment.enchantments())
						.executes(CommandEnchantments::listInfo)
					)
				)
				.then(ArgumentBuilderLiteral.<CommandSource>literal("random")
					.requires(src -> src.hasAdmin() && src.getSender() != null)
					.then(ArgumentBuilderRequired.<CommandSource, Integer>argument("cost", ArgumentTypeInteger.integer())
						.executes(CommandEnchantments::randomEnchant)
					)
				)
				.then(ArgumentBuilderLiteral.<CommandSource>literal("add")
					.requires(src -> src.hasAdmin() && src.getSender() != null)
					.then(ArgumentBuilderRequired.<CommandSource, Enchantment>argument("name", ArgumentTypeEnchantment.enchantments())
						.then(ArgumentBuilderRequired.<CommandSource, Integer>argument("level", ArgumentTypeInteger.integer())
							.executes(ctx -> addEnchantment(ctx, 0))
						)
					)
				)
				.then(ArgumentBuilderLiteral.<CommandSource>literal("increase")
					.requires(src -> src.hasAdmin() && src.getSender() != null)
					.then(ArgumentBuilderRequired.<CommandSource, Enchantment>argument("name", ArgumentTypeEnchantment.enchantments())
						.then(ArgumentBuilderRequired.<CommandSource, Integer>argument("level", ArgumentTypeInteger.integer())
							.executes(ctx -> increaseEnchantmentLevel(ctx, 0))
						)
					)
				)
				.then(ArgumentBuilderLiteral.<CommandSource>literal("remove")
					.requires(src -> src.hasAdmin() && src.getSender() != null)
					.then(ArgumentBuilderLiteral.<CommandSource>literal("all")
						.executes(CommandEnchantments::removeAllEnchantments)
					)
					.then(ArgumentBuilderRequired.<CommandSource, Enchantment>argument("name", ArgumentTypeEnchantment.enchantments())
						.executes(CommandEnchantments::removeEnchantment)
					)
				)
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

	private static int listInfo(CommandContext<CommandSource> ctx) throws CommandSyntaxException {
		final Player player = ctx.getSource().getSender();
		if (player == null) {
			throw NOT_APPLICABLE.create();
		}
		final Enchantment enchantment = ctx.getArgument("name", Enchantment.class);
		String message = String.format("%s: %s", enchantment.prettyToString(), TRANSLATE.translateDescKey(enchantment.translationKey()));
		ctx.getSource().sendMessage(message);
		return Command.SINGLE_SUCCESS;
	}

	private static int randomEnchant(CommandContext<CommandSource> ctx) throws CommandSyntaxException{
		Player player = ctx.getSource().getSender();
		if (player == null) {
			throw NOT_APPLICABLE.create();
		}
		final int cost = ctx.getArgument("cost", Integer.class);
		final ItemStack itemStack = player.getHeldItem();
		if (itemStack == null) {
			throw NOT_APPLICABLE.create();
		}
		if(EnchantmentContainer.hasEnchantments(itemStack) || !EnchantmentContainer.hasApplicable(itemStack)) {
			ctx.getSource().sendTranslatableMessage("enchantment.command.cannot");
			return code(FAIL);
		}
		List<EnchantmentStack> enchantmentStackList = EnchantmentContainer.generateEnchantmentsList(RANDOM, itemStack, cost);
		if(enchantmentStackList.isEmpty()){
			ctx.getSource().sendTranslatableMessage("enchantment.command.none");
			return code(FAIL);
		}
		EnchantmentContainer.addEnchantments(itemStack, enchantmentStackList);
		ctx.getSource().sendTranslatableMessage("enchantment.command.enchant", itemStack.getDisplayName(), EnchantmentContainer.prettyPrint(itemStack));
		return enchantmentStackList.size();
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
		EnchantmentContainer.addEnchantment(itemStack, new EnchantmentStack(enchantment, level));
		ctx.getSource().sendTranslatableMessage("enchantment.command.add", TRANSLATE.translateNameKey(enchantment.translationKey()));
		return Command.SINGLE_SUCCESS;
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
			message = TRANSLATE.translateKeyAndFormat("enchantment.command.increased", TRANSLATE.translateNameKey(enchantment.translationKey()), currentLevel, nextLevel);
		} else if (nextLevel < currentLevel) {
			message = TRANSLATE.translateKeyAndFormat("enchantment.command.decreased", TRANSLATE.translateNameKey(enchantment.translationKey()), currentLevel, nextLevel);
		} else {
			message = TRANSLATE.translateKey("enchantment.command.no.change");
		}
		ctx.getSource().sendMessage(message);
		return Command.SINGLE_SUCCESS;
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
			ctx.getSource().sendTranslatableMessage("enchantment.command.not.fit", itemStack.getDisplayName(), TRANSLATE.translateNameKey(enchantment.translationKey()));
			return code(FAIL);
		}
		final EnchantmentStack enchantmentStack = EnchantmentContainer.removeEnchantment(itemStack, enchantment);
		if(enchantmentStack == null){
			ctx.getSource().sendTranslatableMessage("enchantment.command.remove.cannot", TRANSLATE.translateNameKey(enchantment.translationKey()));
			return code(FAIL);
		}
		ctx.getSource().sendTranslatableMessage("enchantment.command.remove.single", enchantmentStack.prettyToString());
		return Command.SINGLE_SUCCESS;
	}
}
