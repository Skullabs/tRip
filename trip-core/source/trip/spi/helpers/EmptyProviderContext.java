package trip.spi.helpers;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Collections;

import trip.spi.ProviderContext;

public class EmptyProviderContext implements ProviderContext {

	public final static ProviderContext INSTANCE = new EmptyProviderContext();

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

	@Override
	public Collection<Class<? extends Annotation>> qualifierAnnotations() {
		return Collections.emptyList();
	}
}
