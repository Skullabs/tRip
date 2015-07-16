package trip.spi.processor.stateless;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;

import trip.spi.PostConstruct;
import trip.spi.PreDestroy;
import trip.spi.processor.GenerableClass;
import trip.spi.processor.SingletonImplementation;

public class StatelessClass implements GenerableClass {

	/**
	 * This attribute will be part of the class name.
	 */
	final long identifaction;

	/**
	 * The package where the new class should be placed.
	 */
	final String packageName;

	/**
	 * The exposed type as Canonical Name notation.
	 */
	final String typeCanonicalName;

	/**
	 * The exposed type simple name.
	 */
	final String typeName;

	/**
	 * The implementation type as Canonical Name notation.
	 */
	final String implementationCanonicalName;

	/**
	 * Identify if the exposed type is a class or interface.
	 */
	final boolean exposedByClass;

	/**
	 * A list of methods that will be wrapped up.
	 */
	final List<ExposedMethod> exposedMethods;

	/**
	 * A list of methods that run after construct the Stateless service.
	 */
	final List<ExposedMethod> postConstructMethods;

	/**
	 * A list of methods that run before destroy the Stateless service.
	 */
	final List<ExposedMethod> preDestroyMethods;

	/**
	 * @param typeCanonicalName
	 * @param implementationCanonicalName
	 * @param exposedByClass
	 * @param exposedMethods
	 * @param postConstructMethods
	 * @param preDestroyMethods
	 */
	public StatelessClass( final String typeCanonicalName,
		final String implementationCanonicalName, final boolean exposedByClass,
		final List<ExposedMethod> exposedMethods, final List<ExposedMethod> postConstructMethods,
		final List<ExposedMethod> preDestroyMethods ) {
		this.packageName = extractPackageNameFrom( implementationCanonicalName );
		this.typeCanonicalName = typeCanonicalName;
		this.typeName = extractClassNameFrom( typeCanonicalName );
		this.implementationCanonicalName = implementationCanonicalName;
		this.exposedByClass = exposedByClass;
		this.exposedMethods = exposedMethods;
		this.postConstructMethods = postConstructMethods;
		this.preDestroyMethods = preDestroyMethods;
		this.identifaction = createIdentifier();
	}

	private long createIdentifier() {
		final int hashCode =
				String.format( "%s%s%s%s%s%s",
						packageName, typeCanonicalName,
						typeName, implementationCanonicalName, exposedByClass, exposedMethodsAsString() )
						.hashCode();

		return hashCode & 0xffffffffl;
	}

	private String exposedMethodsAsString() {
		final StringBuilder buffer = new StringBuilder();
		for ( final ExposedMethod method : exposedMethods )
			buffer
					.append( method.name )
					.append( method.returnType )
					.append( method.getParametersWithTypesAsString() );
		return buffer.toString();
	}

	String extractPackageNameFrom( final String canonicalName ) {
		return canonicalName.replaceFirst( "(.*)\\.[^\\.]+", "$1" );
	}

	String extractClassNameFrom( final String canonicalName ) {
		return canonicalName.replaceFirst( ".*\\.([^\\.]+)", "$1" );
	}

	public Long getIdentifaction() {
		return identifaction;
	}

	public String getPackageName() {
		return packageName;
	}

	public String getTypeCanonicalName() {
		return typeCanonicalName;
	}

	public String getTypeName() {
		return typeName;
	}

	public String getImplementationCanonicalName() {
		return implementationCanonicalName;
	}

	public boolean isExposedByClass() {
		return exposedByClass;
	}

	public List<ExposedMethod> getExposedMethods() {
		return exposedMethods;
	}

	@Override
	public String getGeneratedClassCanonicalName() {
		return String.format( "%s.%sStateless%s",
				packageName,
				typeName,
				identifaction );
	}

	public static StatelessClass from( final TypeElement type ) {
		final String typeCanonicalName = SingletonImplementation.getProvidedServiceClassAsString( type );
		final String implementationCanonicalName = type.asType().toString();
		final boolean exposedByClass = isImplementingClass( typeCanonicalName, type );
		final List<ExposedMethod> exposedMethods = retrieveExposedMethods( type );
		return new StatelessClass( typeCanonicalName,
			implementationCanonicalName, exposedByClass, exposedMethods,
			retrieveMethodsAnnotatedWith( type, PostConstruct.class, javax.annotation.PostConstruct.class ),
			retrieveMethodsAnnotatedWith( type, PreDestroy.class, javax.annotation.PreDestroy.class ) );
	}

	public static boolean isImplementingClass( final String typeCanonicalName, TypeElement type ) {
		while ( !Object.class.getCanonicalName().equals( type.asType().toString() ) ) {
			for ( final TypeMirror interfaceType : type.getInterfaces() )
				if ( typeCanonicalName.equals( interfaceType.toString() ) )
					return false;
			type = (TypeElement)( (DeclaredType)type.getSuperclass() ).asElement();
		}
		return true;
	}

	static List<ExposedMethod> retrieveExposedMethods( final TypeElement type ) {
		final List<ExposedMethod> list = new ArrayList<ExposedMethod>();
		for ( final Element method : type.getEnclosedElements() )
			if ( isExposedMethod( method ) )
				list.add( ExposedMethod.from( (ExecutableElement)method ) );
		return list;
	}

	@SafeVarargs
	static List<ExposedMethod> retrieveMethodsAnnotatedWith( final TypeElement type,
		final Class<? extends Annotation>... annotations ) {
		final List<ExposedMethod> list = new ArrayList<ExposedMethod>();
		for ( final Class<? extends Annotation> annotation : annotations )
			for ( final Element method : type.getEnclosedElements() )
				if ( isExposedMethod( method )
					&& method.getAnnotation( annotation ) != null )
					list.add( ExposedMethod.from( (ExecutableElement)method ) );
		return list;
	}

	static boolean isExposedMethod( final Element method ) {
		return method.getKind().equals( ElementKind.METHOD )
			&& !isPrivate( (ExecutableElement)method );
	}

	static boolean isPrivate( final ExecutableElement method ) {
		for ( final Modifier modifier : method.getModifiers() )
			if ( modifier.equals( Modifier.PRIVATE ) )
				return true;
		return false;
	}
}
