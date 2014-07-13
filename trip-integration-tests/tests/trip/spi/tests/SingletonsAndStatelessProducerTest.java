package trip.spi.tests;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import lombok.val;

import org.junit.Test;

import trip.spi.ServiceProvider;
import trip.spi.ServiceProviderException;

public class SingletonsAndStatelessProducerTest {

	final ServiceProvider provider = new ServiceProvider();

	@Test
	public void ensureThatProduceThreeDifferentNumbers() throws ServiceProviderException {
		assertThat( provider.load( Integer.class ), is( 1 ) );
		assertThat( provider.load( Integer.class ), is( 2 ) );
		assertThat( provider.load( Integer.class ), is( 3 ) );
	}

	@Test
	public void ensureThatCantProduceThreeDifferentShorts() throws ServiceProviderException {
		assertThat( provider.load( Short.class ), is( (short)0 ) );
		assertThat( provider.load( Short.class ), is( (short)0 ) );
		assertThat( provider.load( Short.class ), is( (short)0 ) );
	}
	
	@Test
	public void ensureThatCanProduceUnrepeatedShortsWhenManuallyCreated(){
		val producerOfShorts = new StatelessProvidedProducerOfShorts();
		assertThat( producerOfShorts.produceShort(), is( (short)0 ) );
		assertThat( producerOfShorts.produceShort(), is( (short)1 ) );
		assertThat( producerOfShorts.produceShort(), is( (short)2 ) );
	}
	
	@Test
	public void ensureThatCantProduceUnrepeatedShortsWhenCreatedByServiceProvider() throws ServiceProviderException{
		StatelessProvidedProducerOfShorts producerOfShorts = provider.load(StatelessProvidedProducerOfShorts.class);
		assertThat( producerOfShorts.produceShort(), is( (short)0 ) );
		producerOfShorts = provider.load(StatelessProvidedProducerOfShorts.class);
		assertThat( producerOfShorts.produceShort(), is( (short)0 ) );
		producerOfShorts = provider.load(StatelessProvidedProducerOfShorts.class);
		assertThat( producerOfShorts.produceShort(), is( (short)0 ) );
	}
}