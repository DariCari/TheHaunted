package de.daricari.thehaunted.game;

import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Bat;
import org.bukkit.entity.Egg;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import de.daricari.thehaunted.TheHaunted;

public class HauntedPlayerEvents 
{
	private static TheHaunted plugin = TheHaunted.getPlugin(TheHaunted.class);
	
	public static void healPlayer(Player player)
	{
		double health = player.getHealth();
		double max = 20;
		health = health+8;
		if(health>max)
			player.setHealth(max);
		else
			player.setHealth(health);
	}
	
	public static void grenade(Player player)
	{
		Location loc = player.getEyeLocation();
		World world = player.getWorld();
		Snowball ball = (Snowball)world.spawn(loc, Snowball.class);
		NamespacedKey key = new NamespacedKey(plugin, "grenade");
		ball.getPersistentDataContainer().set(key, PersistentDataType.INTEGER, 1);
		ball.setShooter(player);
		ball.setVelocity(loc.getDirection().multiply(1.5));
	}
	
	public static void speed(Player player)
	{
		player.removePotionEffect(PotionEffectType.SLOW);
		new BukkitRunnable() {
			
			@Override
			public void run() {
				player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 86400, 2, false, false));
				
			}
		}.runTaskLater(plugin, 20*5);
	}
	
	public static void spawnBatEgg(Player player)
	{	
			Location loc = player.getEyeLocation();
			World world = loc.getWorld();
			NamespacedKey key = new NamespacedKey(plugin, "bat");
			
			Egg egg = (Egg)world.spawn(loc, Egg.class);
			egg.getPersistentDataContainer().set(key, PersistentDataType.INTEGER, 1);
			egg.setShooter(player);
			egg.setVelocity(loc.getDirection().multiply(1.5));
	}
	
	public static void batBomb(Location loc)
	{
		World world = loc.getWorld();
		new BukkitRunnable() 
		{					
			@Override
			public void run() {
				for(int i = 0; i<10; i++)
					{
						Bat bat = (Bat)world.spawnEntity(loc, EntityType.BAT);
						new BukkitRunnable() {
							
							@Override
							public void run() {
								world.createExplosion(bat, bat.getLocation(), 2, false, false);
								bat.remove();
								
							}
						}.runTaskLater(plugin, 20*3);
					}	
			}
		}.runTask(plugin);
	}
	
	public static void freeze(Player player)
	{
		NamespacedKey key = new NamespacedKey(plugin, "isDead");
		BukkitRunnable unfreeze = new BukkitRunnable() {
			
			@Override
			public void run() {
				for(Player p : TheHaunted.getWorldManager().getOnlinePlayers())
				{
					if(p.getPersistentDataContainer().get(key, PersistentDataType.INTEGER) == 0)
					{
						if(!p.equals(player))
						{
							p.removePotionEffect(PotionEffectType.SLOW);
							p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 86400, 1, false, false));
						}
					}
				}
				
			}
		};
		for(Player p : TheHaunted.getWorldManager().getOnlinePlayers())
		{
			if(p.getPersistentDataContainer().get(key, PersistentDataType.INTEGER) == 0)
			{
				if(!p.equals(player))
				{
					p.removePotionEffect(PotionEffectType.SLOW);
					p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20*4, 10, false, false));
					p.playSound(p.getLocation(), Sound.ENTITY_WOLF_GROWL, 1, 1);
				}
			}
			TheHaunted.sendPluginMessage(p, "All players are frozen!");
		}
		unfreeze.runTaskLater(plugin, 20*3);
	}
}
