package trip.spi.recursive;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

import org.junit.Before;
import org.junit.Test;

import trip.spi.DefaultServiceProvider;
import trip.spi.Provided;

public class RecursiveInjectionBehaviorTest {

	@Provided
	ProfileService profileService;

	@Provided
	UserCategoryService userCategoryService;

	@Provided
	UserService userService;

	@Provided
	User user;

	@Test( timeout = 1000l )
	public void ensureThatCanInjectServicesThatAreMutuallyDependent() {
		assertNotNull( profileService );
		assertNotNull( userCategoryService );
		assertNotNull( userService );
	}

	@Test( timeout = 1000l )
	public void ensureThatServicesHaveItsDependenciesResolved() {
		assertSame( profileService.userCategoryService, userCategoryService );
		assertSame( userService.profileService, profileService );
		assertSame( userCategoryService.userService, userService );
	}

	@Test( timeout = 1000l )
	public void ensureThatInstancesProducedWithRecursiveDependenciesAreNotNull() {
		assertNotNull( user );
	}

	@Before
	public void provideDependencies() {
		new DefaultServiceProvider().provideOn( this );
	}
}
