package trip.spi.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.lang.annotation.Annotation;
import java.util.Arrays;

import org.junit.Test;

import trip.spi.Producer;
import trip.spi.ProviderContext;
import trip.spi.DefaultServiceProvider;
import trip.spi.ServiceProviderException;
import trip.spi.helpers.filter.QualifierCondition;
import trip.spi.tests.ann.Ajax;
import trip.spi.tests.ann.DarkKnight;
import trip.spi.tests.ann.Foo;

public class GeneratedCodeAndMetaINFTest {

	final DefaultServiceProvider provider = new DefaultServiceProvider();

	@Test
	public void grantThatGenerateNewHelloFoo() throws ServiceProviderException {
		final HelloWorld helloWorld = this.provider.load( HelloWorld.class, qualifier(Foo.class) );
		assertEquals( "Fooo!!!", helloWorld.toString() );
	}

	@Test
	public void grantThatCouldRetrieveAjaxFromMars() throws ServiceProviderException {
		final Hero hero = this.provider.load( Hero.class, qualifier(Ajax.class) );
		assertNotNull( "No 'Hero' implementations found", hero );
		assertEquals( "Expected 'Hero' should be 'AjaxFromMars' instance",
				hero.getClass(), AjaxFromMars.class );
		final AjaxFromMars ajax = (AjaxFromMars)hero;
		assertEquals( "'ajax' doesn't provide the expected string", "Mars", ajax.getWorld() );
	}

	@Test
	public void grantThatCouldRetrieveBatman() throws ServiceProviderException {
		final Hero hero = this.provider.load( Hero.class, qualifier(DarkKnight.class) );
		assertNotNull( hero );
		assertEquals( Batman.class, hero.getClass() );
		final Batman batman = (Batman)hero;
		assertEquals( "'batman' doesn't provide the expected string", "Mars", batman.getWorld() );
	}

	<T> QualifierCondition<T> qualifier( Class<? extends Annotation> ann ){
		return new QualifierCondition<>(Arrays.asList(ann));
	}

	@Producer
	public String produceAGenericString( final ProviderContext context ) {
		return null;
	}
}
