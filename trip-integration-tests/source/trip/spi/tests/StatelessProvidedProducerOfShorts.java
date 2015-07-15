package trip.spi.tests;

import trip.spi.Producer;
import trip.spi.Stateless;

@Stateless( exposedAs = ProducerOfShorts.class )
public class StatelessProvidedProducerOfShorts implements ProducerOfShorts {

	volatile short counter;

	@Override
	@Producer
	public Short produceShort() {
		return incrementAndReturn();
	}

	private short incrementAndReturn() {
		return counter++;
	}
}
