package org.example.weather.data;

/**
 * Exception for {@link org.example.weather.interfaces.ILocationProvider} failures.
 */
public class LocationLookupException extends Exception {
	public LocationLookupException(String message) {
		super(message, null);
	}

	public LocationLookupException(Exception cause) {
		super(null, cause);
	}

	public LocationLookupException(String message, Exception cause) {
		super(message, cause);
	}
}
