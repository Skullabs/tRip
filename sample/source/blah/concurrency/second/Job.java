package blah.concurrency.second;

public interface Job {

	void run( JobDispatcher executor ) throws InterruptedException;

}
