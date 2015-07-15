package trip.spi.helpers.cache;

import java.util.Iterator;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ServiceLoader {
	
	public static <T> CachedIterable<Class<T>> loadImplementationsFor( Class<T> clazz ) {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        Iterator<Class<T>> reader = new LazyClassReader<T>(clazz, cl);
        return new CachedIterable<Class<T>>(reader);
	}

	public static <T> CachedIterable<Class<T>> loadImplementationsFor( String interfaceCanonicalName ) {
		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		Iterator<Class<T>> reader = new LazyClassReader<T>( interfaceCanonicalName, cl );
		return new CachedIterable<Class<T>>( reader );
	}

	public static <T> CachedIterable<T> loadFrom( Iterable<Class<T>> interfaces ) {
		Iterator<T> instantor = new LazyClassInstantor<T>(interfaces.iterator());
		return new CachedIterable<T>(instantor);
	}
}
