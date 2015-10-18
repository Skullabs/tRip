package trip.spi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.java.Log;
import trip.spi.helpers.ProvidableClass;
import trip.spi.helpers.QualifierExtractor;

@Log
@RequiredArgsConstructor
public class SingletonContext {

	final Map<Class<?>, Object> cache = new HashMap<>();
	final Map<Class<?>, ProvidableClass<?>> providableClassCache = new HashMap<>();

	@Setter
	QualifierExtractor qualifierExtractor;

	@SuppressWarnings("unchecked")
	public <T> Iterable<T> instantiate( Iterable<Class<T>> classes ){
		final List<T> list = new ArrayList<>();
		synchronized ( cache ) {
			for ( final Class<T> clazz : classes ) {
				T object = (T)cache.get( clazz );
				if ( object == null )
					cache.put( clazz, object = instantiate( clazz ) );
				list.add( object );
			}
		}
		return list;
	}

	public <T> T instantiate( Class<T> clazz ) {
		try {
			return clazz.newInstance();
		} catch ( final IllegalAccessException | InstantiationException cause ) {
			log.finest("Can't instantiate " + clazz + ": " + cause.getMessage());
			return null;
		}
	}

	public ProvidableClass<?> retrieveProvidableClass( final Class<?> targetClazz ) {
		ProvidableClass<?> providableClass = providableClassCache.get( targetClazz );
		if ( providableClass == null )
			synchronized ( providableClassCache ) {
				providableClass = providableClassCache.get( targetClazz );
				if ( providableClass == null ) {
					providableClass = ProvidableClass.wrap( qualifierExtractor, targetClazz );
					providableClassCache.put( targetClazz, providableClass );
				}
			}
		return providableClass;
	}
}