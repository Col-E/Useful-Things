package org.example.weather;

import io.inverno.core.annotation.Bean;
import org.example.weather.commands.ICommand;
import org.example.weather.interfaces.ICommandManager;

import java.util.Arrays;
import java.util.Scanner;

/**
 * Dummy command line application to tell you the weather.
 */
@Bean
public class WeatherApplication implements Runnable {
	private static final String[] EMPTY_ARGS = new String[0];
	private static final Scanner scanner = new Scanner(System.in);
	private final ICommandManager commandManager;

	public WeatherApplication(ICommandManager commandManager) {
		this.commandManager = commandManager;
	}

	@Override
	public void run() {
		while (scanner.hasNext()) {
			// Parse input
			String line = scanner.nextLine();
			String[] split = line.split("\\s+");
			String commandName = split[0];
			String[] commandArgs = split.length == 1 ?
					EMPTY_ARGS :
					Arrays.copyOfRange(split, 1, split.length);
			// Get command and execute
			ICommand command = commandManager.get(commandName.toLowerCase());
			if (command == null) {
				System.err.println("Unknown command '" + commandName + "'");
				continue;
			}
			try {
				command.handleCommand(this, commandArgs);
			} catch (IllegalArgumentException ex) {
				System.err.println(ex.getMessage());
			}
		}
	}
}
