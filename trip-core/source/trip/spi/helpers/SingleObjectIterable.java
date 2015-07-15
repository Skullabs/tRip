package trip.spi.helpers;

import java.util.Iterator;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SingleObjectIterable<T> implements Iterable<T> {

	final T object;

	@Override
	public Iterator<T> iterator() {
		return new SingleObjectIterator( object );
	}

	@RequiredArgsConstructor
	public class SingleObjectIterator implements Iterator<T> {

		final T object;
		boolean firstCall = true;

		@Override
		public boolean hasNext() {
			boolean hasNext = firstCall;
			if ( firstCall )
				firstCall = false;
			return hasNext;
		}

		@Override
		public T next() {
			return object;
		}

		@Override
		public void remove() {
		}
	}
}
