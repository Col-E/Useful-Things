package org.example.weather;

import io.jbock.simple.Component;
import io.jbock.simple.Provides;
import org.example.weather.impl.*;
import org.example.weather.interfaces.*;

/**
 * Manual component implementation.
 */
@Component
public interface WeatherComponent {
	@Component.Factory
	interface Factory {
		WeatherComponent create();
	}

	@Provides
	static IWeatherProvider provideWeatherProvider() {
		return new WeatherProvider();
	}

	@Provides
	static INetworkAccessor provideNetworkAccessor() {
		return new NetworkAccessor();
	}

	@Provides
	static ILocationProvider provideLocationProvider(INetworkAccessor accessor) {
		return new LocationProvider(accessor);
	}

	@Provides
	static ICommandLoader provideCommandLoader(ILocationProvider locationProvider, IWeatherProvider weatherProvider) {
		return new CommandLoader(locationProvider, weatherProvider);
	}

	@Provides
	static ICommandManager provideCommandManager(ICommandLoader commandLoader) {
		return new CommandManager(commandLoader);
	}

	WeatherApplication application();
}
