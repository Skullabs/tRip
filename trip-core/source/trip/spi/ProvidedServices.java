package trip.spi;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Fields annotated with {@code ProvidedServices} annotation will receive all
 * implementations of interface or superclass defined at {@code exposedAs}
 * parameter.<br>
 * <br>
 * Note that it expects your field type be {@link Iterable}. Otherwise, an
 * exception will be thrown in runtime.
 *
 * @author Miere Teixeira
 */
@Documented
@Retention( RetentionPolicy.RUNTIME )
@Target( ElementType.FIELD )
public @interface ProvidedServices {

	/**
	 * The name that identifies the service.
	 */
	Class<?> exposedAs();
}
