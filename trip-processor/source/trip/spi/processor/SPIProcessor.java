package trip.spi.processor;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.net.URI;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic.Kind;
import javax.tools.FileObject;
import javax.tools.JavaFileObject;
import javax.tools.StandardLocation;

import trip.spi.Producer;
import trip.spi.ProducerFactory;
import trip.spi.Singleton;
import trip.spi.Stateless;
import trip.spi.helpers.ServiceLoader;
import trip.spi.processor.stateless.StatelessClass;
import trip.spi.processor.stateless.StatelessClassGenerator;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;

@SupportedAnnotationTypes( "trip.spi.*" )
public class SPIProcessor extends AbstractProcessor {

	static final String EOL = "\n";
	static final String SERVICES = "META-INF/services/";
	static final String PROVIDER_FILE = SERVICES + ProducerFactory.class.getCanonicalName();

	final DefaultMustacheFactory mustacheFactory = new DefaultMustacheFactory();
	final Mustache factoryProviderClazzTemplate = this.mustacheFactory.compile( "META-INF/provided-class.mustache" );
	final Map<String, Set<String>> singletons = new HashMap<String, Set<String>>();
	final StatelessClassGenerator statelessClassGenerator = new StatelessClassGenerator();

	@Override
	public boolean process( final Set<? extends TypeElement> annotations, final RoundEnvironment roundEnv ) {
		try {
			if ( !roundEnv.processingOver() )
				process( roundEnv );
			else
				flush();
		} catch ( final IOException e ) {
			e.printStackTrace();
		}
		return false;
	}

	protected void process( final RoundEnvironment roundEnv ) throws IOException {
		processSingletons( roundEnv, Singleton.class );
		processStateless( roundEnv, Stateless.class );
		processProducers( roundEnv, Producer.class );
	}

	public void processStateless( final RoundEnvironment roundEnv, Class<?extends Annotation> ann ) throws IOException {
		if ( ann != null ){
			final Set<? extends Element> annotatedElements = roundEnv.getElementsAnnotatedWith( ann );
			for ( final Element element : annotatedElements )
				if ( element.getKind() == ElementKind.CLASS )
					memorizeAServiceImplementation( StatelessClass.from( (TypeElement)element ) );
		}
	}

	void memorizeAServiceImplementation( final StatelessClass clazz ) throws IOException {
		createAStatelessClassFrom( clazz );
		final String interfaceClass = clazz.getTypeCanonicalName();
		final String implementationClass = clazz.getGeneratedClassCanonicalName();
		memorizeAServiceImplementation( interfaceClass, implementationClass );
	}

	void createAStatelessClassFrom( final StatelessClass clazz ) throws IOException {
		final String name = clazz.getGeneratedClassCanonicalName();
		if ( !classExists( name ) ) {
			info("Generating " + name);
			final JavaFileObject sourceFile = filer().createSourceFile( name );
			final Writer writer = sourceFile.openWriter();
			this.statelessClassGenerator.write( clazz, writer );
			writer.close();
		}
	}

	public void processSingletons( final RoundEnvironment roundEnv, Class<?extends Annotation> ann ) throws IOException {
		if ( ann != null ){
			final Set<? extends Element> annotatedElements = roundEnv.getElementsAnnotatedWith( ann );
			for ( final Element element : annotatedElements )
				if ( element.getKind() == ElementKind.CLASS )
					memorizeAllImplementations((TypeElement)element);
		}
	}

	private void memorizeAllImplementations(final TypeElement type) throws IOException {
		final String implementationClass = type.asType().toString();
		final TypeMirror superinterfaceOrClass = SingletonImplementation.getProvidedServiceClass(type);
		if ( superinterfaceOrClass != null )
			memorizeAServiceImplementation( new SingletonImplementation( superinterfaceOrClass.toString(), implementationClass ) );
		else {
			for ( final TypeMirror interfaceType : type.getInterfaces() )
				memorizeAServiceImplementation( new SingletonImplementation(interfaceType.toString(), implementationClass) );
			memorizeAServiceImplementation( new SingletonImplementation(implementationClass, implementationClass) );
		}
	}

	void memorizeAServiceImplementation( final SingletonImplementation from ) {
		final String interfaceClass = from.interfaceClass();
		final String implementationClass = from.implementationClass();
		memorizeAServiceImplementation( interfaceClass, implementationClass );
	}

	void memorizeAServiceImplementation( final String interfaceClass, final String implementationClass ) {
		Set<String> list = this.singletons.get( interfaceClass );
		if ( list == null ) {
			list = readAListWithAllCreatedClassesImplementing( interfaceClass );
			this.singletons.put( interfaceClass, list );
		}
		list.add( implementationClass );
	}

	private HashSet<String> readAListWithAllCreatedClassesImplementing( final String interfaceClass ) {
		final LinkedHashSet<String> foundSingletons = new LinkedHashSet<String>();
		for ( final Class<?> implementationClass : ServiceLoader.loadImplementationsFor( interfaceClass ) )
			foundSingletons.add( implementationClass.getCanonicalName() );
		return foundSingletons;
	}

	public void processProducers( final RoundEnvironment roundEnv, Class<?extends Annotation> ann ) throws IOException {
		if ( ann != null ){
			final Set<? extends Element> annotatedElements = roundEnv.getElementsAnnotatedWith( ann );
			for ( final Element element : annotatedElements )
				if ( element.getKind() == ElementKind.METHOD )
					createAProducerFrom( ProducerImplementation.from( (ExecutableElement)element ) );
		}
	}

	void createAProducerFrom( final GenerableClass clazz ) throws IOException {
		final String name = clazz.getGeneratedClassCanonicalName();
		if ( !classExists( name ) ) {
			info( "Generating " + name);
			final JavaFileObject sourceFile = filer().createSourceFile( name );
			final Writer writer = sourceFile.openWriter();
			this.factoryProviderClazzTemplate.execute( writer, clazz );
			writer.close();
		}
	}


	boolean classExists( final String name ) {
		try {
			Class.forName( name );
			return true;
		} catch ( final Exception cause ) {
			return false;
		}
	}

	String createClassCanonicalName( final ProducerImplementation clazz ) {
		return String.format( "%s.%sAutoGeneratedProvider%s",
				clazz.packageName(),
				clazz.typeName(),
				clazz.providerName() );
	}

	void createSingletonMetaInf() throws IOException {
		for ( final String interfaceClass : this.singletons.keySet() ) {
			final Writer resource = createResource( SERVICES + interfaceClass );
			System.out.println( "[INFO] Exposing implementations of " + interfaceClass );
			for ( final String implementation : this.singletons.get( interfaceClass ) ) {
				System.out.println( "[INFO]  > " + implementation );
				log( "Exposing " + implementation + " as " + interfaceClass );
				resource.write( implementation + EOL );
			}
			resource.close();
		}
	}

	Writer createResource( final String resourcePath ) throws IOException {
		final FileObject resource = filer().getResource( StandardLocation.CLASS_OUTPUT, "", resourcePath );
		final URI uri = resource.toUri();
		createNeededDirectoriesTo( uri );
		final File file = createFile( uri );
		return new FileWriter( file );
	}

	void createNeededDirectoriesTo( final URI uri ) {
		File dir = null;
		if ( uri.isAbsolute() )
			dir = new File( uri ).getParentFile();
		else
			dir = new File( uri.toString() ).getParentFile();
		dir.mkdirs();
	}

	File createFile( final URI uri ) throws IOException {
		final File file = new File( uri );
		if ( !file.exists() )
			file.createNewFile();
		return file;
	}

	Filer filer() {
		return this.processingEnv.getFiler();
	}

	private void info(final String msg) {
		System.out.println( "[INFO] " + msg );
		log( msg );
	}

	private void log( final String msg ) {
		processingEnv.getMessager().printMessage( Kind.NOTE, msg );
	}

	public void flush() throws IOException {
		if ( !this.singletons.isEmpty() )
			createSingletonMetaInf();
	}

	/**
	 * We just return the latest version of whatever JDK we run on. Stupid?
	 * Yeah, but it's either that or warnings on all versions but 1. Blame Joe.
	 *
	 * PS: this method was copied from Project Lombok. ;)
	 */
	@Override
	public SourceVersion getSupportedSourceVersion() {
		return SourceVersion.values()[SourceVersion.values().length - 1];
	}
}
