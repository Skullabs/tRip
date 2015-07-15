package trip.jsr.cdi;

import static trip.jsr.cdi.CDILoaderOfClasses.loadClass;

import java.io.IOException;

import javax.annotation.processing.RoundEnvironment;
import javax.inject.Named;
import javax.inject.Singleton;

import trip.spi.inject.ProvidedClassesProcessor;

public class CDIProcessor extends ProvidedClassesProcessor {

	@Override
	protected void process( RoundEnvironment roundEnv ) throws IOException {
		processSingletons( roundEnv, Singleton.class );
		processStateless( roundEnv, Named.class );
		processProducers( roundEnv, loadClass("javax.enterprise.inject.Produces") );
	}
}
