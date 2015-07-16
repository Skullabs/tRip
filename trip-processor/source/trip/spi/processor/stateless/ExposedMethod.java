package trip.spi.processor.stateless;

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;

public class ExposedMethod {

	/**
	 * The method name.
	 */
	final String name;

	/**
	 * The Canonical Name notation of the return type.
	 */
	final String returnType;

	/**
	 * List of parameter types in Canonical Name notation.
	 */
	final List<String> parameterTypes;

	public ExposedMethod( String name, String returnType, List<String> parameterTypes ) {
		this.name = name;
		this.returnType = returnType;
		this.parameterTypes = parameterTypes;
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
		String name = method.getSimpleName().toString();
		String returnType = method.getReturnType().toString();
		List<String> parameterTypes = extractParameters( method );
		return new ExposedMethod( name, returnType, parameterTypes );
	}

	static List<String> extractParameters( ExecutableElement method ) {
		List<String> list = new ArrayList<String>();
		for ( VariableElement parameter : method.getParameters() )
			list.add( parameter.asType().toString() );
		return list;
	}
}
