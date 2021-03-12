package de.daricari.thehaunted.util;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import de.daricari.thehaunted.TheHaunted;
import de.daricari.thehaunted.game.HauntedGame;

public class LocationManager 
{
	private static TheHaunted plugin = TheHaunted.getPlugin(TheHaunted.class);
	//private static Logger logger = plugin.getLogger();
	
	public static boolean addSpawnLocation(Location spawnLoc)
	{
		if(spawnLoc == null)
			return false;
		
		List<String> locs = TheHaunted.getSpawnLocations();
		String spLoc = toString(spawnLoc);
		
		if(!locs.contains(spLoc))
		{
			TheHaunted.getSpawnLocations().add(spLoc);
			return true;
		}
		return false;
	}
	
	public static boolean removeSpawnLocation(Location spawnLoc)
	{
		if(spawnLoc == null)
			return false;
		
		List<String> locs = TheHaunted.getSpawnLocations();
		String spLoc = toString(spawnLoc);
		
		if(locs.contains(spLoc))
		{
			TheHaunted.getSpawnLocations().remove(spLoc);
			return true;
		}
		return false;
	}
	
	public static boolean addPageLocation(Location pLoc, Entity e)
	{
		if(pLoc == null || e == null)
			return false;
		
		if(!e.getType().equals(EntityType.ITEM_FRAME))
		{
			return false;
		}
		ItemFrame frame = (ItemFrame) e;
		
		List<String> locs = TheHaunted.getPageLocations();
		String spLoc = toString(pLoc);
		
		if(!locs.contains(spLoc))
		{
			TheHaunted.getPageLocations().add(spLoc);
			
			frame.setItem(new ItemStack(Material.PAPER));
			return true;
		}
		return false;
		
	}
	
	public static boolean removePageLocation(Location pLoc, Entity e)
	{
		if(pLoc == null || e == null)
			return false;
		
		if(!e.getType().equals(EntityType.ITEM_FRAME))
		{
			return false;
		}
		ItemFrame frame = (ItemFrame) e;
		
		List<String> locs = TheHaunted.getPageLocations();
		String spLoc = toString(pLoc);
		
		if(locs.contains(spLoc))
		{
			TheHaunted.getPageLocations().remove(spLoc);
			
			frame.setItem(new ItemStack(Material.BARRIER));
			
			return true;
		}
		return false;
		
	}
	
	public static boolean showAllPages()
	{
		List<Location> locations = new ArrayList<Location>();
		List<String> locs = TheHaunted.getPageLocations();
		locs.forEach(s -> {
			locations.add(fromString(s));
		});
		
		if(!HauntedGame.isActiveGame())
		{
			locations.get(0).getWorld().getEntitiesByClass(ItemFrame.class).forEach(entity -> entity.remove());
			new BukkitRunnable() {
				
				@Override
				public void run() {
					locations.forEach(loc ->{
						ItemFrame frame = (ItemFrame) loc.getWorld().spawnEntity(loc, EntityType.ITEM_FRAME);
						frame.setItem(new ItemStack(Material.PAPER));
					});
					
				}
			}.runTaskLater(plugin, 2);
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
	
	public static String toString(Location loc)
	{
		int x=loc.getBlockX();
		int y=loc.getBlockY();
		int z=loc.getBlockZ();
		
		String sLoc = "X: " + x + " Y: " + y + " Z: " + z + " World: " + loc.getWorld().getName();
		
		return sLoc;
		
	}
}
