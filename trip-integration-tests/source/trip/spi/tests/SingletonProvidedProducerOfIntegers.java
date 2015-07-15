package trip.spi.tests;

import java.util.concurrent.atomic.AtomicInteger;

import trip.spi.Producer;
import trip.spi.Singleton;

@Singleton
public class SingletonProvidedProducerOfIntegers {

	final AtomicInteger counter = new AtomicInteger();

	@Producer
	public Integer produceInteger() {
		return counter.incrementAndGet();
	}
}
