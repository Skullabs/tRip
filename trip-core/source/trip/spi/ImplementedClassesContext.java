package trip.spi;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import trip.spi.helpers.DefaultFieldQualifierExtractor;
import trip.spi.helpers.ProvidableClass;
import trip.spi.helpers.QualifierExtractor;
import trip.spi.helpers.ServiceLoader;

@SuppressWarnings( { "rawtypes", "unchecked" } )
public class ImplementedClassesContext {

	final Map<Class<?>, Iterable<Class<?>>> implementedClasses = new HashMap<>();
	final Map<Class<?>, ProvidableClass<?>> providableClassCache = new HashMap<>();

	// TODO: Make it settable
	final QualifierExtractor qualifierExtractor = new QualifierExtractor( Arrays.asList( new DefaultFieldQualifierExtractor() ) );

	public <T> List<Class<T>> loadClassesImplementing( final Class<T> interfaceClazz ) {
		List<Class<T>> implementations = (List)implementedClasses.get( interfaceClazz );
		if ( implementations == null )
			synchronized ( implementedClasses ) {
				implementations = (List)implementedClasses.get( interfaceClazz );
				if ( implementations == null ) {
					implementations = ServiceLoader.loadImplementationsFor( interfaceClazz );
					implementedClasses.put( (Class)interfaceClazz, (Iterable)implementations );
				}
			}
		return implementations;
	}

	public ProvidableClass<?> retrieveProvidableClass( final Class<?> targetClazz ) {
		ProvidableClass<?> providableClass = providableClassCache.get( targetClazz );
		if ( providableClass == null )
			synchronized ( providableClassCache ) {
				providableClass = providableClassCache.get( targetClazz );
				if ( providableClass == null ) {
					providableClass = ProvidableClass.wrap( qualifierExtractor, targetClazz );
					providableClassCache.put( targetClazz, providableClass );
				}
			}
		return providableClass;
	}
}