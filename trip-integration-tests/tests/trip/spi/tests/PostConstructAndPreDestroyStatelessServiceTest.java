package trip.spi.tests;

import static org.junit.Assert.assertTrue;
import lombok.val;

import org.junit.Before;
import org.junit.Test;

import trip.spi.Provided;
import trip.spi.DefaultServiceProvider;
import trip.spi.ServiceProviderException;

public class PostConstructAndPreDestroyStatelessServiceTest {

	@Provided
	PostConstructAndPreDestroyStatelessService service;

	@Before
	public void provideDependencies() throws ServiceProviderException {
		new DefaultServiceProvider().provideOn( this );
	}

	@Test
	public void ensureThatCalledAllCallbacks() {
		val status = service.getStatus();
		assertTrue( status.calledPostContructJavaAnnotation );
		assertTrue( status.calledPreDestroyJavaAnnotation );
		assertTrue( status.calledPostContructTrip );
		assertTrue( status.calledPreDestroyTrip );
	}
}
