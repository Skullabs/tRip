package trip.spi;

import java.util.HashMap;
import java.util.Map;
import java.util.ServiceConfigurationError;

import lombok.val;
import lombok.experimental.ExtensionMethod;
import trip.spi.helpers.EmptyProviderContext;
import trip.spi.helpers.KeyValueProviderContext;
import trip.spi.helpers.ProducerFactoryMap;
import trip.spi.helpers.ProvidableClass;
import trip.spi.helpers.SingleObjectIterable;
import trip.spi.helpers.cache.CachedIterable;
import trip.spi.helpers.cache.ServiceLoader;
import trip.spi.helpers.filter.AnyClass;
import trip.spi.helpers.filter.AnyObject;
import trip.spi.helpers.filter.Condition;
import trip.spi.helpers.filter.Filter;
import trip.spi.helpers.filter.NamedClass;
import trip.spi.helpers.filter.NamedObject;

@ExtensionMethod( Filter.class )
public class ServiceProvider {

	final Map<Class<?>, ProvidableClass<?>> providableClassCache = new HashMap<Class<?>, ProvidableClass<?>>();
	final Map<Class<?>, Iterable<Class<?>>> implementedClasses = new HashMap<Class<?>, Iterable<Class<?>>>();
	final Map<Class<?>, Iterable<?>> providers;
	final ProducerFactoryMap producers;

	public ServiceProvider() {
		this.providers = createDefaultProvidedData();
		runAllStartupListeners();
		this.producers = loadAllProducers();
	}

	void runAllStartupListeners() {
		try {
			final Iterable<StartupListener> startupListeners = loadAll( StartupListener.class );
			for ( final StartupListener listener : startupListeners )
				listener.onStartup( this );
		} catch ( final ServiceProviderException e ) {
			throw new IllegalStateException( e );
		}
	}

	protected HashMap<Class<?>, Iterable<?>> createDefaultProvidedData() {
		val injectables = new HashMap<Class<?>, Iterable<?>>();
		injectables.put( getClass(), new SingleObjectIterable<ServiceProvider>( this ) );
		return injectables;
	}

	protected ProducerFactoryMap loadAllProducers() {
		try {
			return ProducerFactoryMap.from( loadAll( ProducerFactory.class ) );
		} catch ( final ServiceProviderException e ) {
			throw new IllegalStateException( e );
		}
	}

	public <T> T load( final Class<T> interfaceClazz ) throws ServiceProviderException {
		return load( interfaceClazz, new AnyObject<T>() );
	}

	public <T> T load( final Class<T> interfaceClazz, final String name ) throws ServiceProviderException {
		return load( interfaceClazz, new NamedObject<T>( name ) );
	}

	public <T> T load( final Class<T> interfaceClazz, final Condition<T> condition ) throws ServiceProviderException {
		return load( interfaceClazz, condition, new EmptyProviderContext() );
	}

	public <T> T load( final Class<T> interfaceClazz, final ProviderContext context ) throws ServiceProviderException {
		return load( interfaceClazz, new AnyObject<T>(), context );
	}

	public <T> T load( final Class<T> interfaceClazz, final Map<String, Object> contextData ) throws ServiceProviderException {
		return load( interfaceClazz, new AnyObject<T>(), new KeyValueProviderContext( contextData ) );
	}

	public <T> T load( final Class<T> interfaceClazz, final String name, final Map<String, Object> contextData )
			throws ServiceProviderException {
		return load( interfaceClazz, new NamedObject<T>( name ), new KeyValueProviderContext( contextData ) );
	}

	public <T> T load( final Class<T> interfaceClazz, final Condition<T> condition, final ProviderContext context )
			throws ServiceProviderException {
		final T produced = produceFromFactory( interfaceClazz, condition, context );
		if ( produced != null )
			return produced;
		return loadAll( interfaceClazz, condition ).first( condition );
	}

	@SuppressWarnings( "unchecked" )
	private <T> ProducerFactory<T> getProviderFor( final Class<T> interfaceClazz,
			final Condition<T> condition ) {
		if ( this.producers == null )
			return null;
		return (ProducerFactory<T>)this.producers.get( interfaceClazz, condition );
	}

	public <T> Iterable<T> loadAll( final Class<T> interfaceClazz, final String name ) throws ServiceProviderException {
		return loadAll( interfaceClazz, new NamedObject<T>( name ) );
	}

	public <T> Iterable<T> loadAll( final Class<T> interfaceClazz, final Condition<T> condition ) throws ServiceProviderException {
		return loadAll( interfaceClazz ).filter( condition );
	}

	@SuppressWarnings( "unchecked" )
	public <T> Iterable<T> loadAll( final Class<T> interfaceClazz ) throws ServiceProviderException {
		Iterable<T> iterable = (Iterable<T>)this.providers.get( interfaceClazz );
		if ( iterable == null )
			synchronized ( providers ) {
				iterable = (Iterable<T>)this.providers.get( interfaceClazz );
				if ( iterable == null )
					iterable = loadAllServicesImplementingTheInterface( interfaceClazz );
			}
		return iterable;
	}

	protected <T> Iterable<T> loadAllServicesImplementingTheInterface( final Class<T> interfaceClazz )
			throws ServiceProviderException {
		try {
			final CachedIterable<T> iterable = loadServiceProvidersFor( interfaceClazz );
			provideOn( iterable );
			providerFor( interfaceClazz, iterable );
			return iterable;
		} catch ( final StackOverflowError cause ) {
			throw new ServiceConfigurationError(
				"Could not load implementations of " + interfaceClazz.getCanonicalName() +
					": Recursive dependency injection detected." );
		}
	}

	protected <T> CachedIterable<T> loadServiceProvidersFor(
			final Class<T> interfaceClazz ) throws ServiceProviderException {
		final Iterable<Class<T>> iterableInterfaces = loadClassesImplementing( interfaceClazz );
		return ServiceLoader.loadFrom( iterableInterfaces );
	}

	public <T> Class<T> loadClassImplementing( final Class<T> interfaceClazz ) {
		return loadClassImplementing( interfaceClazz, new AnyClass<T>() );
	}

	public <T> Class<T> loadClassImplementing( final Class<T> interfaceClazz, final String named ) {
		return loadClassImplementing( interfaceClazz, new NamedClass<T>( named ) );
	}

	public <T> Class<T> loadClassImplementing( final Class<T> interfaceClazz, final Condition<Class<T>> condition ) {
		return loadClassesImplementing( interfaceClazz ).first( condition );
	}

	public <T> Iterable<Class<T>> loadClassesImplementing( final Class<T> interfaceClazz, final Condition<Class<T>> condition ) {
		return loadClassesImplementing( interfaceClazz ).filter( condition );
	}

	@SuppressWarnings( { "rawtypes", "unchecked" } )
	public <T> Iterable<Class<T>> loadClassesImplementing( final Class<T> interfaceClazz ) {
		Iterable<Class<T>> implementations = (Iterable)implementedClasses.get( interfaceClazz );
		if ( implementations == null )
			synchronized ( implementedClasses ) {
				implementations = (Iterable)implementedClasses.get( interfaceClazz );
				if ( implementations == null ) {
					implementations = ServiceLoader.loadImplementationsFor( interfaceClazz );
					implementedClasses.put( (Class)interfaceClazz, (Iterable)implementations );
				}
			}
		return implementations;
	}

	public <T> void providerFor( final Class<T> interfaceClazz, final ProducerFactory<T> provider ) {
		this.producers.memorizeProviderForClazz( provider, interfaceClazz );
	}

	public <T> void providerFor( final Class<T> interfaceClazz, final T object ) {
		providerFor( interfaceClazz, new SingleObjectIterable<T>( object ) );
	}

	protected <T> void providerFor( final Class<T> interfaceClazz, final Iterable<T> iterable ) {
		this.providers.put( interfaceClazz, iterable );
	}

	public <T> void provideOn( final Iterable<T> iterable ) throws ServiceProviderException {
		for ( final T object : iterable )
			provideOn( object );
	}

	public void provideOn( final Object object ) throws ServiceProviderException {
		try {
			final ProvidableClass<?> providableClass = retrieveProvidableClass( object.getClass() );
			providableClass.provide( object, this );
		} catch ( final Exception cause ) {
			throw new ServiceProviderException( cause );
		}
	}

	private ProvidableClass<?> retrieveProvidableClass( final Class<?> targetClazz ) {
		ProvidableClass<?> providableClass = providableClassCache.get( targetClazz );
		if ( providableClass == null )
			synchronized ( providableClassCache ) {
				providableClass = providableClassCache.get( targetClazz );
				if ( providableClass == null ) {
					providableClass = ProvidableClass.wrap( targetClazz );
					providableClassCache.put( targetClazz, providableClass );
				}
			}
		return providableClass;
	}

	private <T> T produceFromFactory( final Class<T> interfaceClazz, final Condition<T> condition, final ProviderContext context )
			throws ServiceProviderException {
		final ProducerFactory<T> provider = getProviderFor( interfaceClazz, condition );
		if ( provider != null )
			return provider.provide( context );
		return null;
	}
}
