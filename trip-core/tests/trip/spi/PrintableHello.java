package trip.spi;

import lombok.Getter;
import trip.spi.Provided;

@Getter
public class PrintableHello implements Printable {

	@Provided PrintableWord word;

	@Override
	public String toString() {
		return "Hello " + word.getWord();
	}
}
