package trip.spi.recursive;

import trip.spi.ProducerFactory;
import trip.spi.Provided;
import trip.spi.ProviderContext;
import trip.spi.ServiceProviderException;

public class UserServiceProducer implements ProducerFactory<UserService> {

	@Provided
	UserService userService;

	@Override
	public UserService provide( ProviderContext context ) throws ServiceProviderException {
		return userService;
	}
}
