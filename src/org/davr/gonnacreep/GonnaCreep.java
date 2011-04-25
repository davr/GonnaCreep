package com.bukkit.vicwhiten.moblimiter;

import java.util.ArrayList;
import java.util.List;
import java.lang.Runnable;

//import org.anjocaido.groupmanager.GroupManager;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Creature;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.util.config.Configuration;
import org.bukkit.entity.Squid;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Animals;
import org.bukkit.entity.CreatureType;
import org.bukkit.Location;
import java.util.Random;
import java.util.Queue;
import java.util.LinkedList;

//import com.nijikokun.bukkit.Permissions.Permissions;



public class GonnaCreep extends JavaPlugin
{

	public GonnaCreepPlayerListener pls = new GonnaCreepPlayerListener(this);
	public int chance;
	public int dist;
	public Configuration config;
//	public GroupManager gm;
//	public Permissions perm;
	Random rand = new Random();

	public void onDisable()
	{
		PluginDescriptionFile pdfFile = this.getDescription();
        System.out.println(pdfFile.getName()+" version "+pdfFile.getVersion()+" is disabled!");
	}

	public void onEnable()
	{

		PluginManager pm = getServer().getPluginManager();
		
/*		Plugin p = this.getServer().getPluginManager().getPlugin("GroupManager");
		if (p != null) {
			if (!this.getServer().getPluginManager().isPluginEnabled(p)) {
				this.getServer().getPluginManager().enablePlugin(p);
			}
			gm = (GroupManager) p;
		} 
		
		p = this.getServer().getPluginManager().getPlugin("Permissions");
		if (p != null) {
			if (!this.getServer().getPluginManager().isPluginEnabled(p)) {
				this.getServer().getPluginManager().enablePlugin(p);
			}
			perm = (Permissions) p;
		} */
 
		config = this.getConfiguration();
		setupChance();
		pm.registerEvent(Event.Type.PLAYER_MOVE, this.pls, Event.Priority.Normal, this);
		getCommand("gonnacreep").setExecutor(new GonnaCreepCommand(this));
		
        PluginDescriptionFile pdfFile = this.getDescription();
        System.out.println(pdfFile.getName()+" version "+pdfFile.getVersion()+" is enabled!");
	}
	
	public int getMobAmount(World world)
	{
		int sum=0;
		List<LivingEntity> mobs = world.getLivingEntities();
		for(int j=0; j<mobs.size(); j++)
		{
			if(mobs.get(j) instanceof Squid)
				sum++;
		}
		return sum;
	}
	
	public void purgeMobs(World world)
	{
		List<LivingEntity> mobs = world.getLivingEntities();
		for(int j=0; j<mobs.size(); j++)
		{
			if(Creature.class.isInstance(mobs.get(j)))
			{
				LivingEntity mob = mobs.remove(j);
				if(mob instanceof Squid) {
					mob.remove();
					j--;
				}
			}
		}
	}
	
	public void setupChance()
	{
		config.load();

		chance = config.getInt("mob-chance", 10000);
		config.setProperty("mob-chance", chance);

		dist = config.getInt("mob-dist", 10);
		config.setProperty("mob-dist", dist);

		config.save();
	}
	
	public void setChance(int newChance)
	{
		chance = newChance;
		config.setProperty("mob-chance", newChance);
		config.save();
	}	

	public void setDist(int newDist)
	{
		dist = newDist;
		config.setProperty("mob-dist", newDist);
		config.save();
	}
	
	public int getChance()
	{
		return chance;
	}
	
    public boolean checkPermission(Player player, String permission)
    {
    	if(player.isOp())
    	{
    		return true;
    	}else return false;
    }

	private class GonnaCreepPlayerListener extends PlayerListener {

		private GonnaCreep plugin;
		public Queue<Location> spawnq;

		public GonnaCreepPlayerListener(GonnaCreep plug) {
			spawnq = new LinkedList<Location>();
			plugin = plug;
		}

		public void checkQueue() {
			int n=0;
			while(!spawnq.isEmpty()) {
				Location loc = spawnq.remove();
				loc.getWorld().spawnCreature(loc, CreatureType.CREEPER);
				n++;
			}
		}

		public void onPlayerMove(PlayerMoveEvent event) {
			if(plugin.rand.nextInt(plugin.chance) == 0) {
				Location loc = event.getTo().clone();
				if(loc.getWorld().getName().equals("desert")) {
					loc.setX(loc.getX() + plugin.rand.nextInt(plugin.dist*2) - plugin.dist);
					loc.setZ(loc.getZ() + plugin.rand.nextInt(plugin.dist*2) - plugin.dist);
					loc.getWorld().spawnCreature(loc, CreatureType.CREEPER);
					System.out.println(event.getPlayer().getName()+" creeped!");
					event.getPlayer().sendMessage("SSSSsssssssss....");
				}
			}
		}
	}
	
	private class GonnaCreepCommand implements CommandExecutor 
	{
	    private final GonnaCreep plugin;

	    public GonnaCreepCommand(GonnaCreep plugin) {
	        this.plugin = plugin;
	    }

	    public boolean onCommand(CommandSender sender, 
	    		Command command, 
	    		String label, String[] args) 
	    {
	    	boolean permission = false;
	    	try{
	    		permission = checkPermission((Player)sender, "moblimiter.setChance");
	    	}catch(Exception E)
	    	{
	    		permission = true;
	    	}
	    	if(!permission)
	    	{
	    		sender.sendMessage(ChatColor.RED + "You do not have the permissions to do this");
	    		return true;
	    	}
	    	if(args.length < 1)
	    	{
	    		return false;
	    	}
	    	//setChance command
	    	if(args.length == 2 && args[0].compareTo("setchance") == 0)
	    	{
	    		try{
	    			int newChance = Integer.parseInt(args[1]);
	    			if(newChance >=-1)
	    			{
	    				plugin.setChance(newChance);
	    				sender.sendMessage("Chance set to " + newChance);
	    				return true;
	    			}else return false;
	    		}catch(Exception e){
	    			return false;
	    		}
	    	}
	    	//max command
	    	if(args.length == 1 && args[0].compareTo("chance") == 0)
	    	{
	    		sender.sendMessage("Chance: " + chance);
	    		return true;
	    	}
	    	return false;
	    }	
	}
	
}
