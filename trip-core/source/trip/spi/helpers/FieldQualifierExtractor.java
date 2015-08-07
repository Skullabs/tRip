package trip.spi.helpers;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public interface FieldQualifierExtractor {

	default List<Class<? extends Annotation>> extractQualifiersFrom(Field field) {
		final List<Class<? extends Annotation>> anns = new ArrayList<>();
		for ( final Annotation ann : field.getAnnotations() )
			if ( isAnnotatedWithQualifierAnnotation( ann.annotationType() ) )
				anns.add( ann.annotationType() );
		return anns;
	}

	boolean isAnnotatedWithQualifierAnnotation(Class<? extends Annotation> ann);

	boolean isAnnotatedWithSingletonAnnotation(Class<?> clazz);

	boolean isASingleElementProvider( Field field );

	boolean isAManyElementsProvider( Field field );
}
