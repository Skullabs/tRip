package trip.spi.recursive;

import trip.spi.ProducerFactory;
import trip.spi.Provided;
import trip.spi.ProviderContext;
import trip.spi.ServiceProviderException;

public class UserProducer implements ProducerFactory<User> {

	@Provided
	UserService userService;

	@Override
	public User provide( ProviderContext context ) throws ServiceProviderException {
		return userService.user;
	}
}
