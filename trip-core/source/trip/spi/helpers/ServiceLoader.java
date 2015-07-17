package trip.spi.helpers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class ServiceLoader {

	public static <T> List<Class<T>> loadImplementationsFor( Class<T> clazz ) {
		final ClassLoader cl = Thread.currentThread().getContextClassLoader();
		final Iterator<Class<T>> reader = new LazyClassReader<T>( clazz, cl );
		final List<Class<T>> list = new ArrayList<>();
		while ( reader.hasNext() )
			list.add( reader.next() );
		return list;
	}
}
