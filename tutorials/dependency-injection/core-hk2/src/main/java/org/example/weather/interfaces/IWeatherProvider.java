package org.example.weather.interfaces;

import org.example.weather.data.Location;
import org.example.weather.data.Weather;
import org.example.weather.data.WeatherLookupException;
import org.jvnet.hk2.annotations.Contract;

import javax.annotation.Nonnull;

/**
 * Tool outline to provide weather lookup by location.
 */
@Contract
public interface IWeatherProvider {
	/**
	 * @param location
	 * 		Location coordinates.
	 *
	 * @return Weather information for the requested location.
	 *
	 * @throws WeatherLookupException
	 * 		When the weather location could not be found for the given location.
	 */
	Weather forLocation(@Nonnull Location location) throws WeatherLookupException;
}
