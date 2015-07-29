package trip.spi.tests.concurrency;

import trip.spi.Singleton;

@Singleton
public class Printer {

	public void print( final String msg ) {
		//System.out.println( msg );
	}
}
