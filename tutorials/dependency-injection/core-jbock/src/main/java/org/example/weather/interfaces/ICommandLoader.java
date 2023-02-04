package org.example.weather.interfaces;

import org.example.weather.commands.ICommand;

import java.util.Set;

/**
 * Responsible for substantiating command implementations.
 */
public interface ICommandLoader {
	Set<ICommand> load();
}
