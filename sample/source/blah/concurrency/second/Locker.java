package blah.concurrency.second;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Locker {

	final ReentrantLock lock = new ReentrantLock();
	final Condition waitCondition = lock.newCondition();
	
	public void parkNanos( final long nanos ){
		try {
			waitCondition.awaitNanos(nanos);
		} catch (final InterruptedException e) {
			throw new RuntimeException(e);
		}
	}
}
