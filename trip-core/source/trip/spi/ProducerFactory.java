package trip.spi;

public interface ProducerFactory<T> {

	T provide( ProviderContext context ) throws ServiceProviderException;

}
