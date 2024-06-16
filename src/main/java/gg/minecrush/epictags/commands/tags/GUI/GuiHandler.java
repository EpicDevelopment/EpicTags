package gg.minecrush.epictags.commands.tags.GUI;

import gg.minecrush.epictags.EpicTags;
import gg.minecrush.epictags.database.json.Tags;
import gg.minecrush.epictags.database.sqlite.SQLite;
import gg.minecrush.epictags.database.yaml.Messages;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.sql.SQLException;
import java.util.Objects;

public class GuiHandler implements Listener {

    private final Tags tags;
    private final SQLite sql;
    private final Messages messages;
    private final ListTags listTags;

    public GuiHandler(Tags tags, SQLite sqLite, Messages messages, ListTags listTags) {
        this.tags = tags;
        this.sql = sqLite;
        this.messages = messages;
        this.listTags = listTags;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getView().getTitle().startsWith("Tags")){
            event.setCancelled(true);
            Player player = (Player) event.getWhoClicked();
            ItemStack clickedItem = event.getCurrentItem();
            if (clickedItem != null){
                if (clickedItem.getType() != Material.BLACK_STAINED_GLASS){
                    if (clickedItem.getType() == Material.NAME_TAG){
                        ItemMeta meta = clickedItem.getItemMeta();
                        if (meta != null && meta.hasLore()){
                            String name = meta.getDisplayName();
                            String cleanName = name.replaceAll("§.", "");
                            String[] split = cleanName.split(" tag");
                            String names = "";
                            if (split.length > 0) {
                                names = split[0];
                                split[0] = name.replaceAll(" ", "");
                            }
                            try {
                                if (!Objects.equals(sql.getTag(player), names)){
                                    sql.updateTag(player, names);
                                    player.sendMessage(messages.getMessages("enabled-tag").replace("%tag%", names));
                                    player.closeInventory();
                                } else {
                                    sql.updateTag(player, "");
                                    player.sendMessage(messages.getMessages("disabled-tag").replace("%tag%", names));
                                    player.closeInventory();
                                }
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    if (clickedItem.getType() == Material.BARRIER){
                        player.closeInventory();
                    }

                    if (clickedItem.getType() == Material.ARROW) {
                        int currentPage = Integer.parseInt(event.getView().getTitle().split("Page ")[1]) - 1;
                        String displayName = clickedItem.getItemMeta().getDisplayName();
                        if (displayName.equals(messages.getMessages("tags-gui.nextPageName"))) {
                            listTags.createTagsGui(player, currentPage + 1);
                        } else if (displayName.equals(messages.getMessages("tags-gui.previousPageName"))) {
                            listTags.createTagsGui(player, currentPage - 1);
                        }
                    }
                }

                // To test:
                // Got to test if the Clicked arrows actually work
            }
        }
    }
}
