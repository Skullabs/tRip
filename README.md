# tRip - Low coupling module development
tRip intent to be a practical and lightweight tool to provide modularity and low coupling on your source code.

## Main Features
- Designed to help developers to create modular micro-services in Java.
- Compile time discovery of Service Provider Interfaces (SPI)
- Optimized runtime injection of services (discovered SPIs)
- Orthogonal factory pattern (Similar to ```@Produces``` from JSR299)
- CDI (JSR 330) compliant API
- Very low memory footprint
- Lightweight dependency stack (1 JAR -> ~35kb)

### 1. Configuring a Maven Project
Just include the following libraries to your pom.xml.
```xml
<!-- Main tRip dependency -->
<dependency>
	<groupId>io.skullabs.trip</groupId>
	<artifactId>trip-core</artifactId>
	<version>${version.trip}</version>
</dependency>
<!-- The APT that will discovery your SPIs -->
<dependency>
	<groupId>io.skullabs.trip</groupId>
	<artifactId>trip-processor</artifactId>
	<version>${version.trip}</version>
	<scope>provided</scope>
</dependency>
```
If you intent to use CDI (JSR330) annotations, please include the following library too.
```xml
<dependency>
	<groupId>io.skullabs.trip</groupId>
	<artifactId>trip-core</artifactId>
	<version>${version.trip}</version>
	<!-- it could be 'provided' if you have included javax.inject manually -->
	<!-- scope>provided</scope -->
</dependency>
```

### 2. Providing Services
Bellow some examples how you can make a class available to the _tRip Context_.
```java
import trip.spi.*;

public interface Player {}

@Singleton( exposedAs=Player.class )
// will expose Hero as a Player
public class Hero implements Player {}

@Stateless( exposedAs=Player.class )
// will expose BadBoy as a Player, managed as Stateless
public class BadBoy implements Player {}

@Singleton
public class Game {}
```

### 3. Injecting Provided Services
```java
import trip.spi.*;

public interface Player {}
public interface Weapon {}

@Singleton( exposedAs=Weapon.class )
public class Granade implements Weapon {}

@Singleton( exposedAs=Player.class )
public class BadBoy implements Player {

  @Provided Weapon weapon;

}
```
You can inject concrete implementations into your services. It is not required to
expose services as interfaces (or abstract classes).
```java
// exposing itself as a service
@Singleton
public class Menu {}

// injecting the concrete implementation
@Singleton
public class Game {
  @Provided Menu menu;
}
```

#### 3.1 Injecting all implementations of a service
Sometimes it would be nice if was possible to retrieve all implementations of a specific
interface and have them injected into your controller. tRip provides this functionality through
the ```@ProvidedServices``` annotation as illustrated bellow.
```java
import trip.spi.*;

public interface Player {}

@Singleton( exposedAs=Player.class )
// will expose Hero as a Player
public class Hero implements Player {}

@Stateless( exposedAs=Player.class )
// will expose BadBoy as a Player, managed as Stateless
public class BadBoy implements Player {}

@Singleton
public class Game {

  @ProvidedServices( exposedAs=Player.class )
  Iterable<Player> allAvailablePlayers;

}
```

#### 3.2 Disambiguation
Sometimes we have more than one dependency provided with some Interface. It is possible to
create a qualifier annotation (in a JSR330 way), to identify which implementation should
be injected.
```java
import trip.spi.*;

public interface Weapon {}

@Qualifier
@Retention( RUNTIME )
public @interface HiDamage {}

@HiDamage
@Singleton( exposedAs=Weapon.class )
public class Granade implements Weapon {}

@Singleton( exposedAs=Weapon.class )
public class Gun implements Weapon {}

@Singleton( exposedAs=Player.class )
public class BadBoy implements Player {

  @HiDamage
  @Provided
  Weapon weapon;

}

// You can always inject a specific concrete implementation
@Singleton( exposedAs=Player.class )
public class BadBoy implements Player {

  @Provided(exposedAs=Weapon.class)
  Gun gun;

}
```

### 4. Managing (injecting into) classes with tRip Context
You should have at least one managed class to have its dependencies injected. tRip have
a context implementation called ```trip.spi.ServiceProvide```. It controls all injectable data,
are capable to handle singletons and stateless services, and instantiate classes when it is
required as a dependency but it neither is a singleton nor a stateless implementation.
```java
import trip.spi.*;

@Singleton
public class Game { /* ... */ }

public class Main {

  public static void main( String[] args ){
    ServiceProvider provider = new DefaultServiceProvider();
    Game game = provider.load( Game.class );
    game.run();
  }
}
```
The above example code shows how is possible to intialize the ```Game``` class
and have its dependencies injected. The dependencies' dependencies will be recursively injected.
In this case, will be only instance of ```Game``` no matter how many times ```ServiceProvider.load```
is called. If neither ```@Stateless``` nor ```@Singleton``` annotations is present, then a new instance
of Game will be created everytime ```ServiceProvider.load``` is called.

It is also possible to inject data in an already created instance of any class.
```java
  public static void main( String[] args ){
    ServiceProvider provider = new DefaultServiceProvider();
    Game game = new Game();
    provider.provideOn( game );
    game.run();
  }
```

To avoid side effects on your application, there should be only one instance of ServiceProvider on your
project. If you need access to current tRip Context you should inject it on your code.
```java
public class AClassThatUsesServiceProvider {

  @Provided ServiceProvider provider;

}
```

#### 4.1. Stateless Services
Stateless services, as it name suggests, is a services that does not have state. It means that it does not stores data between two or more executions of a public method. Its simple to be handled by developers, once it is thread-safe by default ( except if this service is manually shared between threads ). The bellow source code illustrates how it works.
```java
public class User {}

public class DatabaseAccess {}

@Stateless
public class UserPersistenceService {

  @Provided DatabaseAccess dao;
  
  public void persist( User user ){
    dao.newTransaction();
    dao.persist( user );
    dao.commit();
  }
}

@Singleton
// exposed as REST endpoint by your favorite REST framework
public class MyRESTEndpoint {

  @Provided UserPersistenceService userService;

  @POST @Path( "/api/user" )
  public void persistUser( User user ){
    userService.persist( user );
  }
}
```
At the above sample code, every time the method ```UserPersistenceService.persist``` is called, a new instance of ```UserPersistenceService``` will be created, all its dependencies will be injected again and then the method you called will be executed. It makes easier to handle transactions in a clean way, as the above sample states.

As ```Stateless``` services have a short life cycle, you can use the JDK ```PostConstruct``` and ```PreDestroy``` annotations to be notified, respectively, when the service is constructed and destructed before every call to a public service's method.

#### 4.2. Singleton Services
It is a managed service that appears once in the context. Every consumer will receive the same instance of this service. Is assigned to the developer the responsibility to keep the service consistent and, if is the case, thread-safe. No secret here. Singletons services are memory friendly, but requires a more sophisticated design to avoid problems in a multi-threaded environment like a web server.

Opposed to stateless services, ```Singleton``` services have a long life cycle. Thus tRip only supports the JDK's ```PostConstruct``` annotation. It is called every time a singleton service is called.

### License
tRip is Apache 2.0 licensed.
