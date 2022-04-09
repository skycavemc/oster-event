package de.skycave.osterevent.enums;

import de.skycave.osterevent.OsterEvent;
import de.skycave.osterevent.models.ChatMessage;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public enum Message {

    PLAYER_ONLY("&cDieser Befehl ist nur als Spieler ausführbar."),
    REWARD_NONEXISTENT("&cEin Geschenk mit dieser Nummer existiert nicht."),
    INVALID_NUMBER("&cBitte gib eine gültige Zahl an."),

    // help
    HELP_CREATE("&a/ostern create <Einmalig> &8- &7Beginnt mit der Erstellung eines Geschenkes"),
    HELP_EDIT("&a/ostern edit <Nummer> &8- &7Editiert die Belohnungen eines Geschenkes"),
    HELP_MOVE("&a/ostern move <Nummer> &8- &7Ändert die Position eines Geschenkes"),
    HELP_DELETE("&a/ostern delete <Nummer> &8- &7Entfernt ein Geschenk"),
    HELP_LIST("&a/ostern list &8- &7Listet alle Geschenke auf"),
    HELP_INFO("&a/ostern info <Nummer> &8- &7Zeigt Informationen über ein Geschenk an"),
    HELP_CANCEL("&a/ostern cancel &8- &7Bricht die aktuelle Aktion ab"),
    HELP_HELP("&a/ostern help &8- &7Zeigt Hilfe an"),

    // edit
    EDIT_SYNTAX("&e/ostern edit <Nummer>"),

    // move
    MOVE_SYNTAX("&e/ostern move <Nummer>"),

    // delete
    DELETE_SYNTAX("&e/ostern delete <Nummer>"),

    // info
    INFO_SYNTAX("&e/ostern info <Nummer>"),

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
