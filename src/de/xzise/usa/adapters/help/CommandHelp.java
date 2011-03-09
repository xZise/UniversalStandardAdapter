package de.xzise.usa.adapters.help;

import org.bukkit.plugin.Plugin;

public interface CommandHelp {
	
	Plugin getPlugin();
	
	String getCommand();
	
	String getShortDescription();
	
	String[] getLongDescription();
	
	boolean showInMain();

}
