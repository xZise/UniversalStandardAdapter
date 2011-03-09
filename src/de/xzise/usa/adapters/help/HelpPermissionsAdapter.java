package de.xzise.usa.adapters.help;

/**
 * This adapter allows to register help information for a command. This adapter
 * based on the Help plugin from tkelly.
 * 
 * @author Fabian Neundorf
 */
public interface HelpPermissionsAdapter {

	/**
	 * Registers the help description of the command. This help will be visible
	 * on the main page, if at least one permission is granted.
	 * 
	 * @param command
	 *            Help data for an command.
	 * @param permissions
	 *            If the user has at least one permission this will be shown.
	 * @return If the help could be registered.
	 */
	boolean registerCommand(CommandHelp command, String... permissions);

}
