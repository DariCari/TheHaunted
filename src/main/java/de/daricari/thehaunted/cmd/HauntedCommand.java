package de.daricari.thehaunted.cmd;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import de.daricari.thehaunted.TheHaunted;
import de.daricari.thehaunted.game.HauntedGame;
import de.daricari.thehaunted.util.LocationManager;

public class HauntedCommand implements CommandExecutor, TabCompleter 
{
	private static TheHaunted plugin = TheHaunted.getPlugin(TheHaunted.class);
	
	public static String getUsage()
	{
		String PREFIX = "&8[&5TheHaunted&8]&3 ";
		String usage = ChatColor.translateAlternateColorCodes('&', 
				PREFIX + "Version &d" + plugin.getDescription().getVersion() + "\n" +
				PREFIX + "&b/thehaunted addspawn &3|&b /thehaunted removespawn" + "\n" +
				PREFIX + "&3Adds or removes the block you are currently looking at as a spawn location" + "\n" +
				PREFIX + "&b/thehaunted addpage &3|&b /thehaunted removepage" + "\n" +
				PREFIX + "&3Adds or removes the item frame you are currently looking at as a page" + "\n" +
				PREFIX + "&b/thehaunted setsword" + "\n" + 
				PREFIX + "&3Sets the location where the sword should spawn at the beginning of the game" + "\n" +
				PREFIX + "&c/thehaunted forcestop" + "\n" +
				PREFIX + "&3Forces the game to stop immediately!" + "\n" +
				PREFIX + "&a/start" + "\n" +
				PREFIX + "&Starts the game"
				);
		
		return usage;
		
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(command.getName().equalsIgnoreCase("thehaunted"))
		{
			if(!(sender instanceof Player))
			{
				TheHaunted.sendPluginMessage(sender, "This command only works for players!");
				return true;
			}
			Player player = (Player) sender;
			if(player.hasPermission("thehaunted.admin"))
			{
				if(args.length == 1)
				{
					switch(args[0])
					{
					default: 
						return false;
						
					case "addspawn":
						if(player.getTargetBlock(5) != null && LocationManager.addSpawnLocation(player.getTargetBlock(5).getLocation()))
							TheHaunted.sendPluginMessage(player, "Successfully added spawn location!");
						else
							TheHaunted.sendPluginMessage(player, "Could not add spawn location!");
						
						return true;
						
					case "removespawn":
						if(player.getTargetBlock(5) != null && LocationManager.removeSpawnLocation(player.getTargetBlock(5).getLocation()))
							TheHaunted.sendPluginMessage(player, "Successfully removed spawn location!");
						else
							TheHaunted.sendPluginMessage(player, "Could not remove spawn location!");
						
						return true;
						
					case "addpage":
						if(player.getTargetEntity(5) != null && LocationManager.addPageLocation(player.getTargetEntity(5).getLocation(), player.getTargetEntity(5)))
							TheHaunted.sendPluginMessage(player, "Successfully added page location!");
						else
							TheHaunted.sendPluginMessage(player, "Could not add page location!");
						
						return true;
						
					case "removepage":
						if(player.getTargetEntity(5) != null && LocationManager.removePageLocation(player.getTargetEntity(5).getLocation(), player.getTargetEntity(5)))
							TheHaunted.sendPluginMessage(player, "Successfully removed page location!");
						else
							TheHaunted.sendPluginMessage(player, "Could not remove page location!");
						
						return true;
						
					case "setsword":
						LocationManager.setSwordLocation(player.getLocation());
						TheHaunted.sendPluginMessage(player, "Successfully set sword location!");
						return true;
						
					case "forcestop":
						if(HauntedGame.isActiveGame())
						{
							HauntedGame.hauntedGame.endGame(true);
							TheHaunted.sendPluginMessage(player, "Stopped the game!");
						}
						else
							TheHaunted.sendPluginMessage(player, "The game has not started yet!");
						return true;
					}
				}
				return false;
			}
			else
			{
				TheHaunted.sendPluginMessage(sender, "You do not have permission to execute this command!");
				return true;
			}
		}
		return false;
	}

	
	List<String> arguments = new ArrayList<String>();
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		if (arguments.isEmpty()) {
			arguments.add("addspawn");
			arguments.add("removespawn");
			arguments.add("addpage");
			arguments.add("removepage");
			arguments.add("setsword");
			arguments.add("forcestop");
		}

		List<String> result = new ArrayList<String>();
		if (args.length == 1) {
			for (String a : arguments) {
				if (a.toLowerCase().startsWith(args[0].toLowerCase()))
					result.add(a);
			}
			return result;
		}

		return null;
	}

}
