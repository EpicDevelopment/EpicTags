package gg.minecrush.epictags.commands.admintags;

import gg.minecrush.epictags.database.json.Tags;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class AdminTagComplete implements TabCompleter {

    private Tags tags;

    public AdminTagComplete(Tags tags) {
        this.tags = tags;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> list = new ArrayList<String>();
        if (args.length == 1) {
            list.add("create");
            list.add("delete");
            list.add("list");
            list.add("reload");
            list.add("grant");
            list.add("revoke");
            list.add("help");
            return list;
        }
        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("create")) {
                list.add("<name>");
            }

            if (args[0].equalsIgnoreCase("delete")) {
                list.add("<name>");
            }
            if (args[0].equalsIgnoreCase("grant")) {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    list.add(player.getName());
                }
            }
            if (args[0].equalsIgnoreCase("revoke")) {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    list.add(player.getName());
                }
            }
            return list;
        }
        if (args.length == 3) {
            if (args[0].equalsIgnoreCase("create")) {
                list.add("<tag>");
            }

            if (args[0].equalsIgnoreCase("grant")) {
                List<String> tag = tags.tagList();
                for (String s : tag) {
                    list.add(s);
                }
            }

            if (args[0].equalsIgnoreCase("revoke")) {
                List<String> tag = tags.tagList();
                for (String s : tag) {
                    list.add(s);
                }
            }
            return list;
        }

        return null;
    }
}
