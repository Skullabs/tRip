package trip.jsr.cdi.singleton;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;

import org.junit.Before;
import org.junit.Test;

import trip.jsr.cdi.CDIServiceProvider;

public class SingletonBehaviorTest {

	final CDIServiceProvider provider = new CDIServiceProvider();

	@Inject
	Closeable closeable;

	@Inject
	Reader reader;

	@Inject
	HelloWorldReader helloReader;

	@Inject
	List<String> names;

	@Before
	public void provideDependencies(){
		provider.provideOn( this );
	}

	@Test
	public void ensureThatBothInjectedDataIsSame(){
		assertSame(closeable, helloReader);
		assertSame(reader, helloReader);
	}

	@Test
	public void ensureThatHaveTheNames() {
		assertNotNull( names );
		assertEquals( "Miere", names.get( 0 ) );
	}

	@Test
	public void ensureThatGetProviderMethodWorksAsExpected() {
		final Provider<HelloWorldReader> providerFor = provider.getProviderFor( HelloWorldReader.class );
		assertEquals( helloReader, providerFor.get() );
	}
}
