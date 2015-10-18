package trip.spi.helpers;

import static trip.spi.helpers.filter.Filter.first;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import trip.spi.DefaultServiceProvider.DependencyInjector;
import trip.spi.ProducerFactory;
import trip.spi.helpers.filter.AnyObject;
import trip.spi.helpers.filter.Condition;

@SuppressWarnings( "rawtypes" )
public class ProducerFactoryMap {

	final Map<Class<?>, List<Class<ProducerFactory>>> producerImplementationClasses = new HashMap<>();
	final Map<Class<?>, List<ProducerFactory<?>>> map = new HashMap<>();

	public static ProducerFactoryMap from( final Iterable<Class<ProducerFactory>> iterable ) {
		final ProducerFactoryMap providers = new ProducerFactoryMap();
		for ( final Class<ProducerFactory> provider : iterable ) {
			final Class<?> clazz = getGenericClassFrom( provider );
			providers.memorizeProviderForClazz(provider, clazz);
		}
		return providers;
	}

	private static Class<?> getGenericClassFrom( Class<? extends ProducerFactory> clazz ) {
		final Type[] types = clazz.getGenericInterfaces();
		for ( final Type type : types )
			if ( ( (ParameterizedType)type ).getRawType().equals( ProducerFactory.class ) )
				return (Class<?>)( (ParameterizedType)type ).getActualTypeArguments()[0];
		return null;
	}

	private void memorizeProviderForClazz( final Class<ProducerFactory> provider, final Class<?> clazz ) {
		List<Class<ProducerFactory>> iterable = producerImplementationClasses.get( clazz );
		if ( iterable == null ) {
			iterable = new ArrayList<>();
			producerImplementationClasses.put( clazz, iterable );
		}
		iterable.add( provider );
	}

	public ProducerFactory<?> get( final Class<?> clazz, DependencyInjector injector, final Condition<?> condition ) {
		final List<ProducerFactory<?>> list = getAll( clazz, injector );

		if ( list == null )
			return null;

		return first(list, condition);
	}

	private List<ProducerFactory<?>> getAll( final Class<?> clazz, DependencyInjector injector ) {
		List<ProducerFactory<?>> list = map.get( clazz );
		if ( list == null )
			synchronized ( map ) {
				list = map.get( clazz );
				if ( list == null ) {
					list = loadAll( clazz, injector );
					map.put( clazz, list );
				}
			}
		return list;
	}

	private List<ProducerFactory<?>> loadAll( Class<?> clazz, DependencyInjector injector ) {
		final List<ProducerFactory<?>> list = new ArrayList<>();
		final List<Class<ProducerFactory>> factories = producerImplementationClasses.get( clazz );
		if ( factories != null )
			for ( final Class<ProducerFactory> factoryClass : factories )
				list.add( injector.load( factoryClass, AnyObject.instance(), EmptyProviderContext.INSTANCE ) );
		return list;
	}

	public void memorizeProviderForClazz( final ProducerFactory<?> provider, final Class<?> clazz ) {
		synchronized ( map ) {
			List<ProducerFactory<?>> list = map.get( clazz );
			if ( list == null ) {
				list = new ArrayList<>();
				map.put( clazz, list );
			}
			list.add( provider );
		}
	}
}
