package org.example.weather;

import com.vanillasource.jaywire.standalone.StandaloneModule;
import org.example.weather.impl.*;
import org.example.weather.interfaces.*;

public class WeatherModule extends StandaloneModule {
	public INetworkAccessor networkAccessor() {
		return new NetworkAccessor();
	}

	public IWeatherProvider weatherProvider() {
		return singleton(WeatherProvider::new);
	}

	public ILocationProvider locationProvider() {
		return singleton(() -> new LocationProvider(networkAccessor()));
	}

	public ICommandLoader commandLoader() {
		return singleton(() -> new CommandLoader(locationProvider(), weatherProvider()));
	}

	public ICommandManager commandManager() {
		return singleton(() -> new CommandManager(commandLoader()));
	}

	public WeatherApplication application() {
		// JayWire provides no automatic parameter calling by design.
		// Instead, we restructure the module (compared to other implementations)
		// to take no-parameters and call the other provider methods defined in this module.
		// This is the intended design for JayWire usage.
		//
		// All return values are wrapped in 'singleton(...)' which is like Map's computeIfAbsent.
		// A lazy-lookup that only gets run a *single* time.
		return singleton(() -> new WeatherApplication(commandManager()));
	}
}
