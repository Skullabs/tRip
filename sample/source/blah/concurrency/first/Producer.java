package blah.concurrency.first;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Producer {

	final ExecutorService executor = Executors.newCachedThreadPool();
	final BlockingQueue<Number> data = new LinkedBlockingQueue<Number>();
	final CountDownLatch counter;

	public Consumer createConsumer() {
		final Consumer consumer = new Consumer(data, counter);
		executor.submit(consumer);
		return consumer;
	}

	public void createConsumers( final int amountOfConsumers ) {
		for ( int i=0; i<amountOfConsumers; i++ )
			createConsumer();
	}

	public void produce(){
		data.add( 1 );
	}

	public void stop() {
		executor.shutdown();
		try {
			executor.awaitTermination(100, TimeUnit.MILLISECONDS);
		} catch (final InterruptedException e) {
			executor.shutdownNow();
		}
	}
}
