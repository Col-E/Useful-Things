package org.example.weather.commands;

import org.example.weather.WeatherApplication;

/**
 * Outline of a runnable task, taking in the executing context and arguments.
 */
public abstract class AbstractCommand implements ICommand {
	private final String name;

	public AbstractCommand(String name) {
		this.name = name;
	}

	/**
	 * @return Command name.
	 */
	public String name() {
		return name;
	}

	/**
	 * @param context
	 * 		Application context to pull values from if necessary.
	 * @param args
	 * 		Arguments.
	 */
	public abstract void handleCommand(WeatherApplication context, String[] args);
}
