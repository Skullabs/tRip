package trip.spi.inject;

public class NameTransformations {

	public static String stripGenericsFrom( String name ) {
		return name.replaceAll("<[^>]*>", "");
	}
}
