package trip.spi.tests;

import trip.spi.Provided;
import trip.spi.Singleton;
import trip.spi.tests.ann.DarkKnight;

@DarkKnight
@Singleton( exposedAs = Hero.class )
public class Batman implements Hero, World {

	@Provided( exposedAs = World.class )
	Mars mars;

	@Override
	public String getWorld() {
		return mars.getWorld();
	}
}
