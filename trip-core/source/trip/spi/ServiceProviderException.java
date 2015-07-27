package trip.spi;

public class ServiceProviderException extends RuntimeException {

	private static final long serialVersionUID = -4728985132376711824L;

	public ServiceProviderException( String message ) {
		super( message );
	}

	public ServiceProviderException( Throwable cause ) {
		super( cause );
	}
}
