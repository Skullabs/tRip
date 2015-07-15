package blah.tests;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import trip.spi.Provided;
import trip.spi.Singleton;

@Singleton( exposedAs = Converter.class )
public class DateConverter implements Converter<Date> {

	@Provided
	@DateFormat
	String pattern;

	@Override
	public Date convert( String string ) throws ConverterException {
		try {
			final SimpleDateFormat formatter = new SimpleDateFormat( this.pattern );
			return formatter.parse( string );
		} catch ( final ParseException cause ) {
			throw new ConverterException( cause );
		}
	}

}
