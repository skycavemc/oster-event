package de.skycave.osterevent.listeners;

import de.skycave.osterevent.OsterEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public class PlayerInteractListener implements Listener {

    private final OsterEvent main;

    public PlayerInteractListener(OsterEvent main) {
        this.main = main;
    }

    @EventHandler
    public void on(PlayerInteractEvent event) {
        // TODO create
        // TODO move
        // TODO get reward
    }

}
