package trip.jsr.cdi;

import static org.junit.Assert.assertEquals;

import java.lang.annotation.Annotation;

import javax.inject.Inject;

import lombok.SneakyThrows;

import org.junit.Test;

public class AnnotationWithReflectionTest {

	@SuppressWarnings("unchecked")
	@Test
	@SneakyThrows
	public void ensure(){
		final Class<? extends Annotation> ann = (Class<? extends Annotation>)Class.forName("javax.inject.Inject");
		assertEquals( Inject.class, ann );
	}
}
