package trip.spi.tests;

import static org.junit.Assert.assertEquals;
import lombok.val;

import org.junit.Before;
import org.junit.Test;

import trip.spi.DefaultServiceProvider;
import trip.spi.Provided;
import trip.spi.ServiceProviderException;

public class PostConstructAndPreDestroyStatelessServiceTest {

	@Provided
	PostConstructAndPreDestroyStatelessService stateless;

	@Provided
	PostConstructorSingletonService singleton;

	@Before
	public void provideDependencies() throws ServiceProviderException {
		new DefaultServiceProvider().provideOn( this );
	}

	@Test
	public void ensureThatCalledAllCallbacksOnStateless() {
		val status = stateless.getStatus();
		assertEquals( 1, status.calledPostContructJavaAnnotation );
		assertEquals( 1, status.calledPreDestroyJavaAnnotation );
	}

	@Test
	public void ensureThatCalledOnlyPostConstructorCallbacksOnSingleton() {
		val status = singleton.getStatus();
		assertEquals( 1, status.calledPostContructJavaAnnotation );
		assertEquals( 0, status.calledPreDestroyJavaAnnotation );
	}
}
