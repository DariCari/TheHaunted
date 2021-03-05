package de.daricari.thehaunted.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Team.Option;
import org.bukkit.scoreboard.Team.OptionStatus;

import de.daricari.thehaunted.TheHaunted;
import de.daricari.thehaunted.util.LocationManager;
import net.kyori.adventure.text.Component;

public class HauntedGameEvents 
{
	private static TheHaunted plugin = TheHaunted.getPlugin(TheHaunted.class);
	
	/**
	 * Teleport all players to a random added spawn location and changes their gamemode to adventure
	 */
	public static void teleportPlayers()
	{
		Collections.shuffle(TheHaunted.getSpawnLocations(), new Random());
		
		BukkitRunnable tpPlayers = new BukkitRunnable() {
			@Override
			public void run() {
				for(Player p : TheHaunted.getWorldManager().getOnlinePlayers())
				{
					p.setGameMode(GameMode.ADVENTURE);
					
						for(String l : TheHaunted.getSpawnLocations())
						{
							Location loc = LocationManager.fromString(l);
							Location spawnLoc = new Location(loc.getWorld(), loc.getBlockX(), loc.getBlockY()+2, loc.getBlockZ());
							if(spawnLoc.getNearbyPlayers(1, 3).isEmpty())
							{
								p.teleport(new Location(loc.getWorld(), loc.getBlockX(), loc.getBlockY()+2, loc.getBlockZ()));
								break;
							}
						}
						continue;
				}
				
			}
		};
		tpPlayers.runTask(plugin);
	}
	
	/**Spawn sword at beginning of game
	 * change difficulty and weather
	 * **/
	public static void spawnSword()
	{
		Location swordLoc = TheHaunted.getSwordLocation();
		World world = swordLoc.getWorld();
		
		ItemStack sword = new ItemStack(Material.IRON_SWORD);
		
		BukkitRunnable spawnEntities = new BukkitRunnable() {
			
			@Override
			public void run() {
				world.strikeLightningEffect(swordLoc);
				world.dropItemNaturally(swordLoc, sword);
				world.setDifficulty(Difficulty.EASY);
				world.setStorm(true);
			}
		};
		spawnEntities.runTask(plugin);

	}
	
	/**Change Haunted player**/
	public static void setHauntedPlayer(Player player)
	{
		//also gives all players their stuff
		HauntedGameEvents.addHauntedItems(player);
		HauntedGameEvents.addEffects();
		for(Player p : TheHaunted.getWorldManager().getOnlinePlayers())
		{
			TheHaunted.sendPluginMessage(p, "&b" + player.getName() + "&3 is now the haunted!");
		}
	}
	
	/**Adds Effects and Armor and disables the names to be shown**/
	public static void addEffects()
	{
		NamespacedKey key = new NamespacedKey(plugin, "isDead");
		
		for(Player p : TheHaunted.getWorldManager().getOnlinePlayers())
		{
			if(p.getPersistentDataContainer().has(key, PersistentDataType.INTEGER))
				if(p.getPersistentDataContainer().get(key, PersistentDataType.INTEGER) == 1)
					continue;
			
			p.setGameMode(GameMode.ADVENTURE);
			p.removePotionEffect(PotionEffectType.SLOW);
			p.removePotionEffect(PotionEffectType.BLINDNESS);
			
			if(p.getScoreboard().getTeam("default") == null)
			{
				p.getScoreboard().registerNewTeam("default");
				p.getScoreboard().getTeam("default").setOption(Option.NAME_TAG_VISIBILITY, OptionStatus.NEVER);
			}
			p.getScoreboard().getTeam("default").addEntry(p.getName());
			
			p.setFoodLevel(1);
			if(p.equals(HauntedGame.hauntedGame.getHaunted()))
				{
					p.getInventory().setChestplate(new ItemStack(Material.CHAINMAIL_CHESTPLATE));
					p.getInventory().setLeggings(new ItemStack(Material.CHAINMAIL_LEGGINGS));
					p.getInventory().setBoots(new ItemStack(Material.CHAINMAIL_BOOTS));
					
					p.setHealth(20);
					
					p.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 20*10, 5, false, false));
					p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 86400, 2, false, false));
					continue;
				}
			p.getInventory().setHelmet(new ItemStack(Material.CHAINMAIL_HELMET));
			p.getInventory().setChestplate(new ItemStack(Material.CHAINMAIL_CHESTPLATE));
			p.getInventory().setLeggings(new ItemStack(Material.CHAINMAIL_LEGGINGS));
			p.getInventory().setBoots(new ItemStack(Material.CHAINMAIL_BOOTS));
			p.getInventory().setItem(0, new ItemStack(Material.STONE_SWORD));
			
			p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 86400, 1, false, false));
			p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 86400, 1, false, false));
		}
	}
	
	/**Ends the game
	 * @see HauntedGame.endGame(boolean hauntedWon)
	 * **/
	public static void endGame(boolean hauntedWon)
	{
		TheHaunted.getWorldManager().getOnlinePlayers().forEach(player ->{
			player.getInventory().clear();
			player.removePotionEffect(PotionEffectType.SLOW);
			player.removePotionEffect(PotionEffectType.BLINDNESS);
			player.stopSound(Sound.MUSIC_DISC_11);
			player.setFoodLevel(20);
		});
		
		HauntedGame.setActiveGame(false);
		HauntedGame.hauntedGame.setStarted(false);
		HauntedGame.hauntedGame = null;
		
		Location fireworkLoc = TheHaunted.getSwordLocation();
		fireworkLoc.getWorld().spawnEntity(fireworkLoc, EntityType.FIREWORK);
		
		fireworkLoc.getWorld().setDifficulty(Difficulty.PEACEFUL);
		
		fireworkLoc.getWorld().getEntitiesByClass(Item.class).forEach(item -> {
			item.remove();
		});
		
		if(!hauntedWon)
		{
			for(Player p : TheHaunted.getWorldManager().getOnlinePlayers())
			{
				TheHaunted.sendPluginMessage(p,"The game has ended! &aThe Haunted lost.");
			}
		}
		else
		{
			for(Player p : TheHaunted.getWorldManager().getOnlinePlayers())
			{
				TheHaunted.sendPluginMessage(p,"The game has ended! &cThe Haunted won.");
			}
		}
	}
	
	/**Spawn pages**/
	public static void spawnPages()
	{
		List<Location> pageLocs = new ArrayList<Location>();
		for(String s : TheHaunted.getPageLocations())
		{
			Location loc = LocationManager.fromString(s);
			pageLocs.add(loc);
		}
		//Random pages spawn every round
		Collections.shuffle(pageLocs, new Random());
		
		BukkitRunnable spawnPages = new BukkitRunnable() {	
			int pages = 0;
			boolean killedEntities = false;
			@Override
			public void run() {
				for(Location loc : pageLocs)
				{
					World world = loc.getWorld();
					if(!killedEntities)
						world.getEntitiesByClass(ItemFrame.class).forEach(frame -> {
							frame.remove();
						});
					killedEntities = true;
					
					new BukkitRunnable() {
						@Override
						public void run() {
							ItemFrame frame;
							try {
								frame = (ItemFrame)world.spawnEntity(loc, EntityType.ITEM_FRAME);
							}catch(IllegalArgumentException ex)
							{
								return;
							}
							if(pages>=plugin.getConfig().getInt("general.gamePages"))
							{
								frame.getPersistentDataContainer().set(new NamespacedKey(plugin, "page"), PersistentDataType.INTEGER, 0);
								frame.remove();
								HauntedGame.hauntedGame.setUnfoundPages(pages);
							}
							else
							{
								frame.getPersistentDataContainer().set(new NamespacedKey(plugin, "page"), PersistentDataType.INTEGER, 1);
								frame.setInvulnerable(true);
								ItemStack paper = new ItemStack(Material.PAPER);
								paper.getItemMeta().displayName(Component.text("Page"));
								
								final ItemStack item = paper;
								frame.setItem(item);
								pages++;
							}
						}
					}.runTaskLater(plugin, 2);
				}
			}
			
		};
		spawnPages.runTask(plugin);
		
	}
	
	/**Heals all players**/
	public static void healPlayers()
	{
		TheHaunted.getWorldManager().getOnlinePlayers().forEach(player -> {
			NamespacedKey key = new NamespacedKey(plugin, "isDead");
			player.getPersistentDataContainer().set(key, PersistentDataType.INTEGER, 0);
			
			player.setHealth(20);
		});
	}
	
	/**Start protection phase**/
	public static void startProt()
	{
		new BukkitRunnable() {
			
			@Override
			public void run() {
				HauntedGame.hauntedGame.setProtectionPhase(false);
				TheHaunted.getWorldManager().getOnlinePlayers().forEach(p -> {
					TheHaunted.sendPluginMessage(p, "Be aware! There is a haunted person among us..");
				});
				
			}
		}.runTaskLater(plugin, 20*15);
	}
	
	/**Adds the items of the haunted to the player and gives 5 seconds of nausea**/
	public static void addHauntedItems(Player haunted)
	{
		haunted.getInventory().clear();
		int slot = 0;
		for(ItemStack item : HauntedGame.hauntedGame.getHauntedItems())
		{
			haunted.getInventory().setItem(slot, item);
			slot++;
		}
		haunted.getInventory().setHelmet(new ItemStack(Material.SKELETON_SKULL));
	}
	
	/**Start the music**/
	public static void playSounds()
	{
		if(plugin.getConfig().getBoolean("general.serverResourcepack"))
		{
			for(Player player : TheHaunted.getWorldManager().getOnlinePlayers())
			{
				player.playSound(player.getLocation(), Sound.MUSIC_DISC_11, 1, 1);
			}
		}
	}
	
	/**Page found**/
	public static void pageFound(Player player)
	{
		HauntedGame.hauntedGame.setUnfoundPages(HauntedGame.hauntedGame.getUnfoundPages()-1);
		
		//Ends the game
		if(HauntedGame.hauntedGame.getUnfoundPages() == 0)
		{
			HauntedGame.hauntedGame.endGame(false);
			return;
		}
		
		for(Player p : TheHaunted.getWorldManager().getOnlinePlayers())
		{
			p.playSound(p.getLocation(), Sound.ENTITY_ENDERMAN_SCREAM, 1, 1);
			TheHaunted.sendPluginMessage(p, "&b" + player.getName() + "&3 has found a page! (&d" + HauntedGame.hauntedGame.getUnfoundPages() + " remaining&3)");
		}
	}
}
