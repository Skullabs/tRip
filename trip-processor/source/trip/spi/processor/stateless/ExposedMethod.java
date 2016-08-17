package trip.spi.processor.stateless;

import static javax.lang.model.type.TypeKind.TYPEVAR;
import java.util.*;
import javax.lang.model.element.*;
import javax.lang.model.type.*;

public class ExposedMethod {

	/**
	 * The method name.
	 */
	final String name;

	/**
	 * The Canonical Name notation of the return type.
	 */
	final String returnType;

	final String typeParameters;

	/**
	 * List of parameter types in Canonical Name notation.
	 */
	final List<String> parameterTypes;

	public ExposedMethod( String name, String returnType, List<String> parameterTypes, Set<String> typeParameters ) {
		this.name = name;
		this.returnType = returnType;
		this.parameterTypes = parameterTypes;
		this.typeParameters = typeParameters.isEmpty() ? "" :
			"<" + String.join( ",", typeParameters ) + ">";
	}

	/**
	 * Identifies if the method should or not returns data.
	 */
	public boolean getReturnable() {
		return !"void".equals( returnType );
	}

	public String getParametersWithTypesAsString() {
		StringBuilder buffer = new StringBuilder();
		int counter = 0;
		for ( String type : parameterTypes ) {
			if ( buffer.length() > 0 )
				buffer.append( ',' );
			buffer.append( type + " arg" + ( counter++ ) );
		}
		return buffer.toString();
	}

	public String getParametersAsString() {
		StringBuilder buffer = new StringBuilder();
		for ( int counter = 0; counter < parameterTypes.size(); counter++ ) {
			if ( buffer.length() > 0 )
				buffer.append( ',' );
			buffer.append( "arg" + counter );
		}
		return buffer.toString();
	}

	public static ExposedMethod from( ExecutableElement method ) {
		final String name = method.getSimpleName().toString();
		String returnType = method.getReturnType().toString();
		final List<String> parameterTypes = extractParameters( method );
		return new ExposedMethod( name, returnType, parameterTypes, extractTypeParameters( method ) );
	}

	static List<String> extractParameters( ExecutableElement method ) {
		final List<String> list = new ArrayList<String>();
		for ( final VariableElement parameter : method.getParameters() )
			list.add( parameter.asType().toString() );
		return list;
	}

	static Set<String> extractTypeParameters( ExecutableElement method ) {
		final Set<String> list = new HashSet<>();
		for ( final VariableElement parameter : method.getParameters() ) {
			if ( parameter.asType() instanceof DeclaredType ) {
				final DeclaredType declaredType = (DeclaredType)parameter.asType();
				for( final TypeMirror mirror : declaredType.getTypeArguments() ) {
					if ( TYPEVAR.equals( mirror.getKind() ) )
						list.add( mirror.toString() );
				}
			}
		}
		return list;
	}
}
