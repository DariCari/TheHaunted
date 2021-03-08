package de.daricari.thehaunted.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import de.daricari.thehaunted.TheHaunted;

public class WorldManager 
{
	private TheHaunted plugin;
	
	private static List<World> gameWorlds;
	
	
	public WorldManager(TheHaunted plugin) {
		this.plugin = plugin;
		gameWorlds = new ArrayList<World>();
	}
	
	public Collection<Player> getOnlinePlayers()
	{
		Collection<Player> players = new ArrayList<Player>();
		gameWorlds.forEach(world -> {
			world.getPlayers().forEach(player -> {
				players.add(player);
			});
		});
		
		return players;
		
	}
	
	public void loadWorlds()
	{
		String w = plugin.getConfig().getString("worlds.worldName");
		
		if(!plugin.getConfig().getBoolean("worlds.ownWorld"))
			Bukkit.getWorlds().forEach(world -> {
				
				if(!gameWorlds.contains(world))
					addGameWorld(world);
				
			});
		
		else
		{
			if(Bukkit.getWorld(w) != null)
			{
				addGameWorld(Bukkit.getWorld(w));
				
				TheHaunted.getSpawnLocations().forEach(l -> {
					Location loc = LocationManager.fromString(l);
					if(!loc.getWorld().equals(Bukkit.getWorld(w)))
						throw new IllegalArgumentException("The spawn locations do not match the set world!");
				});
				TheHaunted.getPageLocations().forEach(l -> {
					Location loc = LocationManager.fromString(l);
					if(!loc.getWorld().equals(Bukkit.getWorld(w)))
						throw new IllegalArgumentException("The page locations do not match the set world!");
				});
			}
			else
			{
				throw new NullPointerException("Could not find world: " + w);
			}
		}
	}

	private void addGameWorld(World world) {
		gameWorlds.add(world);
		
	}
	
}
