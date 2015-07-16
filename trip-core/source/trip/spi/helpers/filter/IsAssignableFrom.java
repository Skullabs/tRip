package trip.spi.helpers.filter;

import lombok.RequiredArgsConstructor;
import trip.spi.ProducerFactory;

@RequiredArgsConstructor
public class IsAssignableFrom implements Condition<Object> {

	final Class<?> expectedClass;

	@Override
	public boolean check( Object object ) {
		return object == null
			|| expectedClass.isAssignableFrom( object.getClass() )
			|| ProducerFactory.class.isInstance( object );
	}

	@Override
	public String toString() {
		return "IsAssignableFrom(" + expectedClass.getCanonicalName() + ")";
	}
}
