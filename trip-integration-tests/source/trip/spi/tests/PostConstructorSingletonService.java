package trip.spi.tests;

import lombok.Getter;
import trip.spi.Singleton;

@Singleton
public class PostConstructorSingletonService {

	@Getter
	final Status status = new Status();

	@javax.annotation.PostConstruct
	public void postConstructorJava() {
		status.calledPostContructJavaAnnotation++;
	}
}
