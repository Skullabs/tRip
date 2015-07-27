package blah.concurrency.second;

public interface JobDispatcher {

	void submit(Job job) throws InterruptedException;

}