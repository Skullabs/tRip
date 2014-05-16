tRip
====

Renewed Interface Provider

Java lacks a way to inject objects into a class that have been provided as Service Provider Interface. tRip intent to help developers to create an already populated object from API implementations. Note that this not intent to provide a new Dependency Injection plataform, but an easy to use way to have loose coupling on your source code design. Please see more about SPI [here](http://en.wikipedia.org/wiki/Service_provider_interface).

# Show me the code

Please consider the following StringGenerator interface. It intent to provide a simple way to generate Strings.
```java
public interface StringGenerator {

  String generate();
}
```

dsuduhashudhuasdhuashudusaduha

Also, consider the following implementations of ```StringGenerator```. The following class should be provided as defined in ```java.util.ServiceLoader``` documentation ( available [here](http://docs.oracle.com/javase/8/docs/api/java/util/ServiceLoader.html) ).
```java
public class HelloWorldGenerator implements StringGenerator {

  public String generate() {
    return "Hello World";
  }
}

```

The following ```Runnable``` will print twice generated data from injected ```StringGenerator```.
```java
@Log
public class TwicePrinterRunnable implements Runnable {

  // Will be provided soon
  @Provided StringGenerator generator;
  
  @Override
  public void run(){
    log.info( generator.generate() );
    log.info( generator.generate() );
  }
}
```

The bellow unit test is a simple way to see this example running.

```java
public class SampleTest {

  // Creating the Provider object
  final Provider provider = new Provider();

  @Test
  public void seeItWorking(){
    Runnable runnable = new TwicePrinterRunnable();
    provider.provideOn( runnable );
    runnable.run();
  }

  @Test
  public void seeItWorkingIfTwicePrinterRunnableWasConfiguredAsRunnableProvider(){
    Runnable runnable = provider.load( Runnable.class );
    runnable.run();
  }
}
```

### Multi-Level Service Providing
As a tiny implementation the above example doesn't show that is also possible to _"inject"_ objects in a multi-level way. It means that, if the provided ```HelloWorldGenerator``` sample code depended ( in a hipotesis ) on a second interface implementation, and there was available a service implementation in the class path, so it would be _"provided"_ as well.

### Configuring project for maven
Configuring a maven project is easy. Just include the following repository and dependency configuration on its respecitve places on your pom.xml.

```xml
...
  <repository>
		<id>texo-release-repository</id>
		<name>Texo Release Repository</name>
		<url>http://repository.texoit.com:8081/content/repositories/PublicRelease/</url>
	</repository>
...
  <dependency>
  	<groupId>com.texoit.trip</groupId>
	  <version>0.1</version>
	  <artifactId>trip-core</artifactId>
  </dependency>
...
```

### License

rTip is Apache 2.0 licensed.
