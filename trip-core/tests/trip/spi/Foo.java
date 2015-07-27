package trip.spi;

import java.lang.annotation.RetentionPolicy;

import java.lang.annotation.Retention;

@Qualifier
@Retention(RetentionPolicy.RUNTIME)
public @interface Foo {

}
