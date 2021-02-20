package de.daricari.thehaunted.cmd;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import de.daricari.thehaunted.TheHaunted;
import de.daricari.thehaunted.game.HauntedGame;

public class StartCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(command.getName().equalsIgnoreCase("start"))
		{
			if(sender.hasPermission("thehaunted.admin"))
			{
				if(HauntedGame.isActiveGame())
				{
					TheHaunted.sendPluginMessage(sender, "The game has already started!");
				}
				else
				{
					TheHaunted.startGame(sender);
				}
				return true;
			}
		}
		return false;
	}

}
