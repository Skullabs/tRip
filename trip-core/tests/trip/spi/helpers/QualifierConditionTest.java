package trip.spi.helpers;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Test;

import trip.spi.Foo;
import trip.spi.PrintableFoo;
import trip.spi.PrintableWorld;
import trip.spi.helpers.filter.Condition;
import trip.spi.helpers.filter.QualifierCondition;

public class QualifierConditionTest {

	@Test
	public void ensureThatCanRetrieveOnlyQualifiedElements(){
		final Condition<Object> condition = new QualifierCondition<Object>(Arrays.asList(Foo.class));
		assertFalse( condition.check( new PrintableWorld() ) );
		assertTrue( condition.check(new PrintableFoo()) );
	}
}
