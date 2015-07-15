package blah.concurrency.second;


public class Data<T> {

	final FieldReference wroteField = FieldReference.wrap( this, "wrote" );
//	final Locker readLocker = new Locker();
//	final Locker writeLocker = new Locker();

	volatile T value;
	volatile int wrote = 0;

	public T get() {
		while( true )
			if ( wroteField.compareAndSet( 1, 0 ) )
				return currentValue();
	}

	public T set(final T data) {
		while( true )
			if ( wroteField.compareAndSet( 0, 1 ) )
				return this.value = data;
	}

	T currentValue() {
		return value;
	}
}
