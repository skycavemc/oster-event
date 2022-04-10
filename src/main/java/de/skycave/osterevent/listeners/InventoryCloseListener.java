package de.skycave.osterevent.listeners;

import com.mongodb.client.model.Filters;
import de.skycave.osterevent.OsterEvent;
import de.skycave.osterevent.enums.Message;
import de.skycave.osterevent.enums.PlayerMode;
import de.skycave.osterevent.models.Gift;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class InventoryCloseListener implements Listener {

    private final OsterEvent main;

    public InventoryCloseListener(OsterEvent main) {
        this.main = main;
    }

    @EventHandler
    public void on(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        UUID uuid = player.getUniqueId();
        if (main.getPlayerModes().get(uuid) != PlayerMode.EDIT) {
            return;
        }
        if (!player.hasPermission("skycave.ostern")) {
            main.getPlayerModes().remove(uuid);
            return;
        }
        Gift gift = main.getGiftCache().get(uuid);
        if (gift == null) {
            return;
        }

        List<ItemStack> newRewards = new ArrayList<>();
        for (ItemStack item : event.getInventory()) {
            newRewards.add(item);
        }
        gift.setRewards(newRewards);
        main.getGifts().replaceOne(Filters.eq("_id", gift.getObjectId()), gift);
        Message.EDIT_SUCCESS.get().replace("%id", "" + gift.getSerialId()).send(player);
        main.getPlayerModes().remove(uuid);
        main.getGiftCache().remove(uuid);
    }

}
