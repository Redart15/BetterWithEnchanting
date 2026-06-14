package googy.betterwithenchanting.api.command;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import googy.betterwithenchanting.api.Enchantment;
import googy.betterwithenchanting.api.Enchantments;
import net.minecraft.core.net.command.util.CommandHelper;

import java.util.Locale;
import java.util.concurrent.CompletableFuture;

public class ArgumentTypeEnchantment implements ArgumentType<Enchantment>{

	public static ArgumentType<Enchantment> enchantments() {
		return new ArgumentTypeEnchantment();
	}

	///  implement them
	@Override
	public Enchantment parse(StringReader stringReader) throws CommandSyntaxException {
		ArgumentParserEnchantment parser = new ArgumentParserEnchantment(stringReader);
		return parser.parse();
	}

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
		StringReader stringReader = new StringReader(builder.getInput());
		stringReader.setCursor(builder.getStart());
		ArgumentParserEnchantment parser = new ArgumentParserEnchantment(stringReader);
		try{
			parser.parse();
		}catch (CommandSyntaxException exception) {
			// reader failed to parse the string
		}
		return parser.fillSuggestions(builder, ArgumentTypeEnchantment::suggestEnchantments);
	}

	private static void suggestEnchantments(SuggestionsBuilder suggestionsBuilder) {
		String remaining = suggestionsBuilder.getRemaining().toLowerCase(Locale.ROOT);
		Enchantments.getInstance().forEach((enchantment) -> transform(suggestionsBuilder, enchantment, remaining));
		suggestionsBuilder.buildFuture();
	}

	private static void transform(SuggestionsBuilder suggestionsBuilder, Enchantment enchantment, String remaining) {
		CommandHelper.getStringToSuggest(enchantment.id().toLowerCase(Locale.ROOT), remaining).ifPresent(suggestionsBuilder::suggest);
	}
}
