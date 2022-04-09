package de.skycave.osterevent.listeners;

import com.mongodb.client.model.Filters;
import de.skycave.osterevent.OsterEvent;
import de.skycave.osterevent.enums.Message;
import de.skycave.osterevent.enums.PlayerMode;
import de.skycave.osterevent.models.Reward;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
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
        Reward reward = main.getRewardCache().get(uuid);
        if (reward == null) {
            return;
        }

        List<ItemStack> newRewards = new ArrayList<>();
        for (ItemStack item : event.getInventory()) {
            newRewards.add(item);
        }
        reward.setRewards(newRewards);
        main.getRewards().replaceOne(Filters.eq("_id", reward.getObjectId()), reward);
        Message.EDIT_SUCCESS.get().replace("%id", "" + reward.getSerialId()).send(player);
    }

}
