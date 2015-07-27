package trip.jsr.cdi.singleton;

import javax.inject.Singleton;

@Singleton
public class HelloWorldReader implements Closeable, Reader {

	@Override
	public String read() {
		return toString();
	}

	@Override
	public void close() {
		throw new UnsupportedOperationException();
	}
}
