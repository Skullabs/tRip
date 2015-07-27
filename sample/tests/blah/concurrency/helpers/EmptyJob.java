package blah.concurrency.helpers;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import blah.concurrency.second.Job;
import blah.concurrency.second.JobDispatcher;

@EqualsAndHashCode
@RequiredArgsConstructor( staticName="create" )
public class EmptyJob implements Job {
	
	final int identifier;

	@Override
	public void run( JobDispatcher dispatcher ) {
	}
}