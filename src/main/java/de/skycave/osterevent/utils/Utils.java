package de.skycave.osterevent.utils;

import de.skycave.osterevent.OsterEvent;
import de.skycave.osterevent.enums.Message;
import de.skycave.osterevent.enums.PlayerMode;
import de.skycave.osterevent.models.Gift;
import net.kyori.adventure.text.Component;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.util.List;
import java.util.StringJoiner;

public class Utils {

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

    /**
     * Transforms a location into a string containing the x, y and z components of the location.
     * @param location The location to transform
     * @return The result
     */
    public static @NotNull String locationAsString(@NotNull Location location) {
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        return decimalFormat.format(location.getX()) + ", " +
                decimalFormat.format(location.getY()) + ", " +
                decimalFormat.format(location.getZ());
    }

    public static @NotNull String itemStacksAsString(@NotNull List<ItemStack> itemStacks, String delimiter) {
        StringJoiner sj = new StringJoiner(delimiter);
        for (ItemStack item : itemStacks) {
            sj.add(item.getAmount() + "x " + enumObjectAsString(item.getType()));
        }
        return sj.toString();
    }

    /**
     * Transforms an enum name into a friendly name.
     * @param object The enum object
     * @return The result
     */
    public static String enumObjectAsString(@NotNull Enum<?> object) {
        StringJoiner sj = new StringJoiner(" ");
        for (String part : object.toString().split("_")) {
            sj.add(StringUtils.capitalize(part.toLowerCase()));
        }
        return sj.toString();
    }

}
