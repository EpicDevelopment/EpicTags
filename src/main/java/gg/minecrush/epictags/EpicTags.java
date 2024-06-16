package gg.minecrush.epictags;

import gg.minecrush.epictags.api.TagExpansion;
import gg.minecrush.epictags.commands.admintags.AdminTagCommand;
import gg.minecrush.epictags.commands.admintags.AdminTagComplete;
import gg.minecrush.epictags.commands.tags.GUI.GuiHandler;
import gg.minecrush.epictags.commands.tags.GUI.ListTags;
import gg.minecrush.epictags.commands.tags.TagCommand;
import gg.minecrush.epictags.database.json.Tags;
import gg.minecrush.epictags.database.sqlite.SQLite;
import gg.minecrush.epictags.database.yaml.Config;
import gg.minecrush.epictags.database.yaml.Messages;
import gg.minecrush.epictags.listeners.JoinClass;
import org.bukkit.Bukkit;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class EpicTags extends JavaPlugin {

    private SQLite sqLite;
    private Messages messagesConfig;
    private Config config;
    private Tags tags;

    @Override
    public void onEnable() {
        try {
            File configFiles = new File(getDataFolder(), "config.yml");
            if (!configFiles.exists()) {
                saveResource("config.yml", false);
            }
        } catch (Exception e) {
            getLogger().severe("[EpicTags] Failed to create configuration file");
            Bukkit.getPluginManager().disablePlugin(this);
        }
        this.config = new Config(this);

        try {
            File msgFile = new File(getDataFolder(), "language.yml");
            if (!msgFile.exists()) {
                saveResource("language.yml", false);
            }
        } catch (Exception e) {
            getLogger().severe("[EpicTags] Failed to create language file");
            Bukkit.getPluginManager().disablePlugin(this);
        }



        try {
            if (!getDataFolder().exists()) {
                getDataFolder().mkdir();
            }
            sqLite = new SQLite(getDataFolder().getAbsolutePath() + "/database.db");
            if (sqLite != null) {
                getLogger().info("SQLite initialized successfully.");
            } else {
                getLogger().severe("SQLite initialization failed.");
                Bukkit.getPluginManager().disablePlugin(this);
            }
        } catch (Exception e) {
            getLogger().severe("SQLite initialization failed.");
            Bukkit.getPluginManager().disablePlugin(this);
        }

        if (sqLite != null){
            this.config = new Config(this);
            this.messagesConfig = new Messages(this, config);
            this.tags = new Tags(this);

            if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
                new TagExpansion(this, sqLite, tags).register();
            } else {
                getLogger().severe("-------------------------------------");
                getLogger().severe("While looking for PlaceholderAPI");
                getLogger().severe("");
                getLogger().severe("Could not find PlaceholderAPI! This plugin is required.");
                getLogger().severe("-------------------------------------");
                Bukkit.getPluginManager().disablePlugin(this);
            }

            this.getCommand("admintags").setExecutor(new AdminTagCommand(tags, messagesConfig, sqLite, config));
            this.getCommand("admintags").setTabCompleter(new AdminTagComplete(tags));
            registerPermission(config.getValue("admin-command-permission"), "Manage tags", PermissionDefault.OP);
            this.getCommand("tags").setExecutor(new TagCommand(new ListTags(sqLite, messagesConfig, tags)));

            this.getServer().getPluginManager().registerEvents(new JoinClass(sqLite), this);
            this.getServer().getPluginManager().registerEvents(new GuiHandler(tags, sqLite, messagesConfig, new ListTags(sqLite, messagesConfig, tags)), this);
        }

    }


    public void registerPermission(String name, String description, PermissionDefault defaultValue) {
        Permission permission = new Permission(name, description, defaultValue);
        Bukkit.getPluginManager().addPermission(permission);
    }

    public void unregisterPermission(String name, String description, PermissionDefault defaultValue){
        Permission permission = new Permission(name, description, defaultValue);
        Bukkit.getPluginManager().removePermission(permission);
    }

    @Override
    public void onDisable() {
        unregisterPermission(config.getValue("admin-command-permission"), "Manage tags", PermissionDefault.OP);
        try {
            sqLite.closeConnect();
        } catch (Exception e) {
            getLogger().severe("[EpicTags] Failed to close database");
        }
    }
}
