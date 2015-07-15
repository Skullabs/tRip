package trip.spi;

import lombok.Getter;

@Getter
public class PrintableWorld implements PrintableWord {

	@Period
	@Provided
	Closure closure;

	@Override
	public String getWord() {
		return "World" + this.closure.getSentenceClosureChar();
	}
}
