package org.example.weather.impl;

import org.example.weather.WeatherApplication;
import org.example.weather.commands.AbstractCommand;
import org.example.weather.commands.ICommand;
import org.example.weather.interfaces.ICommandLoader;
import org.example.weather.interfaces.ICommandManager;
import org.jvnet.hk2.annotations.Service;

import javax.inject.Inject;
import java.util.*;

/**
 * Handles command instances and usage.
 */
@Service
public class CommandManager implements ICommandManager {
	private final Map<String, ICommand> commands = new HashMap<>();

	@Inject
	public CommandManager(ICommandLoader commandLoader) {
		Set<ICommand> loadedCommands = commandLoader.load();
		for (ICommand command : loadedCommands) {
			String name = command.name();
			commands.put(name, command);
		}
		// Internal command to list other commands
		commands.put("help", new AbstractCommand("help") {
			@Override
			public void handleCommand(WeatherApplication context, String[] args) {
				List<ICommand> sortedCommands = new ArrayList<>(loadedCommands);
				sortedCommands.sort(Comparator.comparing(ICommand::name));
				System.out.println("Available commands:");
				for (ICommand command : sortedCommands) {
					System.out.println(" - " + command.name());
				}
			}
		});
	}

	@Override
	public Set<ICommand> commands() {
		return new HashSet<>(commands.values());
	}

	@Override
	public ICommand get(String name) {
		return commands.get(name);
	}
}
