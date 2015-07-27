package trip.spi.helpers;

import java.util.Iterator;

@SuppressWarnings( { "rawtypes", "unchecked" } )
public class EmptyIterable<T> implements Iterable<T>, Iterator<T> {

	private final static EmptyIterable INSTANCE = new EmptyIterable();

	public static <T> EmptyIterable<T> instance() {
		return INSTANCE;
	}

	@Override
	public boolean hasNext() {
		return false;
	}

	@Override
	public T next() {
		return null;
	}

	@Override
	public Iterator<T> iterator() {
		return this;
	}
}
