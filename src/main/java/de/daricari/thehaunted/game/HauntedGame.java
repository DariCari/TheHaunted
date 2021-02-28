package de.daricari.thehaunted.game;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import de.daricari.thehaunted.TheHaunted;
import net.kyori.adventure.text.Component;

public class HauntedGame 
{
	private static boolean activeGame = false;
	public static HauntedGame hauntedGame;
	
	
	private TheHaunted plugin = TheHaunted.getPlugin(TheHaunted.class);
	
	private Player haunted;
	private List<ItemStack> hauntedItems = new ArrayList<ItemStack>();
	private boolean isStarted = false;
	private boolean isProtectionPhase = true;
	private int unfoundPages;
	
	private Thread gameStart;
	
	public HauntedGame()
	{
		if(Bukkit.getOnlinePlayers().size() > TheHaunted.getSpawnLocations().size())
			throw new IndexOutOfBoundsException("There are more players online than set spawn locations!");
		else if (Bukkit.getOnlinePlayers().size() < 2)
			throw new IndexOutOfBoundsException("There are not enough players online to start a round!");
		else if(plugin.getConfig().getInt("general.gamePages") > TheHaunted.getPageLocations().size())
			throw new IndexOutOfBoundsException("The number of wanted pages is larger than the added pages!");
		else if (TheHaunted.getSwordLocation() == null)
			throw new NullPointerException("A sword location hasn't been set yet!");
		
		else
		{
			startGame();
			activeGame = true;
		}
	}
	
	private void startGame()
	{
		hauntedItems().forEach(item ->{
			hauntedItems.add(item);
		});
		
		gameStart = new Thread(new BukkitRunnable() {
			
			@Override
			public void run() {
				HauntedGameEvents.teleportPlayers();
				startTimer();
			}
		});
		gameStart.start();
	}
	
	/**Start a timer that runs from 5 seconds**/
	private void startTimer()
	{
		BukkitRunnable timer = new BukkitRunnable() {
			int count = 5*20;
			
			@Override
			public void run() {
				if(count >= 20)
				{
					float time = count/20;
					Bukkit.getOnlinePlayers().forEach(player -> {
						TheHaunted.sendPluginMessage(player, "The game starts in " + (int) time + " seconds!");
						player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1, 1);
					});
					count = count-20;
				}
				else
				{
					setStarted(true);
					this.cancel();
				}
			}
		};
		timer.runTaskTimerAsynchronously(plugin, 20*2, 20);
	}

	/**Returns if game if running (after timer ran out**/ 
	public boolean isStarted() {
		return isStarted;
	}

	/**If true does everything as soon as the game starts**/
	public void setStarted(boolean isStarted) 
	{
		if(isStarted)
		{	
			HauntedGameEvents.spawnSword();
			HauntedGameEvents.spawnPages();
			HauntedGameEvents.playSounds();
			HauntedGameEvents.healPlayers();
			HauntedGameEvents.startProt();
		}
		this.isStarted = isStarted;
	}

	public Player getHaunted() {
		return haunted;
	}
	/**
	 * @see GameEvents.setHauntedPlayer()
	 */
	public void setHaunted(Player haunted) 
	{
		this.haunted = haunted;
		HauntedGameEvents.setHauntedPlayer(haunted);
	}
	
	public List<ItemStack> getHauntedItems() {
		return hauntedItems;
	}

	/**Returns if game has been started (Even if there is still a count down)**/
	public static boolean isActiveGame() {
		return activeGame;
	}

	public static void setActiveGame(boolean activeGame) {
		HauntedGame.activeGame = activeGame;
	}
	
	/**
	 * 
	 * @return
	 * all items that belong to the haunted
	 */
	private List<ItemStack> hauntedItems()
	{
		NamespacedKey key = new NamespacedKey(plugin, "hauntedItems");
		
		List<ItemStack> items = new ArrayList<ItemStack>();
		
		
		//sword
		ItemStack item = new ItemStack(Material.IRON_SWORD);
		ItemMeta meta = item.getItemMeta();
		
			meta.displayName(Component.text(ChatColor.DARK_PURPLE + "Haunted Sword"));
		
		item.setItemMeta(meta);
		items.add(item);
		
		
		//heal
		ItemStack heal = new ItemStack(Material.IRON_SWORD);
		ItemMeta healMeta = heal.getItemMeta();
		heal.setType(Material.RED_DYE);
		
			healMeta.displayName(Component.text(ChatColor.RED + "Heal"));
		
		healMeta.getPersistentDataContainer().set(key, PersistentDataType.STRING, "heal");
		heal.setItemMeta(healMeta);
		items.add(heal);
		items.add(heal);
		items.add(heal);
		
		
		//grenade
		ItemStack grenade = new ItemStack(Material.IRON_SWORD);
		ItemMeta grenadeMeta = grenade.getItemMeta();
		grenade.setType(Material.GUNPOWDER);
	
			grenadeMeta.displayName(Component.text(ChatColor.YELLOW + "Grenade"));
		
		grenadeMeta.getPersistentDataContainer().set(key, PersistentDataType.STRING, "grenade");
		grenade.setItemMeta(grenadeMeta);
		items.add(grenade);
		items.add(grenade);
		
		
		//speed
		ItemStack speed = new ItemStack(Material.IRON_SWORD);
		ItemMeta speedMeta = speed.getItemMeta();
		speed.setType(Material.FEATHER);

			speedMeta.displayName(Component.text(ChatColor.GRAY + "Speed (5s)"));
		
		speedMeta.getPersistentDataContainer().set(key, PersistentDataType.STRING, "speed");
		speed.setItemMeta(speedMeta);
		items.add(speed);
		
		
		//Batbomb
		ItemStack batbomb = new ItemStack(Material.IRON_SWORD);
		ItemMeta batbombMeta = batbomb.getItemMeta();
		batbomb.setType(Material.TNT);

			batbombMeta.displayName(Component.text(ChatColor.RED + "Batbomb"));
		
		batbombMeta.getPersistentDataContainer().set(key, PersistentDataType.STRING, "batbomb");
		batbomb.setItemMeta(batbombMeta);
		items.add(batbomb);
		
		
		//freeze
		ItemStack freeze = new ItemStack(Material.IRON_SWORD);
		ItemMeta freezeMeta = freeze.getItemMeta();
		freeze.setType(Material.ICE);
		
			freezeMeta.displayName(Component.text(ChatColor.AQUA + "Freeze (3s)"));
		
		freezeMeta.getPersistentDataContainer().set(key, PersistentDataType.STRING, "freeze");
		freeze.setItemMeta(freezeMeta);
		items.add(freeze);
		
		
		
		return items;
		
	}

	/**
	 * 
	 * @return
	 * unfound pages
	 */
	public int getUnfoundPages() {
		return unfoundPages;
	}

	/**
	 * 
	 * @param unfoundPages
	 * changes unfound pages
	 */
	public void setUnfoundPages(int unfoundPages) 
	{
		this.unfoundPages = unfoundPages;
	}
	
	/**Lowers the unfoundPages by 1 and checks if the game has ended**/
	public void addFoundPage(Player player)
	{
		HauntedGameEvents.pageFound(player);
	}
	
	/**Ends the game 
	 * @param hauntedWon
	 * true if the haunted won, false if not
	 * 
	 * **/
	public void endGame(boolean hauntedWon)
	{
		HauntedGameEvents.endGame(hauntedWon);
	}

	/**
	 * 
	 * @return
	 * if there is a protection phase
	 */
	public boolean isProtectionPhase() {
		return isProtectionPhase;
	}

	/**
	 * 
	 * @param isProtectionPhase
	 * true if there is still a protection phase
	 */
	public void setProtectionPhase(boolean isProtectionPhase) {
		this.isProtectionPhase = isProtectionPhase;
	}
	
}
