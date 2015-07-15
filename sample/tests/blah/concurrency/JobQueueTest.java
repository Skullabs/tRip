package blah.concurrency;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.Executors;

import lombok.val;

import org.junit.Test;

import blah.concurrency.helpers.EmptyJob;
import blah.concurrency.helpers.LateConsumer;
import blah.concurrency.helpers.Timer;
import blah.concurrency.second.Job;
import blah.concurrency.second.JobQueue;

public class JobQueueTest {
	
	static final int NEARLY_ONE_SECOND = 990;
	static final int BUFFER_SIZE = 2;

	final JobQueue buffer = new JobQueue(BUFFER_SIZE);
	final Job first = EmptyJob.create(1);
	final Job second = EmptyJob.create(2);
	
	@Test( timeout=30000 )
	public void grantThatCouldPutObjectsIntoBufferAndRetrieveItInSameOrder() throws InterruptedException{
		buffer.put(first);
		buffer.put(second);
		assertThat( buffer.get(), is( first ) );
		assertThat( buffer.get(), is( second ) );
	}

	@Test( timeout=3000 )
	public void grantThatItWillFillTheBufferWaitUntilMoreSlotsAreAvailable() throws InterruptedException{
		scheduleToConsumeAJobInOneSecondFromNow();
		val timer = Timer.start();
		for ( int i=0; i<BUFFER_SIZE+1; i++ )
			buffer.put( EmptyJob.create(i) );
		assertElapsedTimeIsGreaterThanOneSecond(timer);
	}

	void assertElapsedTimeIsGreaterThanOneSecond(final Timer timer) {
		long elapsedTime = timer.elapsedTime();
		assertTrue( "Elapsed time should be greater than one second. "
				  + "Buffer should wait to schedule until a job is consumed."
				  + "After one second a job will be consumed in this test.", elapsedTime >= NEARLY_ONE_SECOND );
	}

	void scheduleToConsumeAJobInOneSecondFromNow(){
		Runnable task = new LateConsumer( buffer, 1000 );
		Executors.newSingleThreadExecutor().submit(task);
	}
}
