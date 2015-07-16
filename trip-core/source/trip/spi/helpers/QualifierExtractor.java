package trip.spi.helpers;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class QualifierExtractor {

	final Iterable<FieldQualifierExtractor> extractors;

	public Collection<Class<? extends Annotation>> extractQualifiersFrom( Field field ){
		final Collection<Class<? extends Annotation>> anns = new ArrayList<>();
		for ( final FieldQualifierExtractor extractor : extractors )
			anns.addAll( extractor.extractQualifiersFrom(field) );
		return anns;
	}

	public boolean isAnnotatedWithQualifierAnnotation( Class<? extends Annotation> ann ){
		for ( final FieldQualifierExtractor extractor : extractors )
			if ( extractor.isAnnotatedWithQualifierAnnotation(ann) )
				return true;
		return false;
	}

	public boolean isASingleElementProvider( Field field ) {
		for ( final FieldQualifierExtractor extractor : extractors )
			if ( extractor.isASingleElementProvider( field ) )
				return true;
		return false;
	}

	public boolean isAManyElementsProvider( Field field ) {
		for ( final FieldQualifierExtractor extractor : extractors )
			if ( extractor.isAManyElementsProvider( field ) )
				return true;
		return false;
	}
}
