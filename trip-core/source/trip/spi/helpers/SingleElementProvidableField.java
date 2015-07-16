package trip.spi.helpers;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Collection;

import lombok.Value;
import lombok.extern.java.Log;
import trip.spi.Provided;
import trip.spi.ProviderContext;
import trip.spi.ServiceProvider;
import trip.spi.ServiceProviderException;
import trip.spi.helpers.filter.ChainedCondition;
import trip.spi.helpers.filter.Condition;
import trip.spi.helpers.filter.IsAssignableFrom;
import trip.spi.helpers.filter.QualifierCondition;

@Log
@Value
@SuppressWarnings( { "unchecked", "rawtypes" } )
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

	public static <T> ProvidableField from( Collection<Class<? extends Annotation>> qualifiers, final Field field ) {
		field.setAccessible( true );
		final Provided provided = field.getAnnotation( Provided.class );
		final Class expectedClass = provided == null || provided.exposedAs().equals( Provided.class )
			? field.getType() : provided.exposedAs();
		return new SingleElementProvidableField<T>(
			field, (Class<T>)expectedClass,
				createInjectionCondition( qualifiers, field),
				new FieldProviderContext( qualifiers, field ) );
	}

	private static <T> Condition<T> createInjectionCondition(Collection<Class<? extends Annotation>> qualifiers, final Field field) {
		final ChainedCondition<T> condition = new ChainedCondition<>();
		condition.add((Condition<T>)new IsAssignableFrom( field.getType() ));
		condition.add((Condition<T>)new QualifierCondition<>(qualifiers));
		return condition;
	}
}
