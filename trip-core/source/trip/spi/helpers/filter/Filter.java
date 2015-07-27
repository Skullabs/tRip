package trip.spi.helpers.filter;

import java.util.ArrayList;

import lombok.val;

@SuppressWarnings({"unchecked", "rawtypes"})
public class Filter {

	public static <T> Iterable<T> filter( Iterable<T> self, Condition condition ) {
		val list = new ArrayList<T>();
		for ( T object : self )
			if ( condition.check(object) )
				list.add(object);
		return list;
	}
	
	public static <T> T first( Iterable<T> self, Condition condition ) {
		for ( T object : self )
			if ( condition.check(object) )
				return object;
		return null;
	}
}
