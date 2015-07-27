package trip.spi.tests;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import trip.spi.Provided;
import trip.spi.DefaultServiceProvider;
import trip.spi.ServiceProviderException;

public class InjectionOfServiceDefinedByItsExposedTypeTest {

	final DefaultServiceProvider provider = new DefaultServiceProvider();

	@Provided( exposedAs = Bean.class )
	SerializableBean bean;

	@Test
	public void ensureThatAreAbleToLoadSerializableBeanExposedAsSerializable() throws ServiceProviderException {
		final Bean loaded = provider.load( Bean.class );
		assertThat( loaded, instanceOf( SerializableBean.class ) );
	}

	@Test
	public void ensureThatCouldProvideSerializableBeanExposedAsSerializable() throws ServiceProviderException {
		provider.provideOn( this );
		assertNotNull( bean );
		assertThat( bean, instanceOf( SerializableBean.class ) );
	}
}
