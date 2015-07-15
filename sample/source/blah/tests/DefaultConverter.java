package blah.tests;

import lombok.val;
import lombok.experimental.Delegate;
import lombok.experimental.ExtensionMethod;
import trip.spi.DefaultServiceProvider;
import trip.spi.ServiceProviderException;

@SuppressWarnings( "unchecked" )
@ExtensionMethod( Commons.class )
public class DefaultConverter<T> {

	@Delegate
	Converter<T> converter;

	public DefaultConverter( final Class<T> targetClass ) throws ServiceProviderException {
		this.converter = extractDefaultConverterFor( targetClass );
	}

	private Converter<T> extractDefaultConverterFor( final Class<T> targetClass ) throws ServiceProviderException {
		val matcher = new GenericTypeMatcher<T>( targetClass );
		return new DefaultServiceProvider().load( Converter.class, matcher );
	}
}
