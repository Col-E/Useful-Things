package org.example.weather;

import dagger.Module;
import dagger.Provides;
import org.example.weather.impl.*;
import org.example.weather.interfaces.*;

@Module
public class WeatherModule {
	@Provides
	public INetworkAccessor networkAccessor() {
		return new NetworkAccessor();
	}

	@Provides
	public IWeatherProvider weatherProvider() {
		return new WeatherProvider();
	}

	@Provides
	public ILocationProvider locationProvider(INetworkAccessor accessor) {
		// Parameter will automatically pull from other provider method
		return new LocationProvider(accessor);
	}

	@Provides
	public ICommandLoader commandLoader(ILocationProvider locationProvider,
										IWeatherProvider weatherProvider) {
		// Parameters will automatically pull from other provider methods
		return new CommandLoader(locationProvider, weatherProvider);
	}

	@Provides
	public ICommandManager commandManager(ICommandLoader commandLoader) {
		// Parameter will automatically pull from other provider method
		return new CommandManager(commandLoader);
	}
}
