package de.daricari.thehaunted.game;

import java.util.logging.Level;

import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.Egg;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import de.daricari.thehaunted.TheHaunted;

public class HauntedPlayerListener implements Listener 
{
	private TheHaunted plugin = TheHaunted.getPlugin(TheHaunted.class);
	
	@EventHandler
	public void onUse(PlayerInteractEvent event) {
		if(!event.getAction().equals(Action.RIGHT_CLICK_AIR) && !event.getAction().equals(Action.RIGHT_CLICK_BLOCK))
			return;
		Player player = (Player) event.getPlayer();
		if(HauntedGame.isActiveGame() && HauntedGame.hauntedGame != null && HauntedGame.hauntedGame.getHaunted() != null)
		{
			if(HauntedGame.hauntedGame.getHaunted().equals(player))
			{
				if(event.getItem() == null)
					return;
				PersistentDataContainer container = event.getItem().getItemMeta().getPersistentDataContainer();
				NamespacedKey key = new NamespacedKey(plugin, "hauntedItems");
				if(!container.has(key, PersistentDataType.STRING))
					return;
				switch((String) container.get(key, PersistentDataType.STRING))
				{
				default:
					plugin.getLogger().log(Level.INFO, "Nothing found!");
					return;
				case "heal":
					HauntedPlayerEvents.healPlayer(player);
					break;
				case "grenade":
					HauntedPlayerEvents.grenade(player);
					break;
				case "speed":
					HauntedPlayerEvents.speed(player);
					break;
				case "batbomb":
					HauntedPlayerEvents.spawnBatEgg(player);
					break;
				case "freeze":
					HauntedPlayerEvents.freeze(player);
					break;
				}
				player.getInventory().setItemInMainHand(null);
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onSnowball(ProjectileHitEvent event)
	{
		if(event.getEntity() instanceof Snowball || event.getEntityType().equals(EntityType.SNOWBALL))
		{
			PersistentDataContainer container = event.getEntity().getPersistentDataContainer();
			NamespacedKey key = new NamespacedKey(plugin, "grenade");
			if(container.get(key, PersistentDataType.INTEGER) == 1)
			{
				Location loc = event.getEntity().getLocation();
				World world = loc.getWorld();
				world.createExplosion(loc, 1, false, false);
			}
		}
	}
	
	@EventHandler
	public void onEgg(ProjectileHitEvent event)
	{
		if(event.getEntity() instanceof Egg || event.getEntityType().equals(EntityType.EGG))
		{
			PersistentDataContainer container = event.getEntity().getPersistentDataContainer();
			NamespacedKey key = new NamespacedKey(plugin, "bat");
			if(container.get(key, PersistentDataType.INTEGER) == 1)
			{
				Location loc = event.getEntity().getLocation();
				HauntedPlayerEvents.batBomb(loc);
			}
		}
	}
}
