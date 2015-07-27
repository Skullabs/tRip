package trip.spi.helpers.filter;

import lombok.NoArgsConstructor;

@NoArgsConstructor
@SuppressWarnings( { "rawtypes", "unchecked" } )
public class AnyObject<T> implements Condition<T> {

	private static final Condition INSTANCE = new AnyObject();

	public static <T> Condition<T> instance() {
		return INSTANCE;
	}

	@Override
	public boolean check(Object object) {
		return true;
	}

	@Override
	public String toString() {
		return "AnyObject";
	}
}
