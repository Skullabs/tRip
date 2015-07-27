package trip.spi.helpers;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceConfigurationError;

import lombok.Cleanup;
import lombok.Getter;

@Getter
public class LazyClassReader<S> implements Iterator<Class<S>> {

	private static final String PREFIX = "META-INF/services/";
	private static final int NOT_FOUND = -1;

	final List<Class<S>> cache = new ArrayList<Class<S>>();
	final String serviceClassCanonicalName;
	final ClassLoader loader;
	final Enumeration<URL> resources;
	Iterator<String> currentResourceLines;

	public LazyClassReader( final Class<S> serviceClass, final ClassLoader loader ) {
		this( serviceClass.getCanonicalName(), loader );
	}

	public LazyClassReader(
		final String serviceClassCanonicalName,
		final ClassLoader loader ) {
		this.serviceClassCanonicalName = serviceClassCanonicalName;
		this.loader = loader;
		this.resources = readAllServiceResources();
	}

	Enumeration<URL> readAllServiceResources() {
		try {
			final String fullName = PREFIX + serviceClassCanonicalName;
			return loader.getResources( fullName );
		} catch ( final IOException cause ) {
			throw new ServiceConfigurationError( serviceClassCanonicalName + ": " + cause.getMessage(), cause );
		}
	}

	@Override
	public boolean hasNext() {
		try {
			if ( currentResourceLines == null || !currentResourceLines.hasNext() )
				readNextResourceFile();
			return currentResourceLines != null && currentResourceLines.hasNext();
		} catch ( final FileNotFoundException cause ) {
			return false;
		} catch ( final IOException cause ) {
			throw new IllegalStateException( cause );
		}
	}

	void readNextResourceFile() throws IOException {
		if ( getResources().hasMoreElements() ) {
			final URL nextElement = getResources().nextElement();
			currentResourceLines = readLines( nextElement );
		}
	}

	@Override
	@SuppressWarnings( "unchecked" )
	public Class<S> next() {
		final String classCanonicalName = currentResourceLines.next();
		try {
			final Class<S> clazz = (Class<S>)Class.forName( classCanonicalName, false, loader );
			cache.add( clazz );
			return clazz;
		} catch ( final ClassNotFoundException cause ) {
			throw new IllegalStateException( "Could not read class " + classCanonicalName, cause );
		} catch ( final NoClassDefFoundError cause ) {
			throw new IllegalStateException( "Could not read class " + classCanonicalName, cause );
		}
	}

	@Override
	public void remove() {
	}

	Iterator<String> readLines( final URL url ) throws IOException {
		@Cleanup final InputStream inputStream = url.openStream();
		@Cleanup final BufferedReader reader = new BufferedReader( new InputStreamReader( inputStream, "utf-8" ) );
		final List<String> lines = new ArrayList<String>();
		String line = null;
		while ( ( line = readNextLine( reader ) ) != null )
			lines.add( line );
		return lines.iterator();
	}

	String readNextLine( final BufferedReader reader ) throws IOException {
		final String ln = reader.readLine();
		if ( ln != null && !isValidClassName( ln ) )
			throw new IOException( "Invalid class name: " + ln );
		return ln;
	}

	boolean isValidClassName( final String className ) {
		return className.indexOf( ' ' ) == NOT_FOUND
			&& className.indexOf( '\t' ) == NOT_FOUND;
	}
}