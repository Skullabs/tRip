package trip.spi.processor;

import static trip.spi.processor.NameTransformations.stripGenericsFrom;

import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;

import trip.spi.ProviderContext;

public class ProducerImplementation implements GenerableClass {

	final String packageName;
	final String provider;
	final String providerName;
	final String providerMethod;
	final String type;
	final String typeName;
	final boolean expectsContext;
	final String serviceFor;
	final boolean stateless;
	final List<String> annotations;

	public ProducerImplementation(
			final String packageName, final String provider,
			final String providedMethod, final String type,
			final String typeName,
			final boolean expectsContext,
			final String serviceFor, final boolean stateless,
			final List<String> annotations ) {
		this.packageName = stripGenericsFrom( packageName );
		this.provider = stripGenericsFrom( provider );
		this.providerMethod = stripGenericsFrom( providedMethod );
		this.type = stripGenericsFrom( type );
		this.typeName = stripGenericsFrom( typeName );
		this.expectsContext = expectsContext;
		this.serviceFor = serviceFor;
		this.providerName = String.valueOf( createIdentifier() );
		this.stateless = stateless;
		this.annotations = annotations;
	}

	private long createIdentifier() {
		final int hashCode =
				String.format( "%s%s%s%s%s%s%s",
						packageName, provider, providerMethod,
						type, typeName, expectsContext, stateless )
						.hashCode();

		return hashCode & 0xffffffffl;
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
				measureIfExpectsContextAsParameter( method ),
				SingletonImplementation.getProvidedServiceClassAsStringOrNull( type ), false,
				SingletonImplementation.getQualifierAnnotation(method) );
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

	@Override
	public String getGeneratedClassCanonicalName() {
		return String.format( "%s.%sAutoGeneratedProvider%s",
				packageName(),
				typeName(),
				providerName() );
	}
}