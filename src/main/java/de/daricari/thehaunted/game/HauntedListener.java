package de.daricari.thehaunted.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.entity.Bat;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import de.daricari.thehaunted.TheHaunted;
import net.kyori.adventure.text.Component;

public class HauntedListener implements Listener
{
	private TheHaunted plugin = TheHaunted.getPlugin(TheHaunted.class);
	
	/**Player joins**/
	@EventHandler
	public void onJoin(PlayerJoinEvent event)
	{
		if(!HauntedGame.isActiveGame())
			return;
		if(HauntedGame.hauntedGame.getHaunted() == null)
			return;
		event.getPlayer().setGameMode(GameMode.SPECTATOR);
	}
	
	/**Players can't move during timer**/
	@EventHandler
	public void onMove(PlayerMoveEvent event)
	{
		
		if(HauntedGame.hauntedGame != null)
		{
			if(HauntedGame.hauntedGame.isStarted())
				return;
			if(event.getFrom().getBlockX() != event.getTo().getBlockX() || event.getFrom().getBlockZ() != event.getTo().getBlockZ())
			{
					event.setCancelled(true);
			}
			
		}
	}
	
	/**Only normal players and the haunted player can hit each other
	 * Disables hitting during protection phase
	 * **/
	@EventHandler
	public void onDmg(EntityDamageByEntityEvent event)
	{
		if(HauntedGame.hauntedGame == null)
			return;
		if(event.getEntity() instanceof ItemFrame || event.getEntityType().equals(EntityType.ITEM_FRAME))
			return;
		if(HauntedGame.hauntedGame.getHaunted() == null)
			return;
		
		if(HauntedGame.hauntedGame.isProtectionPhase())
		{
			event.setCancelled(true);
			return;
		}
		
		if(event.getDamager() instanceof Player && event.getEntity() instanceof Player)
		{
			Player damager = (Player) event.getDamager();
			Player player = (Player) event.getEntity();
			if(!HauntedGame.hauntedGame.getHaunted().equals(player) && !HauntedGame.hauntedGame.getHaunted().equals(damager))
			{
				event.setCancelled(true);
			}
		}
	}
	
	/**Disables damage if game has ended**/
	@EventHandler
	public void onDamage(EntityDamageEvent event)
	{
		if(!HauntedGame.isActiveGame())
			event.setCancelled(true);
	}
	
	/**Makes the player become the haunted if they pick up an iron sword**/
	@EventHandler
	public void onPickup(EntityPickupItemEvent event)
	{
		if(!(event.getEntity() instanceof Player))
			return;
		Player player = (Player) event.getEntity();
		if(event.getItem().getItemStack().getType().equals(Material.IRON_SWORD))
		{
			event.setCancelled(true);
			event.getItem().remove();
			HauntedGameEvents.setHauntedPlayer(player);
			player.playSound(player.getLocation(), Sound.ENTITY_WITHER_DEATH, 1, 1.8f);
		}
	}
	
	/**Haunted player items and pages**/
	@EventHandler
	public void onItemFrame(PlayerInteractEntityEvent event)
	{
		
		Player player = event.getPlayer();
		
		if(HauntedGame.hauntedGame.getHaunted() == null)
			event.setCancelled(true);
		
		if(!HauntedGame.hauntedGame.getHaunted().equals(player))
		{
			if(event.getRightClicked() == null)
				return;
			
			if(!(event.getRightClicked() instanceof ItemFrame))
				return;
			
			ItemFrame frame = (ItemFrame) event.getRightClicked();
			if(frame.getPersistentDataContainer().get(new NamespacedKey(plugin, "page"), PersistentDataType.INTEGER) == 1)
			{
				frame.setItem(null);
				HauntedGame.hauntedGame.addFoundPage(player);
				frame.getPersistentDataContainer().set(new NamespacedKey(plugin, "page"), PersistentDataType.INTEGER, 0);
			}
			event.setCancelled(true);
		}
		else
		{
			if(event.getRightClicked() == null)
				return;
			if(!(event.getRightClicked() instanceof ItemFrame))
				return;
			event.setCancelled(true);
			
		}
	}
	
	/**Deaths of people**/
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onDeath(PlayerDeathEvent event)
	{
		if(!HauntedGame.isActiveGame())
			return;
		if(HauntedGame.hauntedGame.getHaunted() == null)
			return;
		
		NamespacedKey key = new NamespacedKey(plugin, "isDead");
		final Player player = event.getEntity();
		player.getPersistentDataContainer().set(key, PersistentDataType.INTEGER, 1);
		event.getDrops().clear();
		try {
			event.deathMessage(Component.text(ChatColor.translateAlternateColorCodes('&', "&8[&5TheHaunted&8]&b " + event.getEntity().getName() + "&3 died!")));
		}catch(NoClassDefFoundError ex)
		{
			event.setDeathMessage(ChatColor.translateAlternateColorCodes('&', "&8[&5TheHaunted&8]&b " + event.getEntity().getName() + "&3 died!"));
		}
		
		
		if(HauntedGame.hauntedGame.getHaunted().equals(event.getEntity()))
		{
			int remainingPlayers = 0;
			for(Player p : Bukkit.getOnlinePlayers())
			{
				if(p.getPersistentDataContainer().get(key, PersistentDataType.INTEGER) == 1)
				{
					continue;
				}
				else
				{
					remainingPlayers++;
				}
				
			}
			if(remainingPlayers <= 1)
			{
				HauntedGame.hauntedGame.endGame(false);
			}
			else
			{
				event.getDrops().add(new ItemStack(Material.IRON_SWORD));
			}
			return;
		}
		int remainingPlayers = 0;
		for(Player p : Bukkit.getOnlinePlayers())
		{
			if(p.getPersistentDataContainer().get(key, PersistentDataType.INTEGER) == 1)
			{
				continue;
			}
			else
			{
				remainingPlayers++;
			}
			
		}
		if(remainingPlayers <= 1)
		{
			HauntedGame.hauntedGame.endGame(true);
			return;
		}
		
	}
	
	/**Prohibits dropping of items during active game**/
	@EventHandler
	public void onDrop(PlayerDropItemEvent event)
	{
		if(HauntedGame.hauntedGame == null)
			return;
		if(HauntedGame.hauntedGame.getHaunted() == null)
			return;
		if(!HauntedGame.hauntedGame.isStarted() || !HauntedGame.isActiveGame())
			return;
		event.setCancelled(true);
	}
	
	/**Globally disables sprinting, because blindness disables sprinting for normal players**/
	@EventHandler
	public void onSprint(FoodLevelChangeEvent event)
	{
		if(HauntedGame.hauntedGame == null)
			return;
		if(HauntedGame.hauntedGame.getHaunted() == null)
			return;
		if(!HauntedGame.hauntedGame.isStarted() || !HauntedGame.isActiveGame())
			return;
		
		event.setCancelled(true);
		event.setFoodLevel(1);
	}
	
	/**Disables spawning of other mobs**/
	@EventHandler
	public void onSpawn(CreatureSpawnEvent event)
	{
		if(event.getEntity() instanceof Player ||
				event.getEntity() instanceof ItemFrame ||
				event.getEntity() instanceof Item ||
				event.getEntity() instanceof Bat || 
				event.getEntity() instanceof Snowball)
			return;
		event.setCancelled(true);
	}
	
	/**Makes the game end if theHaunted leaves**/
	@EventHandler
	public void onLeave(PlayerQuitEvent event)
	{
		if(!HauntedGame.isActiveGame())
			return;
		if(HauntedGame.hauntedGame.getHaunted() == null)
			return;
		
		NamespacedKey key = new NamespacedKey(plugin, "isDead");
		event.getPlayer().getPersistentDataContainer().set(key, PersistentDataType.INTEGER, 1);
		
		if(HauntedGame.hauntedGame.getHaunted().equals(event.getPlayer()))
		{
			int remainingPlayers = 0;
			for(Player p : Bukkit.getOnlinePlayers())
			{
				if(p.getPersistentDataContainer().get(key, PersistentDataType.INTEGER) == 1)
				{
					continue;
				}
				else
				{
					remainingPlayers++;
				}
				
			}
			if(remainingPlayers <= 1)
			{
				HauntedGame.hauntedGame.endGame(false);
			}
			else
			{
				List<Player> players = new ArrayList<Player>();
				Bukkit.getOnlinePlayers().forEach(p -> {
					players.add(p);
				});
				Collections.shuffle(players, new Random());
				players.get(1).getWorld().dropItemNaturally(players.get(1).getLocation(), new ItemStack(Material.IRON_SWORD));
			}
			return;
		}
		int remainingPlayers = 0;
		for(Player p : Bukkit.getOnlinePlayers())
		{
			if(p.getPersistentDataContainer().get(key, PersistentDataType.INTEGER) == 1)
			{
				continue;
			}
			else
			{
				remainingPlayers++;
			}
			
		}
		if(remainingPlayers <= 1)
		{
			HauntedGame.hauntedGame.endGame(true);
		}
	}
}
