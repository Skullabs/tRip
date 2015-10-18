package trip.spi.recursive;

import trip.spi.Provided;

public class UserService {

	final User user = new User();

	@Provided
	ProfileService profileService;
}
