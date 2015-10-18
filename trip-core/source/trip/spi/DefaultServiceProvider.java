package trip.spi;

import static trip.spi.helpers.filter.Filter.filter;
import static trip.spi.helpers.filter.Filter.first;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.locks.LockSupport;
import java.util.function.Consumer;
import java.util.function.Function;

import lombok.RequiredArgsConstructor;
import trip.spi.helpers.DependencyMap;
import trip.spi.helpers.DependencyMap.TemporarilyLockedException;
import trip.spi.helpers.EmptyIterable;
import trip.spi.helpers.FieldQualifierExtractor;
import trip.spi.helpers.ProducerFactoryMap;
import trip.spi.helpers.ProvidableClass;
import trip.spi.helpers.ProvidableField;
import trip.spi.helpers.QualifierExtractor;
import trip.spi.helpers.SingleObjectIterable;
import trip.spi.helpers.filter.Condition;

@SuppressWarnings( { "rawtypes", "unchecked" } )
public class DefaultServiceProvider implements ServiceProvider {

	final ImplementedClassesContext implementedClasses = new ImplementedClassesContext();
	final SingletonContext singletonContext = new SingletonContext();

	final DependencyMap dependencies;
	final ProducerFactoryMap producers;

	public DefaultServiceProvider() {
		dependencies = new DependencyMap( createDefaultProvidedData() );
		singletonContext.setQualifierExtractor( createQualifierExtractor() );
		runHookBeforeProducersAreReady();
		producers = loadAllProducers();
		runAllStartupListeners();
	}

	private QualifierExtractor createQualifierExtractor() {
		final Iterable<FieldQualifierExtractor> extractors = loadAll(FieldQualifierExtractor.class);
		return new QualifierExtractor( extractors );
	}

	private void runHookBeforeProducersAreReady() {
		final Iterable<StartupListener> startupListeners = loadAll( StartupListener.class );
		for ( final StartupListener listener : startupListeners )
			listener.beforeProducersReady( this );
	}

	private void runAllStartupListeners() {
		final Iterable<StartupListener> startupListeners = loadAll( StartupListener.class );
		for ( final StartupListener listener : startupListeners )
			listener.onStartup( this );
	}

	protected Map<Class<?>, Iterable<?>> createDefaultProvidedData() {
		final Map<Class<?>, Iterable<?>> injectables = new HashMap<Class<?>, Iterable<?>>();
		injectables.put( ServiceProvider.class, new SingleObjectIterable<DefaultServiceProvider>( this ) );
		return injectables;
	}

	protected ProducerFactoryMap loadAllProducers() {
		final Iterable<Class<ProducerFactory>> loadClassesImplementing = loadClassesImplementing( ProducerFactory.class );
		return ProducerFactoryMap.from( loadClassesImplementing );
	}

	public <T> Iterable<Class<T>> loadClassesImplementing( Class<T> targetClass ) {
		return implementedClasses.loadClassesImplementing( targetClass );
	}

	@Override
	public <T> T load( final Class<T> serviceClazz, final Condition<T> condition, final ProviderContext context )
			throws ServiceProviderException {
		while ( true )
			try { return fromInjector( i -> i.load( serviceClazz, condition, context ) ); }
			catch ( final TemporarilyLockedException cause ) { LockSupport.parkNanos( 2l ); }
	}

	@Override
	public <T> Iterable<T> loadAll( final Class<T> serviceClazz ) {
		while ( true )
			try { return fromInjector( i -> i.loadAll( serviceClazz ) ); }
			catch ( final TemporarilyLockedException cause ) { LockSupport.parkNanos( 2l ); }
	}

	@Override
	public <T> void providerFor( final Class<T> serviceClazz, final ProducerFactory<T> provider ) {
		producers.memorizeProviderForClazz( provider, serviceClazz );
	}

	@Override
	public <T> void providerFor( final Class<T> serviceClazz, final T object ) {
		providerFor( serviceClazz, new SingleObjectIterable<T>( object ) );
	}

	protected <T> void providerFor( final Class<T> serviceClazz, final Iterable<T> iterable ) {
		synchronized ( dependencies ) {
			dependencies.put( serviceClazz, iterable );
			dependencies.unlock( serviceClazz );
		}
	}

	@Override
	public <T> void provideOn( final Iterable<T> iterable ) {
		withInjector( i->i.loadDependenciesAndInjectInto( iterable ) );
	}

	@Override
	public void provideOn( final Object object ) {
		withInjector( i->i.loadDependenciesAndInjectInto( object ) );
	}

	private <T> void withInjector( Consumer<DependencyInjector> callback ) {
		final DependencyInjector injector = new DependencyInjector();
		callback.accept( injector );
		injector.flush();
	}

	public <T> ProducerFactory<T> getProviderFor( final Class<T> serviceClazz, final Condition<T> condition ) {
		return fromInjector( i -> i.getProviderFor( serviceClazz, condition ) );
	}

	private <T> T fromInjector( Function<DependencyInjector, T> callback ) {
		final DependencyInjector injector = new DependencyInjector();
		final T t = callback.apply( injector );
		injector.flush();
		return t;
	}

	/**
	 * A dependency injection context. This class was designed to allow
	 * reentrant injection to deal with recursive dependencies.
	 */
	final public class DependencyInjector {

		final Queue<Injectable> classesToBeConstructed = new ArrayDeque<>();
		final Queue<InjectableField> fieldToTryToInjectAgainLater = new ArrayDeque<>();

		public <T> T load( final Class<T> serviceClazz, Condition<T> condition, ProviderContext providerContext ) {
			final T produced = produceFromFactory( serviceClazz, condition, providerContext );
			if ( produced != null )
				return produced;
			return first( loadAll( serviceClazz, condition ), condition );
		}

		private <T> T produceFromFactory( final Class<T> serviceClazz, final Condition<T> condition, final ProviderContext context )
		{
			final ProducerFactory<T> provider = getProviderFor( serviceClazz, condition );
			if ( provider != null )
				return provider.provide( context );
			return null;
		}

		public <T> ProducerFactory<T> getProviderFor( final Class<T> serviceClazz, final Condition<T> condition ) {
			if ( producers == null )
				return null;
			return (ProducerFactory<T>)producers.get( serviceClazz, this, condition );
		}

		public <T> Iterable<T> loadAll( final Class<T> serviceClazz, Condition<T> condition ) {
			return filter( loadAll( serviceClazz ), condition );
		}

		public <T> Iterable<T> loadAll( final Class<T> serviceClazz ) {
			Iterable<?> instances = dependencies.get( serviceClazz );
			if ( instances == null )
				synchronized ( dependencies ) {
					instances = dependencies.get( serviceClazz );
					if ( instances == null )
						instances = loadServicesFor( serviceClazz );
				}
			return (Iterable<T>)instances;
		}

		private <T> Iterable<T> loadServicesFor( final Class<T> serviceClazz ) {
			final List<Class<T>> iterableInterfaces = implementedClasses.loadClassesImplementing( serviceClazz );
			Iterable<T> instances = null;
			if ( !iterableInterfaces.isEmpty() ) {
				instances = singletonContext.instantiate( iterableInterfaces );
				dependencies.put( serviceClazz, instances );
			} else {
				final T instance = singletonContext.instantiate( serviceClazz );
				instances = instance == null ? EmptyIterable.instance() : new SingleObjectIterable<>( instance );
			}
			loadDependenciesAndInjectInto( instances );
			dependencies.unlock( serviceClazz );
			return instances;
		}

		public void loadDependenciesAndInjectInto( Iterable<?> objs ) {
			for ( final Object obj : objs )
				loadDependenciesAndInjectInto( obj );
		}

		public void loadDependenciesAndInjectInto( Object obj ) {
			final ProvidableClass<?> providableClass = singletonContext.retrieveProvidableClass( obj.getClass() );
			tryInjectFields( obj, providableClass );
			tryPostConstructClass( obj, providableClass );
		}

		private void tryInjectFields( Object obj, final ProvidableClass<?> providableClass ) {
			for ( final ProvidableField field : providableClass.fields() )
				try {
					field.provide( obj, this );
				} catch ( final Throwable e ) {
					fieldToTryToInjectAgainLater.add( new InjectableField( field, obj ) );
				}
		}

		private void tryPostConstructClass( Object obj, final ProvidableClass<?> providableClass ) {
			final Injectable injectable = new Injectable( providableClass, obj );
			try {
				injectable.postConstruct();
			} catch ( final Throwable cause ) {
				classesToBeConstructed.add( injectable );
			}
		}

		public void flush() {
			int availableRetries = 5;
			while ( availableRetries > 0 && isNotClean() ) {
				tryPostConstructClasses();
				tryInjectFailedFields();
				availableRetries--;
			}

			injectFailedFields();
			postConstructClasses();
		}

		private boolean isNotClean() {
			return !fieldToTryToInjectAgainLater.isEmpty() || !classesToBeConstructed.isEmpty();
		}

		private void tryPostConstructClasses() {
			final List<Injectable> failed = new ArrayList<>();

			while ( !classesToBeConstructed.isEmpty() ) {
				final Injectable injectable = classesToBeConstructed.poll();
				try { injectable.postConstruct(); } 
				catch ( final Throwable cause ) { failed.add( injectable ); }
			}

			classesToBeConstructed.addAll( failed );
		}

		private void postConstructClasses() {
			while ( !classesToBeConstructed.isEmpty() ) {
				final Injectable injectable = classesToBeConstructed.poll();
				try { injectable.postConstruct(); }
				catch ( final Throwable cause ) { cause.printStackTrace(); }
			}
		}

		private void tryInjectFailedFields() {
			final List<InjectableField> failed = new ArrayList<>();
			while ( !fieldToTryToInjectAgainLater.isEmpty() ) {
				final InjectableField field = fieldToTryToInjectAgainLater.poll();
				try { field.structure.provide( field.instance, this ); }
				catch ( final Throwable e ) { failed.add( field ); }
			}

			fieldToTryToInjectAgainLater.addAll( failed );
		}

		private void injectFailedFields() {
			while ( !fieldToTryToInjectAgainLater.isEmpty() ) {
				final InjectableField field = fieldToTryToInjectAgainLater.poll();
				try { field.structure.provide( field.instance, this ); }
				catch ( final Throwable e ) { handleFieldInjectionError( field, e ); }
			}
		}

		private void handleFieldInjectionError( final InjectableField field, final Throwable e ) {
			System.err.println( "Failed to provide data on " + field.structure + ":" + e.getMessage() );
			e.printStackTrace();
		}
	}

	@RequiredArgsConstructor
	class Injectable {

		final ProvidableClass<?> structure;
		final Object instance;

		public void postConstruct() {
			structure.postConstructor().accept( instance );
		}
	}
}

@RequiredArgsConstructor
class InjectableField {
	final ProvidableField structure;
	final Object instance;
}

class TemporarilyUnavailableException extends RuntimeException {

	private static final long serialVersionUID = -1692242949403885175L;
}
