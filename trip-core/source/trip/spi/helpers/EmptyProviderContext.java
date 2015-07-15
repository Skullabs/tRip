package trip.spi.helpers;

import java.lang.annotation.Annotation;

import trip.spi.ProviderContext;

public class EmptyProviderContext implements ProviderContext {

	@Override
	public <A extends Annotation> A getAnnotation( Class<A> anntationClass ) {
		return null;
	}

	@Override
	public Class<?> targetType() {
		return null;
	}

	@Override
	public Object attribute( String key ) {
		return null;
	}

	@Override
	public <T> T attribute( Class<T> key ) {
		return null;
	}
}
