package trip.spi.helpers.cache;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;

@Log
@Getter
@RequiredArgsConstructor
public class LazyClassInstantor<T> implements Iterator<T> {

	final Iterator<Class<T>> reader;
	List<T> cache = new ArrayList<T>();

	@Override
	public boolean hasNext() {
		return reader.hasNext();
	}

	@Override
	public T next() {
		try {
			final Class<T> clazz = reader.next();
			final T instance = clazz.newInstance();
			cache.add(instance);
			return instance;
		} catch ( final IllegalAccessException cause ) {
			log.warning( cause.getMessage() );
			throw new IllegalStateException( cause );
		} catch ( final InstantiationException cause ) {
			log.warning( cause.getMessage() );
			throw new IllegalStateException(cause);
		}
	}

	@Override
	public void remove() {
		reader.remove();
	}
}
