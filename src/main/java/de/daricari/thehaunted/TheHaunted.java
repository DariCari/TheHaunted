package de.daricari.thehaunted;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import de.daricari.thehaunted.cmd.HauntedCommand;
import de.daricari.thehaunted.cmd.StartCommand;
import de.daricari.thehaunted.files.DataManager;
import de.daricari.thehaunted.game.HauntedGame;
import de.daricari.thehaunted.game.HauntedListener;
import de.daricari.thehaunted.game.HauntedPlayerListener;

public class TheHaunted extends JavaPlugin
{
	private static DataManager yamlLocations;
	
	private static List<String> pageLocations = new ArrayList<String>();
	private static List<String> spawnLocations = new ArrayList<String>();
	private static Location swordLocation = null;
	
	@Override
	public void onEnable() 
	{
		saveDefaultConfig();
		loadLocations();
		
		getCommand("thehaunted").setExecutor(new HauntedCommand());
		getCommand("thehaunted").setTabCompleter(new HauntedCommand());
		getCommand("start").setExecutor(new StartCommand());
		
		getServer().getPluginManager().registerEvents(new HauntedListener(), this);
		getServer().getPluginManager().registerEvents(new HauntedPlayerListener(), this);
	}
	
	@Override
	public void onDisable() 
	{
		saveLocations();
	}
	
	public static void startGame(CommandSender initiator)
	{
		try {
			HauntedGame.hauntedGame = new HauntedGame();
		}catch(Exception ex)
		{
			ex.printStackTrace();
			TheHaunted.sendPluginMessage(initiator, "An error occured while trying to start the game! Please check logs for more informaton.");
			return;
		}
		TheHaunted.sendPluginMessage(initiator, "The game has been started!");
	}
	
	public void loadLocations()
	{
		yamlLocations = new DataManager(this, "data/locations.yml");
		
		//spawnLocations
		for(String s : yamlLocations.getFile().getStringList("spawnLocations"))
		{
			spawnLocations.add(s);
		}
		//pageLocations
		for(String s : yamlLocations.getFile().getStringList("pageLocations"))
		{
			pageLocations.add(s);
		}
		//swordLocation
		swordLocation = yamlLocations.getFile().getLocation("swordLocation");
		
		
	}
	public void saveLocations()
	{
		//spawnLocations
		yamlLocations.getFile().set("spawnLocations", spawnLocations);
		
		//pageLocations
		yamlLocations.getFile().set("pageLocations", pageLocations);
		
		//swordLocation
		yamlLocations.getFile().set("swordLocation", swordLocation);
		
		yamlLocations.save();
	}
	
	public static List<String> getPageLocations()
	{
		return pageLocations;
	}
	public static List<String> getSpawnLocations()
	{
		return spawnLocations;
	}
	public static Location getSwordLocation()
	{
		return swordLocation;
	}
	public static void setSwordLocation(Location loc)
	{
		swordLocation = loc;
	}
	
	public static void sendPluginMessage(final CommandSender player, String message)
	{
		player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8[&5TheHaunted&8]&3 " + message));
	}
	
	
}
