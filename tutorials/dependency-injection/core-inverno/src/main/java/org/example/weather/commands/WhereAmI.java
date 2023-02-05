package org.example.weather.commands;

import org.example.weather.WeatherApplication;
import org.example.weather.data.Location;
import org.example.weather.data.LocationLookupException;
import org.example.weather.interfaces.ILocationProvider;

/**
 * Command to show current location.
 */
public class WhereAmI extends AbstractCommand {
	private final ILocationProvider locationProvider;

	public WhereAmI(ILocationProvider locationProvider) {
		super("where");
		this.locationProvider = locationProvider;
	}

	@Override
	public void handleCommand(WeatherApplication context, String[] args) {
		try {
			Location location = locationProvider.currentLocation();
			System.out.println(location);
		} catch (LocationLookupException ex) {
			System.err.println("Failed to determine current location");
		}
	}
}
