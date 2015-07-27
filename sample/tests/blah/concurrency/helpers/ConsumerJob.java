package blah.concurrency.helpers;

import java.util.concurrent.CountDownLatch;

import blah.concurrency.second.Job;
import blah.concurrency.second.JobDispatcher;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ConsumerJob implements Job {

	final CountDownLatch counter;

	@Override
	public void run(JobDispatcher executor) throws InterruptedException {
		counter.countDown();
	}
}
