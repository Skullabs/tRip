package trip.spi.inject;

import static trip.spi.inject.NameTransformations.stripGenericsFrom;

import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;

import trip.spi.Producer;
import trip.spi.ProviderContext;
import trip.spi.Stateless;

public class ProducerImplementation {

	final String packageName;
	final String provider;
	final String providerName;
	final String providerMethod;
	final String type;
	final String typeName;
	final String name;
	final boolean expectsContext;
	final String serviceFor;
	final boolean stateless;

	public ProducerImplementation(
			final String packageName, final String provider,
			final String providedMethod, final String type,
			final String typeName, final String name,
			final boolean expectsContext,
			final String serviceFor, final boolean stateless ) {
		this.packageName = stripGenericsFrom( packageName );
		this.provider = stripGenericsFrom( provider );
		this.providerMethod = stripGenericsFrom( providedMethod );
		this.type = stripGenericsFrom( type );
		this.typeName = stripGenericsFrom( typeName );
		this.name = name;
		this.expectsContext = expectsContext;
		this.serviceFor = serviceFor;
		this.providerName = String.valueOf( createIdentifier() );
		this.stateless = stateless;
	}

	private long createIdentifier() {
		final int hashCode =
				String.format( "%s%s%s%s%s%s%s%s",
						packageName, provider, providerMethod,
						type, typeName, name, expectsContext, stateless )
						.hashCode();

		return hashCode & 0xffffffffl;
	}

	public static ProducerImplementation from( final TypeElement type ) {
		final String providerName = type.getSimpleName().toString();
		final String provider = type.asType().toString();
		return new ProducerImplementation(
				provider.replace( "." + providerName, "" ),
				provider, "", ServiceImplementation.getProvidedServiceClass( type ), "",
				extractNameFrom( type ), false, "", true );
	}

	public static ProducerImplementation from( final ExecutableElement element ) {
		final ExecutableElement method = assertElementIsMethod( element );
		final TypeElement type = (TypeElement)method.getEnclosingElement();
		final String providerName = type.getSimpleName().toString();
		final String provider = type.asType().toString();
		final DeclaredType returnType = (DeclaredType)method.getReturnType();
		final String typeAsString = returnType.toString();
		final String typeName = returnType.asElement().getSimpleName().toString();
		return new ProducerImplementation(
				provider.replace( "." + providerName, "" ),
				provider,
				method.getSimpleName().toString(),
				typeAsString, typeName,
				extractNameFrom( method ),
				measureIfExpectsContextAsParameter( method ),
				ServiceImplementation.getProvidedServiceClass( type ), false );
	}

	static boolean measureIfExpectsContextAsParameter( final ExecutableElement method ) {
		final List<? extends VariableElement> parameters = method.getParameters();
		if ( parameters.size() == 0 )
			return false;
		final VariableElement variableElement = parameters.get( 0 );
		if ( !variableElement.asType().toString().equals( ProviderContext.class.getCanonicalName() ) )
			throw new IllegalStateException(
					"@Provider annotated methods should have no parameters, or the parameter should be of type ProviderContext." );
		return true;
	}

	static String extractNameFrom( final ExecutableElement element ) {
		final Producer producer = element.getAnnotation( Producer.class );
		if ( !producer.name().isEmpty() )
			return producer.name();
		return null;
	}

	static String extractNameFrom( final TypeElement element ) {
		final Stateless stateless = element.getAnnotation( Stateless.class );
		if ( !stateless.name().isEmpty() )
			return stateless.name();
		return null;
	}

	static ExecutableElement assertElementIsMethod( final Element element ) {
		return (ExecutableElement)element;
	}

	public String packageName() {
		return this.packageName;
	}

	public String provider() {
		return this.provider;
	}

	public String providerMethod() {
		return this.providerMethod;
	}

	public String providerName() {
		return this.providerName;
	}

	public String type() {
		return this.type;
	}

	public String typeName() {
		return this.typeName;
	}

	public String name() {
		return this.name;
	}
}
