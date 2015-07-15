package trip.spi.helpers;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import lombok.RequiredArgsConstructor;
import trip.spi.GeneratedFromStatelessService;
import trip.spi.Provided;
import trip.spi.ProvidedServices;
import trip.spi.ServiceProvider;
import trip.spi.ServiceProviderException;

@RequiredArgsConstructor
public class ProvidableClass<T> {

	final Class<T> targetClazz;
	final Iterable<ProvidableField> fields;

	public void provide( Object instance, ServiceProvider provider )
			throws ServiceProviderException, IllegalArgumentException, IllegalAccessException {
		for ( ProvidableField field : fields )
			field.provide( instance, provider );
	}

	public static <T> ProvidableClass<T> wrap( Class<T> targetClazz ) {
		return new ProvidableClass<T>( targetClazz, readClassProvidableFields( targetClazz ) );
	}

	static Iterable<ProvidableField> readClassProvidableFields( Class<?> targetClazz ) {
		List<ProvidableField> providableFields = new ArrayList<ProvidableField>();
		Class<? extends Object> clazz = targetClazz;
		while ( !Object.class.equals( clazz ) ) {
			populateWithProvidableFields( clazz, providableFields );
			if ( clazz.isAnnotationPresent(GeneratedFromStatelessService.class) )
				break;
			clazz = clazz.getSuperclass();
		}
		return providableFields;
	}

	static void populateWithProvidableFields( Class<?> targetClazz, List<ProvidableField> providableFields ) {
		for ( Field field : targetClazz.getDeclaredFields() )
			if ( field.isAnnotationPresent( Provided.class ) )
				providableFields.add( SingleElementProvidableField.from( field ) );
			else if ( field.isAnnotationPresent( ProvidedServices.class ) )
				providableFields.add( ManyElementsProvidableField.from( field ) );
	}
}
