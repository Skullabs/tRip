package trip.spi;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

public class ProviderTest {

	final DefaultServiceProvider provider = new DefaultServiceProvider();
	
	@Before
	public void grantThatProvidedHasNoCachedData() {
		Iterable<?> nullIterable = this.provider.providers.get( Printable.class );
		assertNull(nullIterable);
		nullIterable = this.provider.implementedClasses.get( Printable.class );
		assertNull(nullIterable);
	}

	@Test
	public void grantThatInjectTestableResourcesButKeepItCachedAsExpected() throws ServiceProviderException {
		grantThatRetrieveAllClassesThatImplementsAnInterface();

		Iterable<Class<?>> implementations = this.provider.implementedClasses.get( Printable.class );
		grantThatRetrieveAWellImplementedPrintableInstanceAsExpected();
		assertEquals( implementations , this.provider.implementedClasses.get( Printable.class ));

		Iterable<?> printableInjectables = this.provider.providers.get( Printable.class );
		grantThatRetrieveAWellImplementedPrintableInstanceAsExpected();
		assertEquals( printableInjectables, this.provider.providers.get( Printable.class ) );
	}

	private void grantThatRetrieveAllClassesThatImplementsAnInterface() {
		Iterable<Class<Printable>> implementations = this.provider.loadClassesImplementing( Printable.class );
		for ( Class<Printable> clazz : implementations )
			if ( PrintableHello.class.equals(clazz) )
				return;
		fail( "Expected to find a Printable implementation." );
	}

	private void grantThatRetrieveAWellImplementedPrintableInstanceAsExpected() throws ServiceProviderException {
		Printable printable = this.provider.load( Printable.class );
		assertThat( printable.toString(), is( "Hello World." ) );
	}
}
