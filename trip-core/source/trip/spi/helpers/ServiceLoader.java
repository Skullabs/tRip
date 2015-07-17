package trip.spi.helpers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class ServiceLoader {

	public static <T> List<Class<T>> loadImplementationsFor( Class<T> clazz ) {
		final ClassLoader cl = Thread.currentThread().getContextClassLoader();
		final Iterator<Class<T>> reader = new LazyClassReader<T>( clazz, cl );
		return readAndConvertToList( reader );
	}

	public static <T> List<Class<T>> loadImplementationsFor( String clazz ) {
		final ClassLoader cl = Thread.currentThread().getContextClassLoader();
		final Iterator<Class<T>> reader = new LazyClassReader<T>( clazz, cl );
		return readAndConvertToList( reader );
	}

	private static <T> List<Class<T>> readAndConvertToList( final Iterator<Class<T>> reader ) {
		final List<Class<T>> list = new ArrayList<>();
		while ( reader.hasNext() )
			try {
				list.add( reader.next() );
			} catch ( IllegalStateException cause ) {
				continue;
			}
		return list;
	}
}
