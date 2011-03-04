package de.xzise.usa.adapters.permission;

public interface PermissionDataAdapter extends PermissionAdapter {

	int getPermissionInteger(String user, String name);
	double getPermissionDouble(String user, String name);
	String getPermissionString(String user, String name);
	boolean getPermissionBoolean(String user, String name);
	
}
