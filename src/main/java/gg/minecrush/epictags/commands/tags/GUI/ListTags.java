package gg.minecrush.epictags.commands.tags.GUI;

import gg.minecrush.epictags.database.json.Tags;
import gg.minecrush.epictags.database.sqlite.SQLite;
import gg.minecrush.epictags.database.yaml.Config;
import gg.minecrush.epictags.database.yaml.Messages;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Objects;

public class ListTags {
    private final SQLite sqLite;
    private final Messages messages;
    private final int ITEMS_PER_PAGE = 28; // 7 items per row, 4 rows for tags
    private final Tags tags;

    public ListTags(SQLite sqLite, Messages messages, Tags tag) {
        this.sqLite = sqLite;
        this.messages = messages;
        this.tags = tag;
    }

    public void createTagsGui(Player player, int page) {
        Inventory tagsGUI = Bukkit.createInventory(null, 54, "Tags - Page " + (page + 1));

        ItemStack darkGlass = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta darkGlassMeta = darkGlass.getItemMeta();
        if (darkGlassMeta != null) {
            darkGlassMeta.setDisplayName(" ");
            darkGlass.setItemMeta(darkGlassMeta);
        }

        // Set border panes
        for (int i = 0; i < 9; i++) {
            tagsGUI.setItem(i, darkGlass);
        }
        for (int i = 45; i < 54; i++) {
            tagsGUI.setItem(i, darkGlass);
        }
        for (int i = 0; i <= 45; i += 9) {
            tagsGUI.setItem(i, darkGlass);
        }
        for (int i = 8; i <= 53; i += 9) {
            tagsGUI.setItem(i, darkGlass);
        }

        try {
            List<String> playerTags = sqLite.getTags(player.getUniqueId());
            int totalItems = playerTags.size();
            int totalPages = (int) Math.ceil((double) totalItems / ITEMS_PER_PAGE);

            int start = page * ITEMS_PER_PAGE;
            int end = Math.min(start + ITEMS_PER_PAGE, totalItems);

            int slot = 10;
            for (int i = start; i < end; i++) {
                String tagName = playerTags.get(i);

                ItemStack tagItem = new ItemStack(Material.NAME_TAG);
                ItemMeta tagMeta = tagItem.getItemMeta();
                if (tagMeta != null) {
                    tagMeta.setDisplayName(messages.getMessages("tags-gui.tag.name").replace("%name%", tagName + " tag").replace("%tag%", tags.getTag(tagName)));

                    String toggleMessage = "";
                    try {
                        if (Objects.equals(sqLite.getTag(player), tagName)){
                            toggleMessage = "&cClick to disable " + tagName;
                        } else {
                            toggleMessage = "&aClick to enable " + tagName;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    tagMeta.setLore(messages.getReplacedArrayMessage("tags-gui.tag.description", tagName, tags.getTag(tagName), toggleMessage));
                    tagItem.setItemMeta(tagMeta);

                    while ((slot % 9 == 0 || slot % 9 == 8) && slot < 45) {
                        slot++;
                    }

                    if (slot >= 45) {
                        break;
                    }

                    tagsGUI.setItem(slot, tagItem);
                    slot++;
                }
            }

            ItemStack closeItem = new ItemStack(Material.BARRIER);
            ItemMeta closeMeta = closeItem.getItemMeta();
            if (closeMeta != null) {
                closeMeta.setDisplayName(messages.getMessages("tags-gui.close"));
                closeItem.setItemMeta(closeMeta);
            }
            tagsGUI.setItem(49, closeItem);

            if (page > 0) {
                ItemStack previousPage = new ItemStack(Material.ARROW);
                ItemMeta previousPageMeta = previousPage.getItemMeta();
                if (previousPageMeta != null) {
                    previousPageMeta.setDisplayName(messages.getMessages("tags-gui.previousPage"));
                    previousPage.setItemMeta(previousPageMeta);
                }
                tagsGUI.setItem(48, previousPage);
            }

            if (page < totalPages - 1) {
                ItemStack nextPage = new ItemStack(Material.ARROW);
                ItemMeta nextPageMeta = nextPage.getItemMeta();
                if (nextPageMeta != null) {
                    nextPageMeta.setDisplayName(messages.getMessages("tags-gui.nextPage"));
                    nextPage.setItemMeta(nextPageMeta);
                }
                tagsGUI.setItem(50, nextPage);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        player.openInventory(tagsGUI);
    }

    // To do:
    // Make a better GUI system

    // To test:
    // Got to test if the arrows actually work
}