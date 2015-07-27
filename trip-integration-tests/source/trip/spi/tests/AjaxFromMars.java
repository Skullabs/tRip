package trip.spi.tests;

import trip.spi.Provided;
import trip.spi.Singleton;
import trip.spi.tests.ann.Ajax;

@Ajax
@Singleton( exposedAs = Hero.class )
public class AjaxFromMars implements Hero, World {

	@Provided
	World world;

	@Override
	public String getWorld() {
		return this.world.getWorld();
	}
}
