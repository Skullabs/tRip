package blah.concurrency.first;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Consumer implements Runnable {

	final BlockingQueue<Number> data;
	final CountDownLatch counter;

	@Override
	public void run() {
		while( counter.getCount() > 0 ) {
			if ( takeData() )
				counter.countDown();
		}
	}

	boolean takeData() {
		try {
			return data.poll(1, TimeUnit.SECONDS) != null;
		} catch (InterruptedException e) {
			return false;
		}
	}
}
