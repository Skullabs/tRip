package trip.spi.startup;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import lombok.SneakyThrows;

import org.junit.Before;
import org.junit.Test;

import trip.spi.Provided;
import trip.spi.DefaultServiceProvider;
import trip.spi.ServiceProvider;

public class StartupListenerTest {

	@Provided
	Configuration injectedByStartupListener;

	@Test
	public void ensureThatConfigurationWasInjectedByStartupListenerAsExpected() {
		assertNotNull( injectedByStartupListener );
		assertThat( injectedByStartupListener.getExpectedConfig(),
			is( ConfigurationStartupListener.EXPECTED_CONFIG ) );
	}

	@Before
	@SneakyThrows
	public void provideDependencies() {
		final ServiceProvider provider = new DefaultServiceProvider();
		provider.provideOn( this );
	}
}
