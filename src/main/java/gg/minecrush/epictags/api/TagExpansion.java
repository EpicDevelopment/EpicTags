package gg.minecrush.epictags.api;

import gg.minecrush.epictags.EpicTags;
import gg.minecrush.epictags.database.json.Tags;
import gg.minecrush.epictags.database.sqlite.SQLite;
import gg.minecrush.epictags.utils.color;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;

import java.sql.SQLException;

public class TagExpansion extends PlaceholderExpansion {

    private final EpicTags plugin;

    private final Tags tags;

    private final SQLite sql;

    public TagExpansion(EpicTags plugin, SQLite sqLite, Tags tags) {
        this.plugin = plugin;
        this.tags = tags;
        this.sql = sqLite;
    }

    @Override
    public String getIdentifier() {
        return "epictags";
    }

    @Override
    public String getAuthor() {
        return plugin.getDescription().getAuthors().get(0);
    }

    @Override
    public String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public String onPlaceholderRequest(Player player, String identifier) {
        if (identifier.equalsIgnoreCase("tag")) {
            try {
                String tag = sql.getTag(player);
                if (tag != null && !tag.isBlank()) {
                    return color.c(tags.getTag(tag));
                }
                return "";
            } catch (SQLException e) {
                e.printStackTrace();
                return "";
            }
        }

        if (identifier.equalsIgnoreCase("epiccore_tag")) {
            try {
                String tag = sql.getTag(player);
                if (tag != null && !tag.isBlank()) {
                    return color.c(tags.getTag(tag)) + " ";
                }
                return "";
            } catch (SQLException e) {
                e.printStackTrace();
                return "";
            }
        }
        return "";
    }
}
