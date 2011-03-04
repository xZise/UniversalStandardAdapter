package de.xzise.usa.adapters.permission;

import org.bukkit.command.CommandSender;

public interface PermissionDataAdapter extends PermissionAdapter {

	int getPermissionInteger(CommandSender user, String name);
	double getPermissionDouble(CommandSender user, String name);
	String getPermissionString(CommandSender user, String name);
	boolean getPermissionBoolean(CommandSender user, String name);
	
}