package blah.concurrency;

import static blah.concurrency.TestDefinitions.NUMBER_OF_MESSAGES;

import java.util.concurrent.CountDownLatch;

import org.junit.Ignore;
import org.junit.Test;

@Ignore
public class ThirdConsumerBenchmarkTest {

	final CountDownLatch counter = new CountDownLatch( NUMBER_OF_MESSAGES );

	@Test( timeout = 10000 )
	public void grant() throws InterruptedException {
		for ( int i = 0; i < NUMBER_OF_MESSAGES; i++ )
			counter.countDown();
		counter.await();
	}
}
