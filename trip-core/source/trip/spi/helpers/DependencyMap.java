package trip.spi.helpers;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DependencyMap {

	final Set<Class<?>> lockedDependencies = new HashSet<>();
	final Map<Class<?>, Iterable<?>> dependencies;

	public Iterable<?> get( Class<?> clazz ) {
		if ( lockedDependencies.contains( clazz ) )
			throw new TemporarilyLockedException();
		return dependencies.get( clazz );
	}

	public void put( Class<?> clazz, Iterable<?> instances ) {
		lockedDependencies.add( clazz );
		dependencies.put( clazz, instances );
	}

	public void unlock( Class<?> clazz ) {
		lockedDependencies.remove( clazz );
	}

	public static class TemporarilyLockedException
			extends RuntimeException {

		private static final long serialVersionUID = -16545564456L;
	}
}
