package trip.spi.tests;

import trip.spi.Provided;
import trip.spi.Singleton;

@Singleton( exposedAs = Hero.class, name = "batman" )
public class Batman implements Hero, World {

	@Provided( exposedAs = World.class )
	Mars mars;

	@Override
	public String getWorld() {
		return mars.getWorld();
	}
}
