package trip.spi.processor;

import static trip.spi.processor.NameTransformations.stripGenericsFrom;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeMirror;

import trip.spi.Singleton;
import trip.spi.Stateless;

public class SingletonImplementation {

	public static final List<Class<? extends Annotation>> QUALIFIERS =
			Arrays.asList( trip.spi.Qualifier.class );

	final String interfaceClass;
	final String implementationClass;

	public SingletonImplementation( final String interfaceClass, final String implementationClass ) {
		this.interfaceClass = stripGenericsFrom( interfaceClass );
		this.implementationClass = stripGenericsFrom( implementationClass );
	}

	public String implementationClass() {
		return this.implementationClass;
	}

	public String interfaceClass() {
		return this.interfaceClass;
	}

	public static String getProvidedServiceClassAsString( final TypeElement type ) {
		return getProvidedServiceClassAsString(type, type.asType().toString());
	}

	public static String getProvidedServiceClassAsStringOrNull( final TypeElement type ) {
		String string = getProvidedServiceClassAsString( type, null );
		if ( string == null && ( isAnnotatedForSingleton( type ) || isAnnotatedForStateless( type ) ) )
			string = type.asType().toString();
		return string;
	}

	private static String getProvidedServiceClassAsString(final TypeElement type, String defaultValue) {
		final TypeMirror typeMirror = getProvidedServiceClass( type );
		if ( typeMirror != null )
			return typeMirror.toString();
		return defaultValue;
	}

	public static TypeMirror getProvidedServiceClass( final TypeElement type ) {
		TypeMirror foundType = null;
		if ( isAnnotatedForStateless( type ) )
			foundType = getProvidedServiceClassForStateless( type );
		if ( foundType == null && isAnnotatedForSingleton( type ) )
			foundType = getProvidedServiceClassForSingleton( type );
		return foundType;
	}

	private static boolean isAnnotatedForStateless( final TypeElement type ) {
		return type.getAnnotation( Stateless.class ) != null;
	}

	private static boolean isAnnotatedForSingleton( final TypeElement type ) {
		return type.getAnnotation( Singleton.class ) != null;
	}

	private static TypeMirror getProvidedServiceClassForStateless( final TypeElement type ) {
		final TypeMirror statelessService = getProvidedStatelessAsTypeMirror( type );
		if ( isStatelessAnnotationClassBlank( statelessService ) )
			return null;
		return statelessService;
	}

	private static TypeMirror getProvidedStatelessAsTypeMirror( final TypeElement type ) {
		try {
			final Stateless singleton = type.getAnnotation( Stateless.class );
			if ( singleton != null )
				singleton.exposedAs();
			return null;
		} catch ( final MirroredTypeException cause ) {
			return cause.getTypeMirror();
		}
	}

	private static boolean isStatelessAnnotationClassBlank( final TypeMirror providedClass ) {
		return providedClass.toString().equals( Stateless.class.getCanonicalName() );
	}

	private static TypeMirror getProvidedServiceClassForSingleton( final TypeElement type ) {
		final TypeMirror providedClass = getProvidedSingletonAsTypeMirror( type );
		if ( isSingletonAnnotationBlank( providedClass ) )
			return null;
		return providedClass;
	}

	private static TypeMirror getProvidedSingletonAsTypeMirror( final TypeElement type ) {
		try {
			final Singleton singleton = type.getAnnotation( Singleton.class );
			if ( singleton != null )
				singleton.exposedAs();
			return null;
		} catch ( final MirroredTypeException cause ) {
			return cause.getTypeMirror();
		}
	}

	private static boolean isSingletonAnnotationBlank( final TypeMirror providedClass ) {
		return providedClass.toString().equals( Singleton.class.getCanonicalName() );
	}

	public static List<String> getQualifierAnnotation( final Element type ){
		final List<String> qualifierAnn = new ArrayList<>();
		for (final AnnotationMirror annotationMirror : type.getAnnotationMirrors())
		for ( final Class<?extends Annotation> annClass : QUALIFIERS )
			if ( isAnnotationPresent(annotationMirror.getAnnotationType().asElement(), annClass.getCanonicalName() ) )
				qualifierAnn.add(annotationMirror.getAnnotationType().toString());
		return qualifierAnn;
	}

	static boolean isAnnotationPresent(final Element element, String canonicalName) {
		for (final AnnotationMirror annotationMirror : element.getAnnotationMirrors()) {
			final DeclaredType annotationType = annotationMirror.getAnnotationType();
			final TypeElement annotationElement = (TypeElement) annotationType.asElement();
			if ( annotationElement.getQualifiedName().contentEquals(canonicalName) )
				return true;
		}
		return false;
	}
}
