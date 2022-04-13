package de.skycave.osterevent.listeners;

import com.mongodb.client.model.Filters;
import de.skycave.osterevent.OsterEvent;
import de.skycave.osterevent.enums.Message;
import de.skycave.osterevent.models.Gift;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.jetbrains.annotations.NotNull;

public class BlockBreakListener implements Listener {

    private final OsterEvent main;

    public BlockBreakListener(OsterEvent main) {
        this.main = main;
    }

    @EventHandler
    public void on(@NotNull BlockBreakEvent event) {
        Gift gift = main.getGifts().find(
                Filters.eq("location", event.getBlock().getLocation())
        ).first();
        if (gift == null) return;

        Player player = event.getPlayer();
        if (!player.hasPermission("skycave.ostern")) {
            event.setCancelled(true);
            return;
        }

        if (!player.isSneaking()) {
            Message.DELETE_SNEAK.get().send(player);
            event.setCancelled(true);
            return;
        }

        main.getGifts().deleteOne(Filters.eq("_id", gift.getSerialId()));
        Message.DELETE_SUCCESS.get().replace("%id", "" + gift.getSerialId()).send(player);
    }

}
