package trip.spi.helpers.filter;

public interface Condition<T> {
	
	boolean check( T object );
}