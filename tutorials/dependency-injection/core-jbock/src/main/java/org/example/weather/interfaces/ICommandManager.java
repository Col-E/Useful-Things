package org.example.weather.interfaces;

import org.example.weather.commands.ICommand;

import java.util.Set;

/**
 * Outlines the available commands.
 */
public interface ICommandManager {
	/**
	 * @return All recognized commands.
	 */
	Set<ICommand> commands();

	/**
	 * @param name
	 * 		Command name.
	 *
	 * @return Command implementation.
	 */
	ICommand get(String name);
}
