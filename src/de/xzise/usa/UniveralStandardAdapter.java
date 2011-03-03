package de.xzise.usa;

import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import de.xzise.usa.adapters.Adapter;
import de.xzise.usa.adapters.EconomyAdapter;
import de.xzise.usa.adapters.PermissionAdapter;

public class UniveralStandardAdapter extends JavaPlugin {
	
	private static final Adapter FAIL = new Adapter() {

		@Override
		public Plugin getPlugin() {
			return null;
		}
		
	};
	
	private PermissionAdapter permissions;
	private EconomyAdapter economy;

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
			Adapter adapter = this.getAdapter(args[0]);
			if (adapter == FAIL) {
				sender.sendMessage(ChatColor.RED + "Incorrect adapter name.");
			} else if (adapter == null) {
				sender.sendMessage("Adapter for " + ChatColor.GREEN + args[0] + ChatColor.WHITE + " is " + ChatColor.RED + "not registered.");
			} else {
				sender.sendMessage("Adapter is registered to: " + ChatColor.GREEN + adapter.getPlugin().getDescription().getName() + ChatColor.WHITE + " version " + ChatColor.GREEN + adapter.getPlugin().getDescription().getVersion());
			}
			return true;
		} else {
			return false;
		}
	}
	
	public boolean registerAdapter(Adapter adapter) {
		if (adapter instanceof PermissionAdapter) {
			if (this.permissions != null) {
				return false;
			}
			this.permissions = (PermissionAdapter) adapter;
		} else if (adapter instanceof EconomyAdapter) {
			if (this.economy != null) {
				return false;
			}
			this.economy = (EconomyAdapter) adapter;
		} else {
			return false;
		}
		return true;
	}
	
	public Adapter getAdapter(String name) {
		if (name.equals("permissions")) {
			return this.permissions;
		} else if (name.equals("economy")) {
			return this.economy;
		} else {
			return FAIL;
		}
	}
	
	public PermissionAdapter getPermissionAdapter() {
		return this.permissions;
	}
	
	public EconomyAdapter getEconomyAdapter() {
		return this.economy;
	}

}
