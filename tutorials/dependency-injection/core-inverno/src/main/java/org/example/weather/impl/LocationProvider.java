package org.example.weather.impl;

import io.inverno.core.annotation.Bean;
import org.example.weather.data.Location;
import org.example.weather.data.LocationLookupException;
import org.example.weather.interfaces.ILocationProvider;
import org.example.weather.interfaces.INetworkAccessor;
import org.example.weather.util.HTTP;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Implementation to lookup location based on IP.
 */
@Bean
public class LocationProvider implements ILocationProvider {
	private static final String API_URL = "https://db-ip.com/";
	private static final Pattern COORDINATE_PATTERN = Pattern.compile(
			"<tr>\\s*<th>\\s*Coordinates\\s*</th>\\s*<td>\\s*" +
					"([\\d.-]+)" + // Group 1: latitude number
					"\\s*,\\s*" +
					"([\\d.-]+)" + // Group 2: longitude number
					"\\s*</td>\\s*</tr>");
	private final INetworkAccessor accessor;
	private Location currentLocation;

	public LocationProvider(INetworkAccessor accessor) {
		this.accessor = accessor;
	}

	@Override
	public Location currentLocation() throws LocationLookupException {
		if (currentLocation == null)
			currentLocation = requestLocation();
		return currentLocation;
	}

	private Location requestLocation() throws LocationLookupException {
		try {
			// Get IP from accessor
			String ip = accessor.externalAddress().getHostAddress();
			// Fetch content from API
			String content = HTTP.readString(API_URL + ip,
					status -> "Location lookup API '" + API_URL + "' yielded status code: " + status);
			Matcher matcher = COORDINATE_PATTERN.matcher(content);
			if (matcher.find()) {
				double latitude = Double.parseDouble(matcher.group(1));
				double longitude = Double.parseDouble(matcher.group(2));
				return new Location(latitude, longitude);
			} else {
				throw new IOException("Location lookup API output format changed! Content could not be parsed.");
			}
		} catch (IOException ex) {
			throw new LocationLookupException(ex);
		}
	}
}
