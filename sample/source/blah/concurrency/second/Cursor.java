package blah.concurrency.second;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Cursor {

	final FieldReference cursorField = FieldReference.wrap( this, "cursor" );
	final int threshold;
	volatile int cursor;

	public int next() {
		return next( 1 );
	}

	public int next( int increment ){
		int current, next;
		while( true ) {
			current = current();
			next = (current + increment) % threshold;
			if ( cursorField.compareAndSet(current, next) )
				return next;
		}
	}

	public int current(){
		return cursor;
	}
}
