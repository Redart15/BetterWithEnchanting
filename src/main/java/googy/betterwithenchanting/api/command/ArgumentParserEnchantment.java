package googy.betterwithenchanting.api.command;

import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import googy.betterwithenchanting.api.Enchantment;
import googy.betterwithenchanting.api.Enchantments;
import net.minecraft.core.lang.I18n;
import net.minecraft.core.net.command.helpers.ArgumentParser;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class ArgumentParserEnchantment extends ArgumentParser {
	private Enchantment enchantment;
	private static final SimpleCommandExceptionType INVALID_ENCHANTMENT = new SimpleCommandExceptionType(new LiteralMessage(I18n.getInstance().translateKey("enchantment.command.invalid")));

	protected ArgumentParserEnchantment(StringReader reader) {super(reader);}

	private CompletableFuture<Suggestions> suggestItems(SuggestionsBuilder suggestionsBuilder, Consumer<SuggestionsBuilder> consumer) {
		SuggestionsBuilder suggestionsBuilder2 = suggestionsBuilder.createOffset(this.startPosition);
		consumer.accept(suggestionsBuilder2);
		return suggestionsBuilder.add(suggestionsBuilder2).buildFuture();
	}

	public Enchantment parse() throws CommandSyntaxException {
		this.startPosition = this.reader.getCursor();
		this.suggestions = this::suggestItems;
		this.parseEnchantment();
		if (this.enchantment == null) {
			throw INVALID_ENCHANTMENT.createWithContext(this.reader);
		} else {
			return this.enchantment;
		}
	}

	private void parseEnchantment(){
		StringBuilder builder = new StringBuilder();
		while (this.reader.canRead()) {
			char peak = this.reader.peek();
			if (peak == '[' || peak == '{' || peak == ' ') {
				break;
			}
			builder.append(this.reader.read());
		}
		String string = builder.toString();
		this.enchantment = Enchantments.getInstance().getItem(string);
	}
}
