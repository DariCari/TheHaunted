package de.daricari.thehaunted.reflection;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scoreboard.Scoreboard;

import de.daricari.thehaunted.TheHaunted;

public class TextComponentBuilder 
{
	private TheHaunted plugin;
	
	private Class<?> componentClass;
	private Method componentTextMethod;
	
	
	private String context;
	
	public TextComponentBuilder(String context)
	{
		this.plugin = TheHaunted.getPlugin(TheHaunted.class);
		this.context = context;
	}
	
	public Object getTextComponent()
	{
		
		String comp = this.context;
		try {
			componentClass = Class.forName("net.kyori.adventure.text.Component");
			componentTextMethod = componentClass.getMethod(("text"), String.class);
			
			Object textComponent = componentTextMethod.invoke(null, comp);
			return textComponent;
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | ClassNotFoundException | NoSuchMethodException | SecurityException e) {
			return null;
		}
	}
	
	public Object getTextComponent(String context)
	{
		
		String comp = context;
		try {
			componentClass = Class.forName("net.kyori.adventure.text.Component");
			componentTextMethod = componentClass.getMethod(("text"), String.class);
			
			Object textComponent = componentTextMethod.invoke(null, comp);
			return textComponent;
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | ClassNotFoundException | NoSuchMethodException | SecurityException e) {
			return null;
		}
	}
	
	public void deathMessage(PlayerDeathEvent event, Object textComponent)
	{
		Method deathMessageMethod;
		try {
			deathMessageMethod = event.getClass().getMethod("deathMessage", componentClass);
			deathMessageMethod.invoke(event, new Object[] {textComponent});
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NullPointerException e) {
			try {
				componentClass = String.class;
				deathMessageMethod = event.getClass().getMethod("setDeathMessage", componentClass);
				deathMessageMethod.invoke(event, new Object[] {context});
			} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e1) {
				plugin.getLogger().severe("Unsupported version!");
				throw new UnsupportedClassVersionError("Unsupported version");
			}
		}
	}
	
	public void displayName(ItemMeta meta, Object textComponent)
	{
		Method displayNameMethod;
		try {
			displayNameMethod = ItemMeta.class.getMethod("displayName", componentClass);
			displayNameMethod.invoke(meta, new Object[] {textComponent});
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NullPointerException e) {
			try {
				componentClass = String.class;
				displayNameMethod = ItemMeta.class.getMethod("setDisplayName", componentClass);
				displayNameMethod.setAccessible(true);
				displayNameMethod.invoke(meta, new Object[] {context});
			} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e1) {
				e1.printStackTrace();
				plugin.getLogger().severe("Unsupported version!");
				throw new UnsupportedClassVersionError("Unsupported version");
			}
		}
	}
	
	public void scoreboardObjective(Scoreboard board, String scoreName, String criteria, Object textComponent)
	{
		Method scoreboardRegisterMethod;
		try {
			scoreboardRegisterMethod = board.getClass().getMethod("registerNewObjective", String.class, String.class, componentClass);
			scoreboardRegisterMethod.invoke(board, new Object[] {scoreName, criteria, textComponent});
			
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NullPointerException e) {
			try {
				componentClass = String.class;
				scoreboardRegisterMethod = board.getClass().getMethod("registerNewObjective", String.class, String.class, componentClass);
				scoreboardRegisterMethod.setAccessible(true);
				scoreboardRegisterMethod.invoke(board, new Object[] {scoreName, criteria, context});
			} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e1) {
				e1.printStackTrace();
				plugin.getLogger().severe("Unsupported version!");
				throw new UnsupportedClassVersionError("Unsupported version");
			}
		}
	}
	
}
