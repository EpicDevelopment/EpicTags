package gg.minecrush.epictags.commands.tags;

import gg.minecrush.epictags.commands.tags.GUI.ListTags;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class TagCommand implements CommandExecutor {
    private ListTags listTags;

    public TagCommand(ListTags listTags) {
        this.listTags = listTags;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            listTags.createTagsGui(player, 0);
            return true;
        }
        return false;
    }
}
