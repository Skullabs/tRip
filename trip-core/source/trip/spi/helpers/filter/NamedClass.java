package trip.spi.helpers.filter;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class NamedClass<T> implements Condition<Class<T>> {

	final String name;

	@Override
	public boolean check(Class<T> clazz) {
		return NameExtractor.doesClassAnnotationsMatchesTheName( clazz, name );
	}
}
