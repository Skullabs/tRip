package blah.concurrency.helpers;


public class Timer {

	long startTime;
	
	public Timer() {
		startTime = System.currentTimeMillis();
	}
	
	public static Timer start(){
		return new Timer();
	}

	public long elapsedTime() {
		return System.currentTimeMillis() - startTime;
	}
}
