package trip.spi.tests.concurrency;

import java.util.List;

import lombok.val;
import trip.spi.Provided;
import trip.spi.Stateless;
import trip.spi.tests.ann.Names;

@Stateless
public class StatelessService {

	@Provided
	@Names
	List<String> names;

	@Provided
	Printer printer;

	void printNames() {
		val builder = new StringBuilder();
		for ( val name : names )
			builder.append( name ).append( ' ' );
		printer.print( builder.toString() );
	}
}
