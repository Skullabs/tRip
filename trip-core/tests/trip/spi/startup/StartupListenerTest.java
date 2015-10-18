package trip.spi.startup;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import trip.spi.DefaultServiceProvider;
import trip.spi.Provided;

public class StartupListenerTest {

	@Provided
	Configuration injectedByStartupListener;

	@Test( timeout = 1000l )
	public void ensureThatConfigurationWasInjectedByStartupListenerAsExpected() {
		new DefaultServiceProvider().provideOn( this );

		assertNotNull( injectedByStartupListener );
		assertThat( injectedByStartupListener.getExpectedConfig(),
			is( ConfigurationStartupListener.EXPECTED_CONFIG ) );
	}

}
