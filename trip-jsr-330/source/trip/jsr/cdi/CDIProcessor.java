package trip.jsr.cdi;

import static trip.jsr.cdi.CDILoaderOfClasses.*;

import java.io.IOException;

import javax.annotation.processing.RoundEnvironment;
import javax.inject.Named;
import javax.inject.Singleton;

import trip.spi.processor.SPIProcessor;

public class CDIProcessor extends SPIProcessor {

	@Override
	protected void process( RoundEnvironment roundEnv ) throws IOException {
		processSingletons( roundEnv, Singleton.class );
		processStateless( roundEnv, Named.class );
		processProducers( roundEnv, loadClass( ANNOTATION_PRODUCES ) );
	}
}
