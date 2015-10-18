package trip.spi.recursive;

import trip.spi.Provided;
import trip.spi.Singleton;

@Singleton
public class UserCategoryService {

	@Provided
	UserService userService;
}
