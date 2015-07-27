package blah.concurrency;

import static blah.concurrency.TestDefinitions.NUMBER_OF_MESSAGES;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.concurrent.CountDownLatch;

import lombok.SneakyThrows;
import lombok.val;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import blah.concurrency.first.Producer;
import blah.concurrency.helpers.Timer;

@Ignore
public class FirstConsumerBenchmarkTest {

	final CountDownLatch counter = new CountDownLatch( NUMBER_OF_MESSAGES );
	Producer producer;

	@Before
	public void setupProducersAndConsumers() {
		producer = new Producer( counter );
		int processors = Runtime.getRuntime().availableProcessors();
		producer.createConsumers( processors );
	}

	@Test( timeout = 10000 )
	@SneakyThrows
	public void grantThatAllConsumersHasDoneItsJobsByGrantingThatCounterIsZero() {
		val timer = Timer.start();
		for ( int i = 0; i < NUMBER_OF_MESSAGES; i++ )
			producer.produce();
		counter.await();
		System.out.println( "Elapsed Time: " + timer.elapsedTime() );
	}

	@After
	public void stopProducersAndConsumers() {
		assertThat( counter.getCount(), is( 0l ) );
		producer.stop();
	}
}
