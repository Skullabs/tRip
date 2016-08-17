package blah.tests;

import java.util.List;
import trip.spi.Stateless;

/**
 * Sample class that used to not compile since the issue #41 has been opened.
 */
@Stateless
public class StatelessClassForTest {

	protected <T>Iterable<T> generic(Class<T> clazz, List<T> obj, Integer integer ) {
		return null;
	}
}
