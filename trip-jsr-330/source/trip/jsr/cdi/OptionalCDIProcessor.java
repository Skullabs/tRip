package trip.jsr.cdi;

import static trip.jsr.cdi.CDILoaderOfClasses.isClassPresent;
import static trip.jsr.cdi.CDILoaderOfClasses.newInstanceOf;

import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;

@SupportedAnnotationTypes( "javax.inject.*" )
public class OptionalCDIProcessor extends AbstractProcessor {

	private static final String DEFAULT_PROCESSOR = "trip.spi.processor.SPIProcessor";

	AbstractProcessor cdiProcessor;

	@Override
	public synchronized void init(ProcessingEnvironment processingEnv) {
		super.init(processingEnv);
		if ( isClassPresent( DEFAULT_PROCESSOR ) ) {
			cdiProcessor = newInstanceOf("trip.jsr.cdi.CDIProcessor", AbstractProcessor.class);
			cdiProcessor.init( processingEnv );
		}
	}

	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		if ( cdiProcessor != null )
			return cdiProcessor.process(annotations, roundEnv);
		return false;
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
