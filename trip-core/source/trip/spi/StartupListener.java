package trip.spi;

public interface StartupListener {

	default void beforeProducersReady( final ServiceProvider provider ){}

	void onStartup( final ServiceProvider provider );
}
