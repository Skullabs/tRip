package trip.jsr.cdi;

import javax.inject.Provider;

import lombok.RequiredArgsConstructor;
import trip.spi.ProducerFactory;
import trip.spi.helpers.EmptyProviderContext;

@RequiredArgsConstructor
public class ProviderWrapper<T> implements Provider<T> {

	final ProducerFactory<T> factory;

	@Override
	public T get() {
		return factory.provide(EmptyProviderContext.INSTANCE);
	}
}