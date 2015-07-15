package blah.concurrency.second;

import java.util.concurrent.Executors;

public class DefaultJobDispatcher implements JobDispatcher {

	final int availableProcessors = Runtime.getRuntime().availableProcessors();
	final java.util.concurrent.ExecutorService executor;
	final JobQueue buffer;
	
	public DefaultJobDispatcher( int bufferSize ) {
		buffer = new JobQueue(bufferSize);
		executor = Executors.newFixedThreadPool( availableProcessors );
		initializeJobRunners();
	}

	void initializeJobRunners(){
		for ( int i=0; i<availableProcessors; i++ )
			executor.submit( new JobRunner( buffer, this ) );
	}

	@Override
	public void submit( Job job ) throws InterruptedException {
		buffer.put(job);
	}
}
