package trip.spi.helpers;

import java.lang.annotation.Annotation;

import trip.spi.Qualifier;

public class DefaultFieldQualifierExtractor implements FieldQualifierExtractor {

	@Override
	public boolean isAnnotatedWithQualifierAnnotation(Class<? extends Annotation> ann) {
		return ann.isAnnotationPresent(Qualifier.class);
	}
}
