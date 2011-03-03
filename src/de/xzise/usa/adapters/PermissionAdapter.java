package de.xzise.usa.adapters;

import org.bukkit.command.CommandSender;

public interface PermissionAdapter extends Adapter {

	boolean hasPermission(CommandSender sender);
	
	String getGroup(String name);
	
}
