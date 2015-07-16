package trip.jsr.cdi;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import javax.inject.Inject;
import javax.inject.Qualifier;

import trip.spi.helpers.FieldQualifierExtractor;

public class CDIFieldQualifierExtractor implements FieldQualifierExtractor {

	@Override
	public boolean isAnnotatedWithQualifierAnnotation(Class<? extends Annotation> ann) {
		return ann.isAnnotationPresent( Qualifier.class );
	}

	@Override
	public boolean isASingleElementProvider( Field field ) {
		return field.isAnnotationPresent( Inject.class );
	}

	@Override
	public boolean isAManyElementsProvider( Field field ) {
		return false;
	}
}
