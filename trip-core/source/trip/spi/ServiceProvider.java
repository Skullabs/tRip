package trip.spi;

import trip.spi.helpers.filter.Condition;

/**
 * The main tRip Context. It manages singleton and stateless instances,
 * inject data into beans and create new instance of classes that could
 * benefits with the tRip's injection mechanism.
 */
public interface ServiceProvider {

	/**
	 * Load a service represented by the argument {@code interfaceClazz}.
	 * If no service was found, it will try to instantiate the class and
	 * inject data.
	 *
	 * @param interfaceClazz - the service interface(or class) representation
	 * @return - the loaded or created service.
	 */
	<T> T load(Class<T> interfaceClazz);

	/**
	 * Load a service represented by the argument {@code interfaceClazz}.
	 * If no service was found, it will try to instantiate the class and
	 * inject data.
	 *
	 * @param interfaceClazz - the service interface(or class) representation
	 * @param condition - a filter condition
	 * @return - the loaded or created service.
	 */
	<T> T load(Class<T> interfaceClazz, Condition<T> condition);

	<T> T load(Class<T> interfaceClazz, ProviderContext context);

	<T> T load(Class<T> interfaceClazz, Condition<T> condition, ProviderContext context);

	/**
	 * Load all services represented by the argument {@code interfaceClazz}.
	 * If no service was found, it will try to instantiate the class,
	 * inject data and return an {@link Iterable} with this instance.
	 *
	 * @param interfaceClazz - the service interface(or class) representation
	 * @param condition - a filter condition
	 * @return - all loaded or created services.
	 */
	<T> Iterable<T> loadAll(Class<T> interfaceClazz, Condition<T> condition);

	/**
	 * Load all services represented by the argument {@code interfaceClazz}.
	 * If no service was found, it will try to instantiate the class,
	 * inject data and return an {@link Iterable} with this instance.
	 *
	 * @param interfaceClazz - the service interface(or class) representation
	 * @return - all loaded or created services.
	 */
	<T> Iterable<T> loadAll(Class<T> interfaceClazz);

	/**
	 * Defines a factory to be invoked every time a service represented with
	 * {@code interfaceClazz} is requested.
	 *
	 * @param interfaceClazz - the service interface(or class) representation
	 * @param provider - the producer implementation
	 */
	<T> void providerFor(Class<T> interfaceClazz, ProducerFactory<T> provider);

	/**
	 * Defines a factory to be invoked every time a service represented with
	 * {@code interfaceClazz} is requested.
	 *
	 * @param interfaceClazz - the service interface(or class) representation
	 * @param object - the service implementation
	 */
	<T> void providerFor(Class<T> interfaceClazz, T object);

	/**
	 * Inject data into all objects present in {@code iterable}.
	 *
	 * @param iterable - the set of objects that will receive "injectable" services.
	 */
	<T> void provideOn(Iterable<T> iterable);

	/**
	 * Inject data into {@code object}.
	 *
	 * @param object - the objects that will receive "injectable" services.
	 */
	void provideOn(Object object);
}