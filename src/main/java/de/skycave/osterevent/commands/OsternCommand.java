package de.skycave.osterevent.commands;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import de.skycave.osterevent.OsterEvent;
import de.skycave.osterevent.enums.GiftState;
import de.skycave.osterevent.enums.Message;
import de.skycave.osterevent.enums.PlayerMode;
import de.skycave.osterevent.models.Gift;
import de.skycave.osterevent.utils.Utils;
import org.bson.conversions.Bson;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class OsternCommand implements CommandExecutor, TabCompleter {

    private final OsterEvent main;

    public OsternCommand(OsterEvent main) {
        this.main = main;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label,
                             @NotNull String @NotNull [] args) {
        if (args.length < 1) {
            sendHelp(sender);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "create" -> {
                if (!(sender instanceof Player player)) {
                    Message.PLAYER_ONLY.get().send(sender);
                    return true;
                }
                Gift gift = new Gift();
                gift.setSerialId(main.getConfiguration().getInt("current_id") + 1);
                Message.CREATE_START.get().replace("%id", "" + gift.getSerialId()).send(player);
                if (args.length >= 2) {
                    gift.setGiftState(GiftState.CLAIMABLE_ONCE);
                    Message.CREATE_ONLY_ONCE.get().send(player);
                } else {
                    gift.setGiftState(GiftState.CLAIMABLE);
                }
                main.getGiftCache().put(player.getUniqueId(), gift);
                main.getPlayerModes().put(player.getUniqueId(), PlayerMode.CREATE);
            }
            case "edit" -> {
                if (!(sender instanceof Player player)) {
                    Message.PLAYER_ONLY.get().send(sender);
                    return true;
                }
                if (args.length < 2) {
                    Message.EDIT_SYNTAX.get().send(player);
                    break;
                }
                int id;
                try {
                    id = Integer.parseInt(args[1]);
                } catch (NumberFormatException e) {
                    Message.INVALID_NUMBER.get().send(player);
                    break;
                }
                Bson filter = Filters.eq("serial_id", id);
                Gift gift = main.getGifts().find(filter).first();
                if (gift == null) {
                    Message.GIFT_NONEXISTENT.get().send(player);
                    break;
                }
                main.getGiftCache().put(player.getUniqueId(), gift);
                Utils.startEdit(player, gift, main);
            }
            case "move" -> {
                if (!(sender instanceof Player player)) {
                    Message.PLAYER_ONLY.get().send(sender);
                    return true;
                }
                if (args.length < 2) {
                    Message.MOVE_SYNTAX.get().send(player);
                    break;
                }
                int id;
                try {
                    id = Integer.parseInt(args[1]);
                } catch (NumberFormatException e) {
                    Message.INVALID_NUMBER.get().send(player);
                    break;
                }
                Bson filter = Filters.eq("serial_id", id);
                Gift gift = main.getGifts().find(filter).first();
                if (gift == null) {
                    Message.GIFT_NONEXISTENT.get().send(player);
                    break;
                }
                main.getGiftCache().put(player.getUniqueId(), gift);
                main.getPlayerModes().put(player.getUniqueId(), PlayerMode.MOVE);
                Message.MOVE_START.get().send(player);
            }
            case "delete" -> {
                if (args.length < 2) {
                    Message.DELETE_SYNTAX.get().send(sender);
                    break;
                }
                int id;
                try {
                    id = Integer.parseInt(args[1]);
                } catch (NumberFormatException e) {
                    Message.INVALID_NUMBER.get().send(sender);
                    break;
                }
                Bson filter = Filters.eq("serial_id", id);
                Gift gift = main.getGifts().findOneAndDelete(filter);
                if (gift == null) {
                    Message.GIFT_NONEXISTENT.get().send(sender);
                    break;
                }
                Message.DELETE_SUCCESS.get().send(sender);
            }
            case "list" -> {
                int page;
                if (args.length < 2) {
                    page = 1;
                } else {
                    try {
                        page = Integer.parseInt(args[1]);
                    } catch (NumberFormatException e) {
                        Message.INVALID_NUMBER.get().send(sender);
                        break;
                    }
                }

                long entries = main.getGifts().countDocuments();
                if (entries == 0) {
                    Message.LIST_NO_ENTRIES.get().send(sender);
                    break;
                }

                int pages = (int) (entries / 10) + 1;
                page = Math.min(page, pages);
                int skip = (page - 1) * 10;

                List<Gift> gifts = main.getGifts().find()
                        .sort(Sorts.ascending("serial_id"))
                        .skip(skip)
                        .limit(10)
                        .into(new ArrayList<>());

                Message.LIST_HEADER_FOOTER.get().replace("%page", "" + page).send(sender);
                for (Gift gift : gifts) {
                    Message.LIST_ENTRY.get()
                            .replace("%id", "" + gift.getSerialId())
                            .replace("%rewards", Utils.itemStacksAsString(gift.getRewards(), "&7, &a"))
                            .replace("%location", Utils.locationAsString(gift.getLocation()))
                            .send(sender);
                }
                Message.LIST_HEADER_FOOTER.get().replace("%page", "" + page).send(sender);
            }
            case "info" -> {
                if (args.length < 2) {
                    Message.INFO_SYNTAX.get().send(sender);
                    break;
                }
                int id;
                try {
                    id = Integer.parseInt(args[1]);
                } catch (NumberFormatException e) {
                    Message.INVALID_NUMBER.get().send(sender);
                    break;
                }
                Bson filter = Filters.eq("serial_id", id);
                Gift gift = main.getGifts().find(filter).first();
                if (gift == null) {
                    Message.GIFT_NONEXISTENT.get().send(sender);
                    break;
                }
                Message.INFO_HEADER.get().replace("%id", "" + gift.getSerialId()).send(sender);
                Message.INFO_LOCATION.get().replace("%location",
                        Utils.locationAsString(gift.getLocation())).send(sender);
                Message.INFO_REWARDS.get().replace("%rewards",
                        Utils.itemStacksAsString(gift.getRewards(), ", ")).send(sender);
                Message.INFO_STATE.get().replace("%yesno",
                        gift.getGiftState() == GiftState.CLAIMABLE ? "Nein" : "Ja").send(sender);
            }
            case "cancel" -> {
                if (!(sender instanceof Player player)) {
                    Message.PLAYER_ONLY.get().send(sender);
                    return true;
                }
                if (main.getPlayerModes().containsKey(player.getUniqueId())) {
                    main.getPlayerModes().remove(player.getUniqueId());
                    Message.CANCEL.get().send(player);
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1.0f, .8f);
                } else {
                    Message.CANCEL_NONE.get().send(player);
                }
            }
            case "help" -> sendHelp(sender);
        }
        return true;
    }

    private void sendHelp(CommandSender sender) {
        Message.HELP_CREATE.get().send(sender, false);
        Message.HELP_EDIT.get().send(sender, false);
        Message.HELP_MOVE.get().send(sender, false);
        Message.HELP_DELETE.get().send(sender, false);
        Message.HELP_LIST.get().send(sender, false);
        Message.HELP_INFO.get().send(sender, false);
        Message.HELP_CANCEL.get().send(sender, false);
        Message.HELP_HELP.get().send(sender, false);
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
                                                @NotNull String alias, @NotNull String @NotNull [] args) {
        List<String> arguments = new ArrayList<>();
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            arguments.add("create");
            arguments.add("edit");
            arguments.add("move");
            arguments.add("delete");
            arguments.add("list");
            arguments.add("info");
            arguments.add("cancel");
            arguments.add("help");
            StringUtil.copyPartialMatches(args[0], arguments, completions);
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("create")) {
                arguments.add("einmalig");
            }
            StringUtil.copyPartialMatches(args[1], arguments, completions);
        }

        Collections.sort(completions);
        return completions;
    }

}
