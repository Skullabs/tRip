package trip.spi.helpers.filter;

import java.lang.annotation.Annotation;
import java.util.Collection;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class QualifierCondition<T> implements Condition<T> {

	final Collection<Class<? extends Annotation>> qualifiers;

	@Override
	public boolean check(T object) {
		final Class<? extends Object> targetClass = object.getClass();
		for ( final Class<? extends Annotation> ann: qualifiers )
			if (!targetClass.isAnnotationPresent(ann))
				return false;
		return true;
	}
}
