package gg.minecrush.epictags.listeners;

import gg.minecrush.epictags.database.sqlite.SQLite;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinClass implements Listener {
    private final SQLite sqLite;

    public JoinClass(SQLite sqLite) {
        this.sqLite = sqLite;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        try {
            if (!sqLite.playerExists(player.getUniqueId().toString())) {
                sqLite.registerPlayer(player);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
