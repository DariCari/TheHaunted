package de.daricari.thehaunted;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import de.daricari.thehaunted.cmd.HauntedCommand;
import de.daricari.thehaunted.cmd.StartCommand;
import de.daricari.thehaunted.files.DataManager;
import de.daricari.thehaunted.game.HauntedGame;
import de.daricari.thehaunted.game.HauntedGameListener;
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
		checkVersion();
		
		saveDefaultConfig();
		loadLocations();
		
		getCommand("thehaunted").setExecutor(new HauntedCommand());
		getCommand("thehaunted").setTabCompleter(new HauntedCommand());
		getCommand("thehaunted").setUsage(HauntedCommand.getUsage());
		
		getCommand("start").setExecutor(new StartCommand());
		
		getServer().getPluginManager().registerEvents(new HauntedGameListener(), this);
		getServer().getPluginManager().registerEvents(new HauntedPlayerListener(), this);
	}
	
	@Override
	public void onDisable() 
	{
		saveLocations();
	}
	
	/**Checks the version and will cause an Exception if the version is not compatible**/
	private void checkVersion()
	{
		String version = getServer().getVersion();
		if(version.contains("Paper"))
		{
			getLogger().log(Level.INFO, "Make sure you are running at least Paper 498 in order to avoid any issues with this plugin");
			return;
		}
		else if(version.contains("Spigot"))
		{
			getLogger().log(Level.SEVERE, "This plugin can only run on Paper 498 or later! If you still wish to use Spigot, you can download version 1.0 of this plugin at https://github.com/DariCari/TheHaunted/releases/tag/1.0");
			setEnabled(false);
		}
		else
		{
			getLogger().log(Level.SEVERE, "This plugin can only run on Paper 498 or later!");
			setEnabled(false);
		}
			
	}
	
	public static void startGame(CommandSender initiator)
	{
		if(Bukkit.getOnlinePlayers().size() < 2)
		{
			TheHaunted.sendPluginMessage(initiator, "There are not enough players online to start a game!");
			return;
		}
		try {
			HauntedGame.hauntedGame = new HauntedGame();
		}catch(Exception ex)
		{
			ex.printStackTrace();
			TheHaunted.sendPluginMessage(initiator, "&cAn error occured while trying to start the game! Please check logs for more informaton.");
			return;
		}
		TheHaunted.sendPluginMessage(initiator, "The game has been started!");
	}
	
	private void loadLocations()
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
	private void saveLocations()
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
