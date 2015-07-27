package trip.jsr.cdi.singleton;

import java.util.Arrays;
import java.util.List;

import javax.enterprise.inject.Produces;
import javax.inject.Singleton;

@Singleton
public class ListOfStringsProducer {

	@Produces
	public List<String> produceNames() {
		return Arrays.asList( "Miere", "Poppins", "Helden", "Lissie" );
	}
}
