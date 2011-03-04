package de.xzise.usa;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import de.xzise.usa.adapters.Adapter;
import de.xzise.usa.adapters.EconomyAdapter;
import de.xzise.usa.adapters.PermissionAdapter;
import de.xzise.usa.adapters.PermissionDataAdapter;

public class UniveralStandardAdapter extends JavaPlugin {
	
	private static final Map<String, Class<? extends Adapter>> ADAPTER_NAMES = new HashMap<String, Class<? extends Adapter>>();
	private static final Map<Class<? extends Adapter>, Class<? extends Adapter>> ADAPTER_BASE_CLASSES = new HashMap<Class<? extends Adapter>, Class<? extends Adapter>>();
	
	static {
		// Register all names here
		ADAPTER_NAMES.put("permissions", PermissionAdapter.class);
		ADAPTER_NAMES.put("economy", EconomyAdapter.class);
		
		// Register all base classes here
		ADAPTER_BASE_CLASSES.put(PermissionDataAdapter.class, PermissionAdapter.class);
	}
	
	private final Map<Class<? extends Adapter>, Adapter> adapters = new HashMap<Class<? extends Adapter>, Adapter>();
	
	@Override
	public void onDisable() {
		Logger.getLogger("Minecraft").info(this.getDescription().getName() + " disabled.");
	}

	@Override
	public void onEnable() {
		Logger.getLogger("Minecraft").info(this.getDescription().getName() + " enabled (Version: " + this.getDescription().getVersion() + ").");
	}
	
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (args.length == 1) {
			if (ADAPTER_NAMES.containsKey(args[0])) {
				Adapter adapter = this.getAdapter(args[0]);
				if (adapter == null) {
					sender.sendMessage("Adapter for " + ChatColor.GREEN + args[0] + ChatColor.WHITE + " is " + ChatColor.RED + "not registered.");
				} else {
					sender.sendMessage("Adapter is registered to: " + ChatColor.GREEN + adapter.getPlugin().getDescription().getName() + ChatColor.WHITE + " version " + ChatColor.GREEN + adapter.getPlugin().getDescription().getVersion());
				}
			} else {
				sender.sendMessage(ChatColor.RED + "Incorrect adapter name.");
			}
			return true;
		} else {
			return false;
		}
	}
	
	private Class<? extends Adapter> getBaseClass(Class<? extends Adapter> adapterClass) {
		Class<? extends Adapter> baseClass = ADAPTER_BASE_CLASSES.get(adapterClass);
		if (baseClass == null) {
			baseClass = adapterClass;
		}
		return baseClass;
	}
	
	/**
	 * Registers an adapter. Returns false if an adapter is already registered.
	 * @param adapter New adapter.
	 * @return If an adapter for this type was already registered.
	 */
	public boolean registerAdapter(Adapter adapter) {
		// Get base class. If there is no base class the adapter itself is the base class.
		Class<? extends Adapter> baseClass = this.getBaseClass(adapter.getClass());
		
		if (!ADAPTER_NAMES.containsValue(baseClass)) {
			String baseClassName = "";
			if (!adapter.getClass().equals(baseClass)) {
				baseClassName = " (Baseclass: " + baseClass.getName() + ")";
			}
			Logger.getLogger("Minecraft").warning("[" + this.getDescription().getName() + "] Register an adapter without a name: " + adapter.getClass().getName() + baseClassName);
		}
		
		Adapter a = this.adapters.get(baseClass);
		if (a == null) {
			this.adapters.put(baseClass, adapter);
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Gets an adapter for a name.
	 * @param name Adapter name.
	 * @return The adapter.
	 */
	public Adapter getAdapter(String name) {
		Class<? extends Adapter> adapterClass = ADAPTER_NAMES.get(name);
		if (adapterClass != null) {
			return this.adapters.get(adapterClass);
		} else {
			return null;
		}
	}
	
	/**
	 * Gets an adapter for a desired adapter class.
	 * @param adapterClass The class of the desired adapter.
	 * @return The adapter.
	 */
	@SuppressWarnings("unchecked")
	public <T extends Adapter> T getAdapter(Class<T> adapterClass) {
		Adapter a = this.adapters.get(this.getBaseClass(adapterClass));
		if (adapterClass.getClass().isAssignableFrom(a.getClass())) {
			return (T) a;
		} else {
			return null;
		}
	}
}
