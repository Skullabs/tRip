package trip.spi;

import java.lang.annotation.Annotation;

/**
 * Object holding data about and provided object. It is useful when producing
 * object though producer API, allowing to create a new object based on specific
 * context.
 */
public interface ProviderContext {

	/**
	 * The list of annotations present on the target.
	 * 
	 * @return
	 */
	<A extends Annotation> A getAnnotation( Class<A> anntationClass );

	/**
	 * The type is expected to generate an object.
	 * 
	 * @return
	 */
	Class<?> targetType();

	/**
	 * Retrieve an attribute ( identified by {@code key} ), from current
	 * context. Returns {@code null} if no object associated to the provided
	 * {@code key} was found.
	 * 
	 * @param key
	 * @return
	 */
	Object attribute( String key );

	/**
	 * Retrieve an attribute ( identified by {@code key} ), from current
	 * context. Returns {@code null} if no object associated to the provided
	 * {@code key} was found.
	 * 
	 * @param key
	 * @return
	 */
	<T> T attribute( Class<T> key );
}
