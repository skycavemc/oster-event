package de.skycave.osterevent.utils;

import de.skycave.osterevent.OsterEvent;
import de.skycave.osterevent.enums.Message;
import de.skycave.osterevent.enums.PlayerMode;
import de.skycave.osterevent.models.Gift;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class PlayerUtils {

    /**
     * Initiates the edit mode for modifying the rewards of a gift.
     * @param player The player to start the edit mode for
     * @param gift The gift to modify
     * @param main The main class
     */
    public static void startEdit(@NotNull Player player, @NotNull Gift gift, @NotNull OsterEvent main) {
        main.getPlayerModes().put(player.getUniqueId(), PlayerMode.EDIT);
        Message.EDIT_START.get().send(player);
        Inventory inv = Bukkit.createInventory(null, 9,
                Component.text(Message.EDIT_TITLE.get().get(false)));
        for (ItemStack item : gift.getRewards()) {
            inv.addItem(item);
        }
        player.openInventory(inv);
    }

}
