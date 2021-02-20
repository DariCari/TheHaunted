package de.daricari.thehaunted.files;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import de.daricari.thehaunted.TheHaunted;

public class DataManager 
{
	private TheHaunted plugin;
	private FileConfiguration dataConfig = null;
	private File configFile = null;
	private String fileName;

	public DataManager(TheHaunted plugin, String fileName) {
		this.plugin = plugin;
		this.fileName = fileName;
		saveDefault();
	}

	public void reload() {
		if (this.configFile == null) {
			this.configFile = new File(this.plugin.getDataFolder(), this.fileName);
		}

		this.dataConfig = YamlConfiguration.loadConfiguration(this.configFile);

		InputStream defaultStream = this.plugin.getResource(this.fileName);
		if (defaultStream != null) {
			YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(defaultStream));
			this.dataConfig.setDefaults(defaultConfig);
		}
	}

	public FileConfiguration getFile() {
		if (this.dataConfig == null)
			reload();

		return this.dataConfig;
	}

	public void save() {
		if (this.dataConfig == null || this.configFile == null)
			return;
		try {
			this.getFile().save(this.configFile);
		} catch (IOException e) {
			this.plugin.getLogger().log(Level.WARNING, "Could not save confg to " + this.configFile, e);
		}
	}

	public void saveDefault() {
		if (this.configFile == null)
			this.configFile = new File(this.plugin.getDataFolder(), this.fileName);

		if (!this.configFile.exists()) {
			this.plugin.saveResource(fileName, false);
		}
	}
}
