package blah.concurrency;

import static blah.concurrency.TestDefinitions.BUFFER_SIZE;
import static blah.concurrency.TestDefinitions.NUMBER_OF_MESSAGES;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.concurrent.CountDownLatch;

import org.junit.After;
import org.junit.Ignore;
import org.junit.Test;

import blah.concurrency.helpers.ConsumerJob;
import blah.concurrency.helpers.Timer;
import blah.concurrency.second.DefaultJobDispatcher;
import blah.concurrency.second.JobDispatcher;

@Ignore
public class SecondConsumerBenchmarkTest {

	static final int REPEAT_TIMES = 4;
	static final int TOTAL_MESSAGES = REPEAT_TIMES * NUMBER_OF_MESSAGES;

	final CountDownLatch counter = new CountDownLatch( NUMBER_OF_MESSAGES );
	final JobDispatcher dispatcher = new DefaultJobDispatcher( BUFFER_SIZE );

	@Test( timeout = 10000 )
	public void grant() throws InterruptedException {
		for ( int i = 0; i < NUMBER_OF_MESSAGES; i++ )
			dispatcher.submit( new ConsumerJob( counter ) );
		counter.await();
	}

	@Test
	public void grantThatCouldRunTestManyTimes() throws InterruptedException {
		Timer timer = Timer.start();
		for ( int i = 0; i < REPEAT_TIMES; i++ )
			grant();
		double elapsedTime = timer.elapsedTime() / 1000;
		System.out.println( "Total Elapsed time: " + elapsedTime );
		System.out.println( "Total messages: " + TOTAL_MESSAGES );
		System.out.println( "Operations per second: " + ( TOTAL_MESSAGES / elapsedTime ) );
	}

	@After
	public void stopProducersAndConsumers() {
		assertThat( counter.getCount(), is( 0l ) );
	}
}
