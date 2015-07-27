package trip.spi;

import lombok.Getter;

@Getter
@GeneratedFromStatelessService
@Singleton
public class MyFakeStatelessService extends Readable {

	@Provided
	Printable printable;
}