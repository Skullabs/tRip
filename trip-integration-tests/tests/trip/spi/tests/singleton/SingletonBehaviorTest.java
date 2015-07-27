package trip.spi.tests.singleton;

import static org.junit.Assert.assertSame;

import org.junit.Before;
import org.junit.Test;

import trip.spi.DefaultServiceProvider;
import trip.spi.Provided;

public class SingletonBehaviorTest {

	@Provided
	Closeable closeable;

	@Provided
	Reader reader;

	@Provided
	HelloWorldReader helloReader;

	@Before
	public void provideDependencies(){
		new DefaultServiceProvider().provideOn(this);
	}

	@Test
	public void ensureThatBothInjectedDataIsSame(){
		assertSame(closeable, helloReader);
		assertSame(reader, helloReader);
	}
}
