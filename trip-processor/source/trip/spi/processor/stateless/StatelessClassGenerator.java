package trip.spi.processor.stateless;

import java.io.Writer;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;

public class StatelessClassGenerator {

	final DefaultMustacheFactory mustacheFactory = new DefaultMustacheFactory();
	final Mustache factoryProviderClazzTemplate = this.mustacheFactory.compile( "META-INF/stateless-class.mustache" );

	public void write( StatelessClass clazz, Writer writer ) {
		factoryProviderClazzTemplate.execute( writer, clazz );
	}
}
