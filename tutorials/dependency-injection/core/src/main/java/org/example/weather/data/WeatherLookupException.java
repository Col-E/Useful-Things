package org.example.weather.data;

/**
 * Exception for {@link org.example.weather.interfaces.IWeatherProvider} failures.
 */
public class WeatherLookupException extends Exception {
	public WeatherLookupException(String message) {
		super(message, null);
	}

	public WeatherLookupException(Exception cause) {
		super(null, cause);
	}

	public WeatherLookupException(String message, Exception cause) {
		super(message, cause);
	}
}
