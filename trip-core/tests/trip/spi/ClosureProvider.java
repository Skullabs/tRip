package trip.spi;

@Singleton
@Period
public class ClosureProvider implements ProducerFactory<Closure> {

	@Override
	public Closure provide( ProviderContext context ) {
		return new PeriodClosure();
	}

	class PeriodClosure implements Closure {
		@Override
		public Character getSentenceClosureChar() {
			return Closure.PERIOD;
		}
	}
}
