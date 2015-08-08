package trip.jsr.cdi.producer;

import static org.junit.Assert.*;

import java.util.concurrent.atomic.AtomicInteger;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;

import trip.jsr.cdi.CDIServiceProvider;

public class MultipleSingletonConsumerOfProducedDataBehaviorTest {

	final CDIServiceProvider provider = new CDIServiceProvider();
	final AtomicInteger databaseProducerPostConstructorCallCounter = new AtomicInteger();

	@Inject UserRepository userRepository;
	@Inject TenantRepository tenantRepository;

	@Before
	public void provideDependencies() {
		provider.providerFor( AtomicInteger.class, databaseProducerPostConstructorCallCounter );
		provider.provideOn( this );
	}

	@Test
	public void shouldBeAbleToInjectedBothSingletonConsumers() {
		assertNotNull( userRepository );
		assertNotNull( userRepository.getDatabase() );
		assertNotNull( tenantRepository );
		assertNotNull( tenantRepository.getDatabase() );
	}

	@Test
	public void shouldCallDatabaseProducerPostConstructorOnlyOnce() {
		assertEquals( 1, databaseProducerPostConstructorCallCounter.get() );
	}
}
