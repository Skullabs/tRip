package trip.spi.helpers;

import java.lang.reflect.Field;

import lombok.Value;
import lombok.extern.java.Log;
import trip.spi.Provided;
import trip.spi.ProviderContext;
import trip.spi.ServiceProvider;
import trip.spi.ServiceProviderException;
import trip.spi.helpers.filter.Condition;
import trip.spi.helpers.filter.IsAssignableFrom;

@Log
@Value
public class SingleElementProvidableField<T> implements ProvidableField {

	final Field field;
	final Class<T> fieldType;
	final Condition<T> condition;
	final ProviderContext providerContext;

	@Override
	public void provide( final Object instance, final ServiceProvider provider )
		throws ServiceProviderException, IllegalArgumentException, IllegalAccessException {
		final Object value = provider.load( fieldType, condition, providerContext );
		if ( value == null )
			log.warning( "No data found for " + fieldType.getCanonicalName() );
		set( instance, value );
	}

	public void set( final Object instance, final Object value ) throws IllegalArgumentException, IllegalAccessException {
		field.set( instance, value );
	}

	@SuppressWarnings( { "unchecked", "rawtypes" } )
	public static <T> ProvidableField from( final Field field ) {
		field.setAccessible( true );
		final Provided provided = field.getAnnotation( Provided.class );
		final Class expectedClass = provided.exposedAs().equals( Provided.class )
			? field.getType() : provided.exposedAs();
		return new SingleElementProvidableField<T>(
			field, (Class<T>)expectedClass,
				(Condition<T>)new IsAssignableFrom( field.getType() ),
				new FieldProviderContext( field ) );
	}
}
