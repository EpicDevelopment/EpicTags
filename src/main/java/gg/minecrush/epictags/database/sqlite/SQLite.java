package gg.minecrush.epictags.database.sqlite;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.lang.reflect.Type;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SQLite {
    private Connection connection;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final Type listType = new TypeToken<List<UUID>>() {}.getType();

    public SQLite(String path) throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite:" + path);
        try (Statement statement = connection.createStatement()) {
            statement.execute("""
                CREATE TABLE IF NOT EXISTS players (
                    uuid TEXT PRIMARY KEY,
                    username TEXT NOT NULL,
                    tag TEXT DEFAULT '',
                    tags TEXT DEFAULT '[]'
                )
            """);
        }
    }

    public void updateTag(OfflinePlayer p, String tag) throws SQLException {
        if (!playerExists(p.getUniqueId().toString())){
            registerPlayer(p);
        } else {
            try (PreparedStatement ps = connection.prepareStatement("UPDATE players SET tag = ? WHERE uuid = ?")){
                ps.setString(1, tag);
                ps.setString(2, p.getUniqueId().toString());
                ps.executeUpdate();
            }
        }
    }

    public List<UUID> getPlayersWithTag(String tag) throws SQLException {
        List<UUID> players = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement("SELECT uuid FROM players WHERE tags LIKE ?")) {
            ps.setString(1, "%" + tag + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    players.add(UUID.fromString(rs.getString("uuid")));
                }
            }
        }
        return players;
    }

    public String getTag(OfflinePlayer p) throws SQLException {
        if (playerExists(p.getUniqueId().toString())) {
            try (PreparedStatement ps = connection.prepareStatement("SELECT tag FROM players WHERE uuid = ?")) {
                ps.setString(1, p.getUniqueId().toString());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return rs.getString("tag");
                    }
                }
            }
        }
        return "";
    }


    public boolean playerExists(String uuid) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement("SELECT * FROM players WHERE uuid = ?")){
            ps.setString(1, uuid);
            try (ResultSet rs = ps.executeQuery()){
                if (rs.next()){
                    return true;
                }
                return false;
            }
        }
    }

    public void registerPlayer(OfflinePlayer p) throws SQLException {
        if (!playerExists(p.getName())) {
            try (PreparedStatement ps = connection.prepareStatement(("INSERT INTO players (uuid, username) VALUES(?, ?)"))){
                ps.setString(1, p.getUniqueId().toString());
                ps.setString(2, p.getName());
                ps.executeUpdate();
            }
        }
    }

    public void closeConnect() throws SQLException {
        if (connection != null && !connection.isClosed()){
            connection.close();
        }
    }

    public void addTags(UUID uuid, String newTag) throws SQLException {
        List<String> tags = getTags(uuid);
        if (!tags.contains(newTag)) {
            tags.add(newTag);
            updateTags(uuid, tags);
        }
    }

    public void removeTags(UUID uuid, String tagToRemove) throws SQLException {
        List<String> tags = getTags(uuid);
        if (tags.contains(tagToRemove)) {
            tags.remove(tagToRemove);
            updateTags(uuid, tags);
        }
    }

    public void updateTags(UUID uuid, List<String> tags) throws SQLException {
        String tagsJson = gson.toJson(tags);
        try (PreparedStatement ps = connection.prepareStatement("UPDATE players SET tags = ? WHERE uuid = ?")) {
            ps.setString(1, tagsJson);
            ps.setString(2, uuid.toString());
            ps.executeUpdate();
        }
    }

    public boolean hasTag(UUID uuid, String tag) throws SQLException {
        List<String> tags = getTags(uuid);
        return tags.contains(tag);
    }

    public List<String> getTags(UUID uuid) throws SQLException {
        if (!playerExists(uuid.toString())) {
            registerPlayer(Bukkit.getOfflinePlayer(uuid));
        }
        try (PreparedStatement ps = connection.prepareStatement("SELECT tags FROM players WHERE uuid = ?")) {
            ps.setString(1, uuid.toString());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String tagsJson = rs.getString("tags");
                    return gson.fromJson(tagsJson, new TypeToken<List<String>>() {}.getType());
                }
            }
        }
        return new ArrayList<>();
    }

}
