package de.xzise.usa.adapters.permission;

import de.xzise.usa.adapters.Adapter;

public interface PermissionDataAdapter extends Adapter {

	int getPermissionInteger(String user, String world, String name);
	double getPermissionDouble(String user, String world, String name);
	String getPermissionString(String user, String world, String name);
	boolean getPermissionBoolean(String user, String world, String name);
	
}