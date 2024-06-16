package gg.minecrush.epictags.commands.admintags;

import gg.minecrush.epictags.database.json.Tags;
import gg.minecrush.epictags.database.sqlite.SQLite;
import gg.minecrush.epictags.database.yaml.Config;
import gg.minecrush.epictags.database.yaml.Messages;
import gg.minecrush.epictags.utils.color;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class AdminTagCommand implements CommandExecutor {

    private final Tags tags;
    private final SQLite sqLite;
    private final Messages messages;
    private final Config config;

    public AdminTagCommand(Tags tags, Messages message, SQLite sqLite, Config config) {
        this.tags = tags;
        this.messages = message;
        this.sqLite = sqLite;
        this.config = config;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(messages.getMessages("admin-invalid-command"));
            return false;
        }

        if (!sender.hasPermission(config.getValue("admin-command-permission"))){
            sender.sendMessage(messages.getMessages("no-permission"));
            return false;
        }

        String subCommands = args[0].toLowerCase();
        switch (subCommands){
            case "create":
                handleCreateCommand(sender, args);
                break;
            case "delete":
                handleDeleteCommand(sender, args);
                break;
            case "grant":
                handleGrantCommand(sender, args);
                break;
            case "revoke":
                handleRevokeCommand(sender, args);
                break;
            case "list":
                handleListCommand(sender, args);
                break;
            case "reload":
                handleReloadCommand(sender, args);
                break;
            case "help":
                handleHelpCommand(sender, args);
                break;
            default:
                sender.sendMessage(messages.getMessages("admin-invalid-command"));
                break;
        }
        return true;
    }


    private void handleCreateCommand(CommandSender sender, String[] args){
        if (args.length != 3){
            sender.sendMessage(messages.getMessages("admin-invalid-command"));
            return;
        }

        String tag = args[1].toLowerCase();
        String display = String.join(" ", Arrays.copyOfRange(args, 2, args.length));
        if (tags.getTag(tag).isBlank()){
            tags.setTag(tag, color.c(display));
            sender.sendMessage(messages.getMessages("admin-tag-success").replace("%tag%", tag));
        } else {
            sender.sendMessage(messages.getMessages("tag-exist").replace("%tag%", tag));
        }
    }

    private void handleDeleteCommand(CommandSender sender, String[] args){
        if (args.length != 2){
            sender.sendMessage(messages.getMessages("admin-invalid-command"));
            return;
        }

        String tag = args[1].toLowerCase();
        if (tags.getTag(tag).isBlank()){
            sender.sendMessage(messages.getMessages("tag-doesnt-exist").replace("%tag%", tag));
            return;
        } else {
            sender.sendMessage(messages.getMessages("admin-tag-deleted").replace("%tag%", tag));
            try {
                List<UUID> playersWithTag = sqLite.getPlayersWithTag(tag);
                for (UUID uuid : playersWithTag) {
                    if (sqLite.getTag(Bukkit.getOfflinePlayer(uuid)).equals(tag)) {
                        sqLite.updateTag(Bukkit.getOfflinePlayer(uuid), "");
                    }
                    sqLite.removeTags(uuid, tag);
                }
            } catch (SQLException e){
                e.printStackTrace();
            }
            tags.deleteTag(tag);
        }
    }

    private void handleGrantCommand(CommandSender sender, String[] args){
        if (args.length != 3){
            sender.sendMessage(messages.getMessages("admin-invalid-command"));
            return;
        }

        String tag = args[2].toLowerCase();
        if (tags.getTag(tag).isBlank()){
            sender.sendMessage(messages.getMessages("tag-doesnt-exist").replace("%tag%", tag));
            return;
        }
        String target = args[1];
        OfflinePlayer targetPlayer = Bukkit.getOfflinePlayer(target);
        if (targetPlayer == null){
            sender.sendMessage(messages.getMessages("invalid-player").replace("%player%", target));
            return;
        }

        try {
            if (sqLite.hasTag(targetPlayer.getUniqueId(), tag)){
                sender.sendMessage(messages.getMessages("player-has-tag").replace("%tag%", tag).replace("%player%", targetPlayer.getName()));
                return;
            }


            sqLite.addTags(targetPlayer.getUniqueId(), tag);
            sender.sendMessage(messages.getMessages("admin-tag-granted").replace("%tag%", tag).replace("%player%", targetPlayer.getName()));

            if (Bukkit.getPlayer(target) != null){
                Player p = Bukkit.getPlayer(target);
                p.sendMessage(messages.getMessages("admin-tag-recieved").replace("%tag%", tag).replace("%player%", targetPlayer.getName()));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void handleRevokeCommand(CommandSender sender, String[] args){
        if (args.length != 3){
            sender.sendMessage(messages.getMessages("admin-invalid-command"));
            return;
        }

        String tag = args[2].toLowerCase();
        if (tags.getTag(tag).isBlank()){
            sender.sendMessage(messages.getMessages("tag-doesnt-exist").replace("%tag%", tag));
            return;
        }
        String target = args[1];
        OfflinePlayer targetPlayer = Bukkit.getOfflinePlayer(target);
        if (targetPlayer == null){
            sender.sendMessage(messages.getMessages("invalid-player").replace("%player%", target));
            return;
        }

        try {
            if (!sqLite.hasTag(targetPlayer.getUniqueId(), tag)){
                sender.sendMessage(messages.getMessages("player-doesnt-have-tag").replace("%tag%", tag).replace("%player%", targetPlayer.getName()));
                return;
            }
            if (sqLite.getTag(targetPlayer) == tag){
                sqLite.updateTag(targetPlayer, "");
            }

            sqLite.removeTags(targetPlayer.getUniqueId(), tag);
            sender.sendMessage(messages.getMessages("admin-tag-removed").replace("%tag%", tag).replace("%player%", targetPlayer.getName()));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void handleListCommand(CommandSender sender, String[] args){
        if (args.length != 1){
            sender.sendMessage(messages.getMessages("admin-invalid-command"));
            return;
        }
        sender.sendMessage(messages.getMessages("admin-tag-list").replace("%tags%", tags.listTags()));
    }

    private void handleHelpCommand(CommandSender sender, String[] args){
        if (args.length != 1){
            sender.sendMessage(messages.getMessages("admin-invalid-command"));
            return;
        }
        sender.sendMessage(messages.getMessages("admin-tag-help"));
    }

    private void handleReloadCommand(CommandSender sender, String[] args){
        if (args.length != 1){
            sender.sendMessage(messages.getMessages("admin-invalid-command"));
            return;
        }

        long startTime = System.currentTimeMillis();
        messages.reloadConfig();
        tags.reloadConfig();
        config.reloadConfig();
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        sender.sendMessage(messages.getReplacedMessage("admin-reload-command").replace("%time%", Long.toString(duration)));

    }

    // To do:
    // Got to test some of thease commands
}
