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

    // create
    CREATE_START("&aErstellung eines neuen Geschenkes mit der Nummer &2%id &agestartet. Klicke auf einen Block, um die Position zu setzen."),
    CREATE_ONLY_ONCE("&eDas Geschenk wird nur von einem Spieler einlösbar sein."),

    // edit
    EDIT_SYNTAX("&e/ostern edit <Nummer>"),
    EDIT_START("&aLege die Belohnungen in das geöffnete Inventar hinein."),
    EDIT_TITLE("&2Belohnungen hier ablegen"),
    EDIT_SUCCESS("&aBelohnungen für Geschenk Nummer &2%id &aerfolgreich angepasst."),

    // move
    MOVE_SYNTAX("&e/ostern move <Nummer>"),
    MOVE_START("&aKlicke auf einen Block, um die Position zu setzen."),
    MOVE_SUCCESS("&aPosition für Geschenk Nummer &2%id &awurde erfolgreich angepasst."),

    // delete
    DELETE_SYNTAX("&e/ostern delete <Nummer>"),
    DELETE_SUCCESS("&aDas Geschenk mit der Nummer &2%id &awurde erfolgreich entfernt."),

    // info
    INFO_SYNTAX("&e/ostern info <Nummer>"),

    // cancel
    CANCEL("&cAktion wurde abgebrochen."),
    CANCEL_NONE("&cDu hast keine Aktion, die du abbrechen kannst."),

    // claim
    CLAIM_ALREADY_ONCE("&cDieses Geschenk ist nur von einem Spieler einlösbar und wurde bereits gefunden."),
    CLAIM_ALREADY("&cDu hast dieses Geschenk bereits gefunden."),
    CLAIM("&aDu hast ein Geschenk gefunden! Du erhältst folgende Belohnungen: &e%rewards"),
    CLAIM_ONCE("&6Dieses Geschenk war nur von einem einzigen Spieler einlösbar! Herzlichen Glückwunsch!"),
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
