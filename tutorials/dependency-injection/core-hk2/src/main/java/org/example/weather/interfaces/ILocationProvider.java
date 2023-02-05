package org.example.weather.interfaces;

import org.example.weather.data.Location;
import org.example.weather.data.LocationLookupException;
import org.jvnet.hk2.annotations.Contract;

/**
 * Tool outline to provide location lookup.
 */
@Contract
public interface ILocationProvider {
	/**
	 * @return Location of user upon request.
	 *
	 * @throws LocationLookupException
	 * 		When the current location could not be found.
	 */
	Location currentLocation() throws LocationLookupException;
}
