package de.daricari.thehaunted.util;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import de.daricari.thehaunted.TheHaunted;

public class LocationManager 
{
	private static TheHaunted plugin = TheHaunted.getPlugin(TheHaunted.class);
	//private static Logger logger = plugin.getLogger();
	
	public static boolean addSpawnLocation(Location spawnLoc)
	{
		int x=spawnLoc.getBlockX();
		int y=spawnLoc.getBlockY();
		int z=spawnLoc.getBlockZ();
		List<String> locs = TheHaunted.getSpawnLocations();
		List<Location> locations = new ArrayList<Location>();
		for(String s : locs)
		{
			Location l = fromString(s);
			locations.add(l);
		}
		if(!locations.contains(spawnLoc))
		{
			String spLoc = "X: " + x + " Y: " + y + " Z: " + z + " World: " + spawnLoc.getWorld().getName();
			TheHaunted.getSpawnLocations().add(spLoc);
			return true;
		}
		return false;
	}
	
	public static boolean removeSpawnLocation(Location spawnLoc)
	{
		int x=spawnLoc.getBlockX();
		int y=spawnLoc.getBlockY();
		int z=spawnLoc.getBlockZ();
		List<String> locs = TheHaunted.getSpawnLocations();
		List<Location> locations = new ArrayList<Location>();
		for(String s : locs)
		{
			Location l = fromString(s);
			locations.add(l);
		}
		if(locations.contains(spawnLoc))
		{
			String spLoc = "X: " + x + " Y: " + y + " Z: " + z + " World: " + spawnLoc.getWorld().getName();
			TheHaunted.getSpawnLocations().remove(spLoc);
			return true;
		}
		return false;
	}
	
	public static boolean addPageLocation(Location pLoc, Entity e)
	{
		if(!e.getType().equals(EntityType.ITEM_FRAME))
		{
			return false;
		}
		ItemFrame frame = (ItemFrame) e;
		int x=pLoc.getBlockX();
		int y=pLoc.getBlockY();
		int z=pLoc.getBlockZ();
		List<String> locs = TheHaunted.getPageLocations();
		List<Location> locations = new ArrayList<Location>();
		for(String s : locs)
		{
			Location l = fromString(s);
			locations.add(l);
			//logger.log(Level.INFO, s + " to " + l);
		}
		if(!locations.contains(pLoc))
		{
			String spLoc = "X: " + x + " Y: " + y + " Z: " + z + " World: " + pLoc.getWorld().getName();
			locs.add(spLoc);
			TheHaunted.getPageLocations().add(spLoc);
			
			NamespacedKey foundKey = new NamespacedKey(plugin, "isFound");
			frame.getPersistentDataContainer().set(foundKey, PersistentDataType.INTEGER, 0);
			
			NamespacedKey pageKey = new NamespacedKey(plugin, "isPage");
			frame.getPersistentDataContainer().set(pageKey, PersistentDataType.INTEGER, 1);
			
			frame.setItem(new ItemStack(Material.PAPER));
			return true;
		}
		return false;
		
	}
	
	public static boolean removePageLocation(Location pLoc, Entity e)
	{
		if(!e.getType().equals(EntityType.ITEM_FRAME))
		{
			return false;
		}
		ItemFrame frame = (ItemFrame) e;
		int x=pLoc.getBlockX();
		int y=pLoc.getBlockY();
		int z=pLoc.getBlockZ();
		List<String> locs = TheHaunted.getPageLocations();
		String spLoc = "X: " + x + " Y: " + y + " Z: " + z + " World: " + pLoc.getWorld().getName();
		if(locs.contains(spLoc))
		{
			locs.remove(spLoc);
			TheHaunted.getPageLocations().remove(spLoc);
			
			NamespacedKey key = new NamespacedKey(plugin, "isFound");
			frame.getPersistentDataContainer().remove(key);
			NamespacedKey pageKey = new NamespacedKey(plugin, "isPage");
			frame.getPersistentDataContainer().set(pageKey, PersistentDataType.INTEGER, 0);
			
			frame.setItem(new ItemStack(Material.AIR));
			
			return true;
		}
		return false;
		
	}
	
	public static void setSwordLocation(Location loc)
	{
		TheHaunted.setSwordLocation(loc);
	}
	
	public static Location fromString(String loc)
	{
		int a = 1;
		
		int x = 0;
		int y = 0;
		int z = 0;
		World w = null;
		
		for(String s : loc.split(" "))
		{
			if(!s.equals("X:") && !s.equals("Y:") && !s.equals("Z:") && !s.equals("World:"))
			{
				if(a==1)
					x = Integer.parseInt(s);
				else if(a==2)
					y = Integer.parseInt(s);
				else if(a==3)
					z = Integer.parseInt(s);
				else if(a==4)
					w = Bukkit.getWorld(s);
				a++;
			}
		}
		Location l = new Location(w, x, y, z);
		return l;
		
	}
}
