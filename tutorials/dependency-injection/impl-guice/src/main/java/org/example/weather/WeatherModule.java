package org.example.weather;

import com.google.inject.AbstractModule;
import org.example.weather.impl.*;
import org.example.weather.interfaces.*;

public class WeatherModule extends AbstractModule {
	@Override
	protected void configure() {
		bind(INetworkAccessor.class).to(NetworkAccessor.class);
		bind(IWeatherProvider.class).to(WeatherProvider.class);
		bind(ILocationProvider.class).to(LocationProvider.class);
		bind(ICommandLoader.class).to(CommandLoader.class);
		bind(ICommandManager.class).to(CommandManager.class);
	}
}
