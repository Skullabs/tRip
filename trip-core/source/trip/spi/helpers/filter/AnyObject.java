package trip.spi.helpers.filter;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class AnyObject<T> implements Condition<T> {

	@Override
	public boolean check(Object object) {
		return true;
	}
}
