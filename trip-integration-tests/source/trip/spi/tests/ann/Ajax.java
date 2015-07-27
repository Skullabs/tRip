package trip.spi.tests.ann;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import trip.spi.Qualifier;

@Qualifier
@Retention(RetentionPolicy.RUNTIME)
public @interface Ajax {
}
