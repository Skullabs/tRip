package trip.spi;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import lombok.SneakyThrows;
import lombok.val;

import org.junit.Test;

public class InheritedStatelessServiceTest {

	@Test
	@SneakyThrows
	public void ensureThatStatelessServiceDoesntHaveSuperClassDependenciesProvided() {
		val provider = new DefaultServiceProvider();
		val fakeService = (MyFakeStatelessService)provider.load( Readable.class );
		val superclassProvidedObject = fakeService.getWord();
		assertNull( superclassProvidedObject );
	}

	@Test
	@SneakyThrows
	public void ensureThatStatelessServiceCouldProvideDataInChildClass() {
		val provider = new DefaultServiceProvider();
		val fakeService = (MyFakeStatelessService)provider.load( Readable.class );
		val childclassProvidedObject = fakeService.getPrintable();
		assertNotNull( childclassProvidedObject );
	}
}