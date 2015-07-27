package trip.jsr.cdi;

import javax.inject.Provider;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SingleElementProvider<T> implements Provider<T> {

	final T t;

	@Override
	public T get() {
		return t;
	}
}
