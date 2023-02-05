package org.example.weather.impl;

import io.inverno.core.annotation.Bean;
import org.example.weather.commands.CurrentWeather;
import org.example.weather.commands.ICommand;
import org.example.weather.commands.RemoteWeather;
import org.example.weather.commands.WhereAmI;
import org.example.weather.interfaces.ICommandLoader;
import org.example.weather.interfaces.ILocationProvider;
import org.example.weather.interfaces.IWeatherProvider;

import java.util.HashSet;
import java.util.Set;

/**
 * Supplies new command instances.
 */
@Bean
public class CommandLoader implements ICommandLoader {
	private final Set<ICommand> commands = new HashSet<>();

	public CommandLoader(ILocationProvider locationProvider,
						 IWeatherProvider weatherProvider) {
		// Commands are injectable, but with basic JSR-330 there is no dynamic way to instantiate them all in one pass.
		// So we use this loader type to request the dependencies in this constructor, then pass them to the commands.
		commands.add(new WhereAmI(locationProvider));
		commands.add(new CurrentWeather(weatherProvider, locationProvider));
		commands.add(new RemoteWeather(weatherProvider));
	}

	@Override
	public Set<ICommand> load() {
		return commands;
	}
}
