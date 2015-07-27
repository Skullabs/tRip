package trip.spi.inject.stateless;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.junit.Test;

import trip.spi.processor.stateless.ExposedMethod;
import trip.spi.processor.stateless.StatelessClass;
import trip.spi.processor.stateless.StatelessClassGenerator;

public class StatelessClassGeneratorTest {

	@Test
	public void ensureThatGenerateTheExpectedClassFromInterfaceImplementation() throws IOException {
		final StatelessClass statelessClass = createStatelessImplementationOfInterface();
		final StatelessClassGenerator generator = new StatelessClassGenerator();
		final StringWriter writer = new StringWriter();
		generator.write( statelessClass, writer );
		final String expected = readFile( "tests/stateless-class-exposing-interface.txt" );
		assertEquals( expected, writer.toString() );
	}

	@Test
	public void ensureThatGenerateTheExpectedClassFromExposedServiceByItSelf() throws IOException {
		final StatelessClass statelessClass = createStatelessImplementationOfClass();
		final StatelessClassGenerator generator = new StatelessClassGenerator();
		final StringWriter writer = new StringWriter();
		generator.write( statelessClass, writer );
		final String expected = readFile( "tests/stateless-class-exposing-class.txt" );
		assertEquals( expected, writer.toString() );
	}

	StatelessClass createStatelessImplementationOfInterface() {
		return new StatelessClass(
			"important.api.Interface", "sample.project.ServiceFromInterface", false,
			list( voidMethod(), returnableMethod() ),
			list( returnableMethod() ),
			list( voidMethod() ) );
	}

	StatelessClass createStatelessImplementationOfClass() {
		return new StatelessClass(
			"sample.project.ServiceFromInterface",
			"sample.project.ServiceFromInterface", true,
			list( voidMethod(), returnableMethod() ),
			list( returnableMethod() ),
			list( voidMethod() ) );
	}

	ExposedMethod returnableMethod() {
		return new ExposedMethod( "sum", "Long", list( "Double", "Integer" ) );
	}

	ExposedMethod voidMethod() {
		return new ExposedMethod( "voidMethod", "void", emptyStringList() );
	}

	@SuppressWarnings("unchecked")
	<T> List<T> list( final T... ts ) {
		final List<T> list = new ArrayList<T>();
		for ( final T t : ts )
			list.add( t );
		return list;
	}

	List<String> emptyStringList() {
		return new ArrayList<String>();
	}

	String readFile( final String name ) throws IOException {
		final Scanner scanner = new Scanner( new File( name ) );
		try {
			return scanner.useDelimiter( "\\Z" ).next();
		} finally {
			scanner.close();
		}
	}
}
