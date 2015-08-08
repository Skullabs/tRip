package trip.spi.helpers;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import trip.spi.Provided;
import trip.spi.ProvidedServices;
import trip.spi.Qualifier;

public class DefaultFieldQualifierExtractor implements FieldQualifierExtractor {

	@Override
	public boolean isAnnotatedWithQualifierAnnotation(Class<? extends Annotation> ann) {
		return ann.isAnnotationPresent( Qualifier.class );
	}

	@Override
	public boolean isASingleElementProvider( Field field ) {
		return field.isAnnotationPresent( Provided.class );
	}

	@Override
	public boolean isAManyElementsProvider( Field field ) {
		return field.isAnnotationPresent( ProvidedServices.class );
	}
}
