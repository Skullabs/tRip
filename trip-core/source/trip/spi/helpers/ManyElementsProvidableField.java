package trip.spi.helpers;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Collection;

import lombok.RequiredArgsConstructor;
import lombok.val;
import trip.spi.DefaultServiceProvider.DependencyInjector;
import trip.spi.ProvidedServices;
import trip.spi.helpers.filter.Condition;
import trip.spi.helpers.filter.QualifierCondition;

@RequiredArgsConstructor
@SuppressWarnings( { "unchecked" } )
public class ManyElementsProvidableField<T> implements ProvidableField {

	final Field field;
	final Class<T> fieldType;
	final Condition<T> condition;

	@Override
	public void provide( Object instance, DependencyInjector provider ) throws Throwable {
		final Object value = provider.loadAll( fieldType, condition );
		set( instance, value );
	}

	public void set( final Object instance, final Object value ) throws IllegalArgumentException, IllegalAccessException {
		field.set( instance, value );
	}

	public static <T> ProvidableField from( Collection<Class<? extends Annotation>> qualifiers, final Field field ) {
		assertFieldTypeIsIterable( field );
		field.setAccessible( true );
		val provided = field.getAnnotation( ProvidedServices.class );
		return new ManyElementsProvidableField<T>(
			field, (Class<T>)provided.exposedAs(),
			(Condition<T>)new QualifierCondition<>(qualifiers) );
	}

	private static void assertFieldTypeIsIterable( final Field field ) {
		if ( !Iterable.class.equals( field.getType() ) )
			throw new IllegalStateException( "Field " + field.getName() + " expects to have Iterable type." );
	}
}
