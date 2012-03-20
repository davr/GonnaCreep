package org.davr;

import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.*;
import java.lang.*;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.*;
import org.bukkit.*;
import org.bukkit.event.*;
import org.bukkit.event.player.*;
import org.bukkit.entity.*;

/**
* GonnaCreep 1.0
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Public License as published by
* the Free Software Foundation, either version 2 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU Permissions Public License for more details.
*
* You should have received a copy of the GNU Public License
* along with this program. If not, see <http://www.gnu.org/licenses/>.
*/

/**
* This plugin was written for Craftbukkit
* @author davr
*/

public class GonnaCreep extends JavaPlugin {
	public static final Logger console = Logger.getLogger("Minecraft");
//	private final GonnaCreepPlayerListener playerListener = new GonnaCreepPlayerListener();
//	public static PermissionHandler permissionHandler = null;
//	public static PermissionManager permissionExHandler = null;

	private class GonnaCreepListener implements Listener {
		private Random rand = new Random();
		private GonnaCreep plugin;

		public GonnaCreepListener(GonnaCreep plugin) {
			this.plugin = plugin;
		}

		@EventHandler(priority = EventPriority.LOW)
		public void onPlayerMove(PlayerMoveEvent event) {
			if(rand.nextInt(plugin.chance) == 0) {
				Location loc = event.getTo().clone();
				if(loc.getWorld().getName().equals(plugin.badworld)) {
					loc.setX(loc.getX() + rand.nextInt(plugin.dist*2) - plugin.dist);
					loc.setZ(loc.getZ() + rand.nextInt(plugin.dist*2) - plugin.dist);
					loc.getWorld().spawnCreature(loc, CreatureType.CREEPER);
					console.info(event.getPlayer().getName()+" creeped!");
					event.getPlayer().sendMessage("SSSSsssssssss....");

				}
			}
		}
	}
	
	@Override
	public void onDisable() {
		console.info("[GonnaCreep] version " + this.getDescription().getVersion() + " disabled.");

		saveSets();
	}

	@Override
	public void onEnable() {		
		PluginManager pm = this.getServer().getPluginManager();	
		pm.registerEvents(new GonnaCreepListener(this), this);
		console.info("[GonnaCreep] version " + this.getDescription().getVersion() + " enabled.");
		
		loadSets();
	}

	public int chance, dist;
	public String badworld;

	private void loadSets() {
		reloadConfig();
		chance = getConfig().getInt("mob-chance", 10000);
		dist = getConfig().getInt("mob-dist", 10);
		badworld = getConfig().getString("badworld", "desert");
	}

	private void saveSets() {
		getConfig().set("mob-chance", chance);
		getConfig().set("mob-dist", dist);
		getConfig().set("badworld", badworld);
		saveConfig();
	}


	private void sendHelp(CommandSender sender) {
		sender.sendMessage("Valid subcommands: ");
		sender.sendMessage("  info");
		sender.sendMessage("  chance <new chance>");
		sender.sendMessage("  dist <new dist>");
		sender.sendMessage("  badworld <new badworld>");
	}
	

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
		if(sender instanceof Player) {
			Player player = (Player) sender;
			if(!player.isOp()) {
				if(!player.hasPermission("gcreep.admin")) {
					player.sendMessage("You do not have permission to do that.");
					return true;
				}
			}
		}


		if(cmd.getName().equalsIgnoreCase("gcreep")) {
			if(args.length < 2 || args[0].equals("help") || args[0].equals("?")) {
				sendHelp(sender);
				return true;
			}

			if(args[0].equals("chance")) {
				try {
					int newchance = Integer.parseInt(args[1]);
					if(newchance >= -1)
					{
						chance = newchance;
						saveSets();
					}
				} catch(Exception e){
				}
				return true;
			}

			if(args[0].equals("dist")) {
				try {
					int newdist = Integer.parseInt(args[1]);
					if(newdist >= -1)
					{
						dist = newdist;
						saveSets();
					}
				} catch(Exception e){
				}
				return true;
			}

			if(args[0].equals("badworld")) {
				try {
					badworld = args[1];
					saveSets();
				} catch(Exception e){
				}
				return true;
			}

			if(args[0].equals("info")) {
				sender.sendMessage("Chance="+chance);
				sender.sendMessage("Dist="+dist);
				sender.sendMessage("Badworld="+badworld);
				return true;
			}

			sender.sendMessage("Unknown subcommand. try help.");
			return true;
		}

		return false;
	}

}
