package trip.spi.helpers;

import static trip.spi.helpers.filter.Filter.first;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.experimental.Delegate;
import lombok.experimental.ExtensionMethod;
import trip.spi.ProducerFactory;
import trip.spi.helpers.filter.Condition;
import trip.spi.helpers.filter.Filter;

@ExtensionMethod( Filter.class )
public class ProducerFactoryMap implements Map<Class<?>, List<ProducerFactory<?>>> {

	@Delegate
	final Map<Class<?>, List<ProducerFactory<?>>> map = new HashMap<Class<?>, List<ProducerFactory<?>>>();

	@SuppressWarnings("rawtypes")
	public static ProducerFactoryMap from( final Iterable<ProducerFactory> iterable ) {
		final ProducerFactoryMap providers = new ProducerFactoryMap();
		for ( final ProducerFactory<?> provider : iterable ) {
			final Class<?> clazz = getGenericClassFrom( provider );
			providers.memorizeProviderForClazz(provider, clazz);
		}
		return providers;
	}

	private static Class<?> getGenericClassFrom( final ProducerFactory<?> provider ) {
		final Type[] types = provider.getClass().getGenericInterfaces();
		for ( final Type type : types )
			if ( ( (ParameterizedType)type ).getRawType().equals( ProducerFactory.class ) )
				return (Class<?>)( (ParameterizedType)type ).getActualTypeArguments()[0];
		return null;
	}

	public void memorizeProviderForClazz( final ProducerFactory<?> provider, final Class<?> clazz ) {
		List<ProducerFactory<?>> iterable = map.get( clazz );
		if ( iterable == null ) {
			iterable = new ArrayList<ProducerFactory<?>>();
			map.put( clazz, iterable );
		}
		iterable.add( provider );
	}

	public ProducerFactory<?> get( final Class<?> clazz, final Condition<?> condition ) {
		final List<ProducerFactory<?>> list = get( clazz );
		if ( list == null )
			return null;
		return first(list, condition);
	}
}
