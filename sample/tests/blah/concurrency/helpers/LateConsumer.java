package blah.concurrency.helpers;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import blah.concurrency.second.JobQueue;

@RequiredArgsConstructor
public class LateConsumer implements Runnable {
	
	final JobQueue producer;
	final Integer timeout;

	@Override
	@SneakyThrows
	public void run() {
		Thread.sleep( timeout );
		System.out.println( "Produced (lately): " + producer.get() );
	}

	
}
