package trip.spi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;

@RequiredArgsConstructor
@Log
class SingletonContext {

	final Map<Class<?>, Object> cache = new HashMap<>();

	@SuppressWarnings("unchecked")
	public <T> Iterable<T> instantiate( Iterable<Class<T>> classes ){
		final List<T> list = new ArrayList<>();
		for ( final Class<T> clazz : classes ){
			T object = (T)cache.get(clazz);
			if ( object == null )
				cache.put( clazz, object = instantiate( clazz ) );
			list.add(object);
		}
		return list;
	}

	public <T> T instantiate( Class<T> clazz ) {
		try {
			final T instance = clazz.newInstance();
			return instance;
		} catch ( final IllegalAccessException | InstantiationException cause ) {
			log.warning( cause.getMessage() );
			throw new IllegalStateException( cause );
		}
	}
}