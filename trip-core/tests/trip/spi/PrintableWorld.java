package trip.spi;

import lombok.Getter;

@Getter
public class PrintableWorld implements PrintableWord {

	@Provided( name = "period" )
	Closure closure;

	@Override
	public String getWord() {
		return "World" + this.closure.getSentenceClosureChar();
	}
}
