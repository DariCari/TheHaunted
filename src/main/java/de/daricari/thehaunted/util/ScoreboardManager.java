package de.daricari.thehaunted.util;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team.Option;
import org.bukkit.scoreboard.Team.OptionStatus;

import de.daricari.thehaunted.TheHaunted;
import de.daricari.thehaunted.game.HauntedGame;
import de.daricari.thehaunted.reflection.TextComponentBuilder;

public class ScoreboardManager 
{	
	private TheHaunted plugin;
	
	private Scoreboard board;
	private Objective obj;
	
	private Score text;
	private Score foundP;
	
	public ScoreboardManager(TheHaunted plugin)
	{
		this.plugin = plugin;
		setScoreboard();
	}
	
	private void setScoreboard()
	{
		board = Bukkit.getScoreboardManager().getNewScoreboard();
		
		TheHaunted.getWorldManager().getOnlinePlayers().forEach(p -> {
			if(board.getTeam("default") == null)
			{
				board.registerNewTeam("default");
				board.getTeam("default").setOption(Option.NAME_TAG_VISIBILITY, OptionStatus.NEVER);
			}
			board.getTeam("default").addEntry(p.getName());
		});
		
		int unFoundPages;
		if(!(HauntedGame.hauntedGame.getUnfoundPages() == 0))
			unFoundPages = HauntedGame.hauntedGame.getUnfoundPages();
		else
			unFoundPages = plugin.getConfig().getInt("general.gamePages");
		
		TextComponentBuilder textComp = new TextComponentBuilder(ChatColor.translateAlternateColorCodes('&', "&8[&5TheHaunted&8]&3"));
		textComp.scoreboardObjective(board, "hScoreboard", "dummy", textComp.getTextComponent());
		
		board.getObjectives().forEach(o -> {
			obj = o;
		});
		
		obj.setDisplaySlot(DisplaySlot.SIDEBAR);
		
		text = obj.getScore(ChatColor.translateAlternateColorCodes('&', "&3Pages remaining:"));
		text.setScore(1);
		foundP = obj.getScore(ChatColor.translateAlternateColorCodes('&', "&d" + unFoundPages));
		foundP.setScore(0);
	}
	
	private Scoreboard getScoreboard()
	{
		return board;
	}
	
	public void updateScores()
	{
		setScoreboard();
		
		TheHaunted.getWorldManager().getOnlinePlayers().forEach(player -> {
			player.setScoreboard(getScoreboard());
		});
	}
	
	public void clearScores()
	{
		TheHaunted.getWorldManager().getOnlinePlayers().forEach(player -> {
			player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
		});
	}
}
