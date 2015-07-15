package trip.spi.tests;

import trip.spi.Producer;
import trip.spi.tests.ann.Foo;

public class HelloWorldProvider {

	@Producer
	public HelloWorld createHelloWorld() {
		return new HelloWorld();
	}

	@Producer
	@Foo
	public HelloWorld createHelloFooo() {
		return new HelloFoo();
	}
}
