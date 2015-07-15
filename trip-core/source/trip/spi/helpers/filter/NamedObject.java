package trip.spi.helpers.filter;

import lombok.RequiredArgsConstructor;
import lombok.val;

@RequiredArgsConstructor
public class NamedObject<T> implements Condition<T> {

	final String name;

	@Override
	public boolean check( T object ) {
		val clazz = object.getClass();
		return NameExtractor.doesClassAnnotationsMatchesTheName( clazz, name );
	}
}
