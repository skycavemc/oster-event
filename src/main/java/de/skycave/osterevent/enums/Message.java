package de.skycave.osterevent.enums;

import de.skycave.osterevent.OsterEvent;
import de.skycave.osterevent.models.ChatMessage;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public enum Message {

    PLAYER_ONLY("&cDieser Befehl ist nur als Spieler ausführbar."),

    // cancel
    CANCEL("&cAktion wurde abgebrochen."),
    CANCEL_NONE("&cDu hast keine Aktion, die du abbrechen kannst."),
    ;

    private final String message;

    Message(String message) {
        this.message = message;
    }

    @Contract(" -> new")
    public @NotNull ChatMessage get() {
        return new ChatMessage(JavaPlugin.getPlugin(OsterEvent.class), message);
    }
}
