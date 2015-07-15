package blah.tests;

import trip.spi.Producer;

public class TestProviders {

	@Producer
	@DateFormat
	public String createDateFormat() {
		return "yyyyMMddHHmmss";
	}
}
