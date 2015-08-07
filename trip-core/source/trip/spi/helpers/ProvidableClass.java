package trip.spi.helpers;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import javax.annotation.PostConstruct;

import lombok.RequiredArgsConstructor;
import trip.spi.GeneratedFromStatelessService;
import trip.spi.ServiceProvider;
import trip.spi.ServiceProviderException;

@RequiredArgsConstructor
public class ProvidableClass<T> {

	final Class<T> targetClazz;
	final Iterable<ProvidableField> fields;
	final Consumer<Object> postConstructor;

	public void provide( Object instance, ServiceProvider provider )
			throws ServiceProviderException, IllegalArgumentException, IllegalAccessException {
		for ( final ProvidableField field : fields )
			field.provide( instance, provider );
		postConstructor.accept(instance);
	}

	public static <T> ProvidableClass<T> wrap( QualifierExtractor extractor, Class<T> targetClazz ) {
		return new ProvidableClass<T>(
				targetClazz,
				readClassProvidableFields( extractor, targetClazz ),
				readPostConstructor(targetClazz));
	}

	static Consumer<Object> readPostConstructor( Class<?> targetClazz ){
		Method postConstructor = null;
		for ( final Method method : targetClazz.getMethods() )
			if ( method.isAnnotationPresent(PostConstruct.class) ){
				postConstructor = method;
				break;
			}
		return postConstructor != null
			? new PostConstructorMethod(postConstructor)
			: EmptyMethod.INSTANCE;
	}

	static Iterable<ProvidableField> readClassProvidableFields( QualifierExtractor extractor, Class<?> targetClazz ) {
		final List<ProvidableField> providableFields = new ArrayList<ProvidableField>();
		Class<? extends Object> clazz = targetClazz;
		while ( !Object.class.equals( clazz ) ) {
			populateWithProvidableFields( extractor, clazz, providableFields );
			if ( clazz.isAnnotationPresent(GeneratedFromStatelessService.class) )
				break;
			clazz = clazz.getSuperclass();
		}
		return providableFields;
	}

	static void populateWithProvidableFields( QualifierExtractor extractor, Class<?> targetClazz, List<ProvidableField> providableFields ) {
		for ( final Field field : targetClazz.getDeclaredFields() ){
			final Collection<Class<? extends Annotation>> qualifiers = extractQualifiersFromAvoidingNPEWhenCreatingQualifierExtractor(extractor, field);
			if ( extractor.isASingleElementProvider( field ) )
				providableFields.add( SingleElementProvidableField.from( qualifiers, field ) );
			else if ( extractor.isAManyElementsProvider( field ) )
				providableFields.add( ManyElementsProvidableField.from( qualifiers, field ) );
		}
	}

	private static Collection<Class<? extends Annotation>> extractQualifiersFromAvoidingNPEWhenCreatingQualifierExtractor(
			final QualifierExtractor extractor, final Field field)
	{
		if ( null == extractor )
			return Collections.emptyList();
		return extractor.extractQualifiersFrom(field);
	}
}

class EmptyMethod implements Consumer<Object> {

	static final Consumer<Object> INSTANCE = new EmptyMethod();

	@Override
	public void accept(Object t) {}
}

@RequiredArgsConstructor
class PostConstructorMethod implements Consumer<Object> {

	final Method method;

	@Override
	public void accept(Object target) {
		try {
			method.invoke(target);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new ServiceProviderException("Can't call "+method.toString(),e);
		}
	}
}