package blah.concurrency.second;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JobRunner implements Runnable {
	
	final JobQueue buffer;
	final JobDispatcher dispatcher;
	
	Job currentJob;

	@Override
	public void run() {
		while( true )
			if ( hasMoreJobsToRun() )
				tryRunCurrentJob();
	}

	void tryRunCurrentJob() {
		try {
			currentJob.run( dispatcher );
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	boolean hasMoreJobsToRun(){
		try {
			currentJob = buffer.get();
			return currentJob != null;
		} catch (InterruptedException e) {
			return false;
		}
	}
}
