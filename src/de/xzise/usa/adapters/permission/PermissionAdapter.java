package de.xzise.usa.adapters.permission;

import org.bukkit.command.CommandSender;

import de.xzise.usa.adapters.Adapter;

public interface PermissionAdapter extends Adapter {

	boolean hasPermission(CommandSender sender, String name);
	
	String getGroup(String name);
	
}
