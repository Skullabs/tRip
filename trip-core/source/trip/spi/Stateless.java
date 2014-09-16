package trip.spi;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Inherited
@Documented
@Retention( RetentionPolicy.RUNTIME )
@Target( { ElementType.TYPE } )
public @interface Stateless {

	Class<?> exposedAs() default Stateless.class;

	/**
	 * The name that identifies the service.
	 */
	String name() default "";
}
