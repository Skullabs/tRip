package trip.spi;

import trip.spi.helpers.QualifierExtractor;
import trip.spi.helpers.filter.Condition;

public interface ServiceProvider {

	<T> T load(Class<T> interfaceClazz);

	<T> T load(Class<T> interfaceClazz, Condition<T> condition);

	<T> T load(Class<T> interfaceClazz, ProviderContext context);

	<T> T load(Class<T> interfaceClazz, Condition<T> condition, ProviderContext context);

	<T> Iterable<T> loadAll(Class<T> interfaceClazz, Condition<T> condition);

	<T> Iterable<T> loadAll(Class<T> interfaceClazz);

	<T> void providerFor(Class<T> interfaceClazz, ProducerFactory<T> provider);

	<T> void providerFor(Class<T> interfaceClazz, T object);

	<T> void provideOn(Iterable<T> iterable);

	void provideOn(Object object);

	QualifierExtractor getQualifierExtractor();

}