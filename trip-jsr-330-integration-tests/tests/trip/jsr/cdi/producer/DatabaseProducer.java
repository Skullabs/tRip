package trip.jsr.cdi.producer;

import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.PostConstruct;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class DatabaseProducer {

	@Inject
	AtomicInteger postConstructorCallCounter;

	@PostConstruct
	public void postConstructor() {
		postConstructorCallCounter.incrementAndGet();
	}

	@Produces
	public Database produceDatabase() {
		return new Database();
	}
}
