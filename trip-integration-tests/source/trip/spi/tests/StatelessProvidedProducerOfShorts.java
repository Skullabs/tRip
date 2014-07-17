package trip.spi.tests;

import trip.spi.Producer;
import trip.spi.Stateless;

@Stateless
public class StatelessProvidedProducerOfShorts implements ProducerOfShorts {

	volatile short counter;

	@Override
	@Producer
	public Short produceShort() {
		return counter++;
	}
}
