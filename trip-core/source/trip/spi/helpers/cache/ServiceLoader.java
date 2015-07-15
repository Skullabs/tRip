package trip.spi.helpers.cache;

import java.util.Iterator;

public abstract class ServiceLoader {

	public static <T> CachedIterable<Class<T>> loadImplementationsFor( Class<T> clazz ) {
        final ClassLoader cl = Thread.currentThread().getContextClassLoader();
        final Iterator<Class<T>> reader = new LazyClassReader<T>(clazz, cl);
        return new CachedIterable<Class<T>>(reader);
	}

	public static <T> CachedIterable<Class<T>> loadImplementationsFor( String interfaceCanonicalName ) {
		final ClassLoader cl = Thread.currentThread().getContextClassLoader();
		final Iterator<Class<T>> reader = new LazyClassReader<T>( interfaceCanonicalName, cl );
		return new CachedIterable<Class<T>>( reader );
	}
}
