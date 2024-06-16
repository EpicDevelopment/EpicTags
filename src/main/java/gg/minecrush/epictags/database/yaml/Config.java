package gg.minecrush.epictags.database.yaml;

import gg.minecrush.epictags.utils.color;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Config {
    private final Plugin plugin;
    private File configFile;
    private FileConfiguration config;

    public Config(Plugin plugin) {
        this.plugin = plugin;
        createConfig();
    }

    private void createConfig() {
        configFile = new File(plugin.getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            plugin.saveResource("config.yml", false);
        }
        config = YamlConfiguration.loadConfiguration(configFile);
    }

    public String getValue(String key) {
        String message = config.getString(key);
        if (message == null) {
            return "";
        }
        return color.c(message);
    }

    public void reloadConfig() {
        if (configFile == null) {
            configFile = new File(plugin.getDataFolder(), "config.yml");
        }
        config = YamlConfiguration.loadConfiguration(configFile);
    }

    public void saveConfig() {
        try {
            config.save(configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
