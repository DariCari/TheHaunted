package de.daricari.thehaunted;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import de.daricari.thehaunted.cmd.HauntedCommand;
import de.daricari.thehaunted.cmd.StartCommand;
import de.daricari.thehaunted.files.DataManager;
import de.daricari.thehaunted.game.HauntedGame;
import de.daricari.thehaunted.game.GameListener;
import de.daricari.thehaunted.player.PlayerListener;
import de.daricari.thehaunted.util.ScoreboardManager;
import de.daricari.thehaunted.util.WorldManager;

public class TheHaunted extends JavaPlugin
{
	private static DataManager yamlLocations;
	
	private static WorldManager worldManager;
	
	private static ScoreboardManager scoreManager;
	
	private static List<String> pageLocations = new ArrayList<String>();
	private static List<String> spawnLocations = new ArrayList<String>();
	private static Location swordLocation = null;
	
	@Override
	public void onEnable() 
	{
		checkVersion();
		
		saveDefaultConfig();
		loadLocations();
		
		worldManager = new WorldManager(this);
		worldManager.loadWorlds();
		
		getCommand("thehaunted").setExecutor(new HauntedCommand());
		getCommand("thehaunted").setTabCompleter(new HauntedCommand());
		getCommand("thehaunted").setUsage(HauntedCommand.getUsage());
		
		getCommand("start").setExecutor(new StartCommand());
		
		getServer().getPluginManager().registerEvents(new GameListener(), this);
		getServer().getPluginManager().registerEvents(new PlayerListener(), this);
	}
	
	@Override
	public void onDisable() 
	{
		saveLocations();
		
		if(HauntedGame.hauntedGame != null || HauntedGame.isActiveGame())
			HauntedGame.hauntedGame.endGame(true);
	}
	
	/**Checks the version and will cause an Exception if the version is not compatible**/
	private void checkVersion()
	{
		String version = getServer().getVersion();
		
		if(version.contains("Paper"))
		{
			int pVersion = Integer.parseInt(version.split("-")[2].split(" ")[0]);
			if(pVersion >= 498)
				return;
			else
			{
				getLogger().log(Level.SEVERE, "This plugin can only run on Paper 498 or later and might not function properly! - Current version: Paper " + pVersion);
			}
		}
		else if(version.contains("Spigot"))
		{
			getLogger().log(Level.INFO, "This plugin is recommended to run on Paper - Download at https://papermc.io/downloads#Paper-1.16");
		}
		else
		{
			getLogger().log(Level.SEVERE, "This plugin can only run on Paper 498+ or Spigot!");
			getServer().getPluginManager().disablePlugin(this);
		}
			
	}
	
	public static void startGame(CommandSender initiator)
	{
		try {
			HauntedGame.hauntedGame = new HauntedGame();
		}catch(Exception ex)
		{
			if(TheHaunted.getPlugin(TheHaunted.class).getConfig().getBoolean("general.errorLogging"))
				ex.printStackTrace();
			
			TheHaunted.sendPluginMessage(initiator, "An error occured while trying to start the game!");
			TheHaunted.sendPluginMessage(initiator, "&cError: " + ex.getMessage());
			TheHaunted.sendPluginMessage(initiator, "&cPlease check logs for more information.");
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
	
	public static WorldManager getWorldManager() {
		return worldManager;
	}
	
	public static ScoreboardManager getScoreManager() {
		return scoreManager;
	}
	public static void initScoreManager() {
		scoreManager = new ScoreboardManager(TheHaunted.getPlugin(TheHaunted.class));
	}

	public static void sendPluginMessage(final CommandSender player, String message)
	{
		player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8[&5TheHaunted&8]&3 " + message));
	}
	
	
}
