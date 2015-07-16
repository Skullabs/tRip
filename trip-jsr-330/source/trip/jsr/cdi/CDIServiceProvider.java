package trip.jsr.cdi;

import javax.inject.Provider;

import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;
import trip.spi.DefaultServiceProvider;
import trip.spi.ProducerFactory;
import trip.spi.ServiceProvider;
import trip.spi.helpers.filter.AnyObject;
import trip.spi.helpers.filter.Condition;
import trip.spi.helpers.filter.Filter;

@RequiredArgsConstructor
@SuppressWarnings({"rawtypes","unchecked"})
public class CDIServiceProvider implements ServiceProvider {

	private final static Condition ANY = new AnyObject<>();

	@Delegate( types=ServiceProvider.class )
	final DefaultServiceProvider wrapped;

	public CDIServiceProvider() {
		wrapped = new DefaultServiceProvider();
	}

	public <T> Provider<T> getProviderFor( Class<T> clazz ) {
		final ProducerFactory factory = wrapped.getProviderFor(clazz, ANY);
		if ( factory != null )
			return new ProviderWrapper<>( factory );
		T t = Filter.first( wrapped.loadAll( clazz ), AnyObject.instance() );
		return new SingleElementProvider<T>( t );
	}
}
