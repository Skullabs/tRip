package trip.spi.helpers;

import trip.spi.DefaultServiceProvider.DependencyInjector;

public interface ProvidableField {

	public void provide( final Object instance, final DependencyInjector provider ) throws Throwable;
}