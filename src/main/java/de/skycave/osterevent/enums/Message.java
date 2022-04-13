package de.skycave.osterevent.enums;

import de.skycave.osterevent.OsterEvent;
import de.skycave.osterevent.models.ChatMessage;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public enum Message {

    PLAYER_ONLY("&cDieser Befehl ist nur als Spieler ausführbar."),
    GIFT_NONEXISTENT("&cEin Geschenk mit dieser Nummer existiert nicht."),
    INVALID_NUMBER("&cBitte gib eine gültige Zahl an."),

    // help
    HELP_CREATE("&a/ostern create <Einmalig> &8- &7Beginnt mit der Erstellung eines Geschenkes"),
    HELP_EDIT("&a/ostern edit <Nummer> &8- &7Editiert die Belohnungen eines Geschenkes"),
    HELP_MOVE("&a/ostern move <Nummer> &8- &7Ändert die Position eines Geschenkes"),
    HELP_DELETE("&a/ostern delete <Nummer> &8- &7Entfernt ein Geschenk (Alternativ: SHIFT + Abbauen)"),
    HELP_LIST("&a/ostern list &8- &7Listet alle Geschenke auf"),
    HELP_INFO("&a/ostern info <Nummer> &8- &7Zeigt Informationen über ein Geschenk an"),
    HELP_CANCEL("&a/ostern cancel &8- &7Bricht die aktuelle Aktion ab"),
    HELP_RESET("&a/ostern reset &8- &7Setzt ALLE Userdaten zurück"),
    HELP_HELP("&a/ostern help &8- &7Zeigt Hilfe an"),

    // create
    CREATE_START("&aErstellung eines neuen Geschenkes mit der Nummer &2%id &agestartet. &7Klicke auf einen Block, um die Position zu setzen."),
    CREATE_ONLY_ONCE("&eDas Geschenk wird nur von einem Spieler einlösbar sein."),

    // edit
    EDIT_SYNTAX("&e/ostern edit <Nummer>"),
    EDIT_START("&7Lege die Belohnungen in das geöffnete Inventar hinein."),
    EDIT_TITLE("&2Belohnungen hier ablegen"),
    EDIT_SUCCESS("&aBelohnungen für Geschenk Nummer &2%id &aerfolgreich angepasst."),

    // move
    MOVE_SYNTAX("&e/ostern move <Nummer>"),
    MOVE_START("&7Klicke auf einen Block, um die Position zu setzen."),
    MOVE_SUCCESS("&aPosition für Geschenk Nummer &2%id &awurde erfolgreich angepasst."),

    // delete
    DELETE_SYNTAX("&e/ostern delete <Nummer>"),
    DELETE_SUCCESS("&aDas Geschenk mit der Nummer &2%id &awurde erfolgreich entfernt."),
    DELETE_SNEAK("&cSneaken + Abbauen, um das Geschenk zu entfernen."),

    // info
    INFO_SYNTAX("&e/ostern info <Nummer>"),

    // list
    LIST_HEADER_FOOTER("&8>&7------------&8[ &a%page&2/&a%amount &8]&7------------&8<"),
    LIST_ENTRY("&6&l%id &8- &a%rewards &8(&7Koordinaten: &e%location&8)"),
    LIST_NO_ENTRIES("&cEs sind keine Einträge vorhanden."),

    // info
    INFO_HEADER("&2Info für Geschenk Nummer %id"),
    INFO_LOCATION("&aPosition: &7%location"),
    INFO_REWARDS("&aBelohnungen: &7%rewards"),
    INFO_STATE("&aNur von einem Spieler abholbar: &7%yesno"),

    // cancel
    CANCEL("&cAktion wurde abgebrochen."),
    CANCEL_NONE("&cDu hast keine Aktion, die du abbrechen kannst."),

    // reset
    RESET_DONE("&eALLE Userdaten wurden zurückgesetzt."),

    // claim
    CLAIM_ALREADY_ONCE("&cDieses Geschenk ist nur von einem Spieler einlösbar und wurde bereits gefunden."),
    CLAIM_ALREADY("&cDu hast dieses Geschenk bereits gefunden."),
    CLAIM("&aDu hast ein Geschenk gefunden! &7Du erhältst folgende Belohnungen: &e%rewards"),
    CLAIM_ONCE("&6Dieses Geschenk war nur von einem einzigen Spieler einlösbar! Herzlichen Glückwunsch!"),
    CLAIM_NO_SPACE("&cDu hast nicht genug Platz, um die Belohnungen abzuholen. Du brauchst %amount freie Slots.")
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
