package me.aglerr.automaticfarms.configs;

import me.aglerr.automaticfarms.AutomaticFarms;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class TemporaryDataConfiguration {

    private FileConfiguration data;
    private File cfg;

    private final AutomaticFarms plugin;
    public TemporaryDataConfiguration(final AutomaticFarms plugin){
        this.plugin = plugin;
    }

    public void setup() {
        if(!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdir();
        }

        cfg = new File(plugin.getDataFolder(), "temp_data.yml");

        if(!cfg.exists()) {
            plugin.saveResource("temp_data.yml", false);
        }

        data = YamlConfiguration.loadConfiguration(cfg);

    }

    public FileConfiguration getConfiguration() {
        return data;
    }

    public void saveData() {
        try {
            data.save(cfg);
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public void reloadData() {
        data = YamlConfiguration.loadConfiguration(cfg);
    }

}
