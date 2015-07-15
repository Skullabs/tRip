package trip.spi.helpers;

import java.lang.reflect.Field;

import lombok.RequiredArgsConstructor;
import lombok.val;
import trip.spi.ProvidedServices;
import trip.spi.ServiceProvider;
import trip.spi.ServiceProviderException;
import trip.spi.helpers.filter.AnyObject;
import trip.spi.helpers.filter.Condition;

@RequiredArgsConstructor
public class ManyElementsProvidableField<T> implements ProvidableField {

	final Field field;
	final Class<T> fieldType;
	final Condition<T> condition;

	@Override
	public void provide( final Object instance, final ServiceProvider provider )
		throws ServiceProviderException, IllegalArgumentException, IllegalAccessException {
		final Object value = provider.loadAll( fieldType, condition );
		set( instance, value );
	}

	public void set( final Object instance, final Object value ) throws IllegalArgumentException, IllegalAccessException {
		field.set( instance, value );
	}

	@SuppressWarnings( { "unchecked" } )
	public static <T> ProvidableField from( final Field field ) {
		assertFieldTypeIsIterable( field );
		field.setAccessible( true );
		val provided = field.getAnnotation( ProvidedServices.class );
		return new ManyElementsProvidableField<T>(
			field, (Class<T>)provided.exposedAs(),
			new AnyObject<T>() );
	}

	static void assertFieldTypeIsIterable( final Field field ) {
		if ( !Iterable.class.equals( field.getType() ) )
			throw new IllegalStateException( "Field " + field.getName() + " expects to have Iterable type." );
	}
}
