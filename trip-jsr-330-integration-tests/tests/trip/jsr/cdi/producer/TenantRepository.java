package trip.jsr.cdi.producer;

import javax.inject.Inject;
import javax.inject.Singleton;

import lombok.Getter;

@Getter
@Singleton
public class TenantRepository {

	@Inject
	Database database;

}
