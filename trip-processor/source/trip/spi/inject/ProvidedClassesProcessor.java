package trip.spi.inject;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;

import java.io.*;
import java.lang.annotation.Annotation;
import java.net.URI;
import java.util.*;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.tools.*;

import trip.spi.*;

@SupportedAnnotationTypes( "trip.spi.*" )
public class ProvidedClassesProcessor extends AbstractProcessor {

	static final String EOL = "\n";
	static final String SERVICES = "META-INF/services/";
	static final String PROVIDER_FILE = SERVICES + ProviderFactory.class.getCanonicalName();

	final DefaultMustacheFactory mustacheFactory = new DefaultMustacheFactory();
	final Mustache providedClazzTemplate = this.mustacheFactory.compile( "META-INF/provided-class.mustache" );
	final List<String> producers = new ArrayList<>();
	final Map<String, List<String>> singletons = new HashMap<>();

	@Override
	public boolean process( Set<? extends TypeElement> annotations, RoundEnvironment roundEnv ) {
		try {
			process( roundEnv );
		} catch ( IOException e ) {
			e.printStackTrace();
		}
		return true;
	}

	void process( RoundEnvironment roundEnv ) throws IOException {
		processSingletons( roundEnv, Singleton.class );
		processProducers( roundEnv, Producer.class );
		if ( !this.producers.isEmpty() )
			createServiceProviderForProducers();
		if ( !this.singletons.isEmpty() )
			createSingletonMetaInf();
		flush();
	}

	void processSingletons( RoundEnvironment roundEnv, Class<? extends Annotation> annotation ) {
		Set<? extends Element> annotatedElements = roundEnv.getElementsAnnotatedWith( annotation );
		for ( Element element : annotatedElements )
			if ( element.getKind() == ElementKind.CLASS )
				memorizeASingletonImplementation( SingletonImplementation.from( element ) );
	}

	void memorizeASingletonImplementation( SingletonImplementation from ) {
		List<String> list = this.singletons.get( from.interfaceClass() );// ,
		if ( list == null ) {
			list = new ArrayList<>();
			this.singletons.put( from.interfaceClass(), list );
		}
		list.add( from.implementationClass() );
	}

	void processProducers( RoundEnvironment roundEnv, Class<? extends Annotation> annotation ) throws IOException {
		Set<? extends Element> annotatedElements = roundEnv.getElementsAnnotatedWith( annotation );
		for ( Element element : annotatedElements )
			if ( element.getKind() == ElementKind.METHOD )
				createAProducerFrom( ProducerImplementation.from( element ) );
	}

	void createAProducerFrom( ProducerImplementation clazz ) throws IOException {
		String name = createClassCanonicalName( clazz );
		if ( !classExists( clazz, name ) ) {
			System.out.println( "Generating " + name );
			JavaFileObject sourceFile = filer().createSourceFile( name );
			Writer writer = sourceFile.openWriter();
			this.providedClazzTemplate.execute( writer, clazz );
			writer.close();
			memorizeProvider( name );
		}
	}

	boolean classExists( ProducerImplementation clazz, String name ) {
		try {
			Class.forName( name );
			return true;
		} catch ( IllegalArgumentException | ClassNotFoundException cause ) {
			return false;
		}
	}

	String createClassCanonicalName( ProducerImplementation clazz ) {
		return String.format( "%s.%sAutoGeneratedProvider%s",
				clazz.packageName(),
				clazz.typeName(),
				clazz.providerName() );
	}

	void memorizeProvider( String name ) {
		this.producers.add( name );
	}

	void createServiceProviderForProducers() throws IOException {
		Writer writer = createMetaInfForFactoryProviderInterface();
		for ( String provider : this.producers )
			writer.write( provider + EOL );
		writer.close();
	}

	Writer createMetaInfForFactoryProviderInterface() throws IOException {
		return createResource( PROVIDER_FILE );
	}

	void createSingletonMetaInf() throws IOException {
		for ( String interfaceClass : this.singletons.keySet() ) {
			System.out.println( "Registering service providers for " + interfaceClass );
			Writer resource = createResource( SERVICES + interfaceClass );
			for ( String implementation : this.singletons.get( interfaceClass ) )
				resource.write( implementation + EOL );
			resource.close();
		}
	}

	Writer createResource( String resourcePath ) throws IOException {
		FileObject resource = filer().getResource( StandardLocation.CLASS_OUTPUT, "", resourcePath );
		URI uri = resource.toUri();
		createNeededDirectoriesTo( uri );
		File file = createFile( uri );
		return new FileWriter( file );
	}

	void createNeededDirectoriesTo( URI uri ) {
		File dir = null;
		if ( uri.isAbsolute() )
			dir = new File( uri ).getParentFile();
		else
			dir = new File( uri.toString() ).getParentFile();
		dir.mkdirs();
	}

	File createFile( URI uri ) throws IOException {
		File file = new File( uri );
		if ( !file.exists() )
			file.createNewFile();
		return file;
	}

	Filer filer() {
		return this.processingEnv.getFiler();
	}

	void flush() {
		this.producers.clear();
		this.singletons.clear();
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
