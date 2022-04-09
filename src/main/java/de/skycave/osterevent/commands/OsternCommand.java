package de.skycave.osterevent.commands;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import de.skycave.osterevent.OsterEvent;
import de.skycave.osterevent.enums.Message;
import de.skycave.osterevent.enums.PlayerMode;
import de.skycave.osterevent.models.Reward;
import net.kyori.adventure.text.Component;
import org.bson.conversions.Bson;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
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
                Reward reward = new Reward();
                reward.setOnlyOnce(args.length >= 2);
                reward.setSerialId(main.getConfiguration().getInt("current_id") + 1);
                Message.CREATE_START.get().replace("%id", "" + reward.getSerialId()).send(player);
                if (args.length >= 2) {
                    Message.CREATE_ONLY_ONCE.get().send(player);
                }
                main.getRewardCache().put(player.getUniqueId(), reward);
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
                Reward reward = main.getRewards().find(filter).first();
                if (reward == null) {
                    Message.REWARD_NONEXISTENT.get().send(player);
                    break;
                }
                main.getRewardCache().put(player.getUniqueId(), reward);
                main.getPlayerModes().put(player.getUniqueId(), PlayerMode.EDIT);
                Message.EDIT_START.get().send(player);
                Inventory inv = Bukkit.createInventory(null, 9,
                        Component.text(Message.EDIT_TITLE.get().get(false)));
                for (ItemStack item : reward.getRewards()) {
                    inv.addItem(item);
                }
                player.openInventory(inv);
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
                Reward reward = main.getRewards().find(filter).first();
                if (reward == null) {
                    Message.REWARD_NONEXISTENT.get().send(player);
                    break;
                }
                main.getRewardCache().put(player.getUniqueId(), reward);
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
                Reward reward = main.getRewards().findOneAndDelete(filter);
                if (reward == null) {
                    Message.REWARD_NONEXISTENT.get().send(sender);
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

                long entries = main.getRewards().countDocuments();
                if (entries == 0) {
                    // TODO msg
                    break;
                }

                int pages = (int) (entries / 10) + 1;
                page = Math.min(page, pages);
                int skip = (page - 1) * 10;

                List<Reward> rewards = main.getRewards().find()
                        .sort(Sorts.ascending("serial_id"))
                        .skip(skip)
                        .limit(10)
                        .into(new ArrayList<>());

                // TODO header
                for (Reward reward : rewards) {
                    // TODO msg
                }
                // TODO footer
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
                Reward reward = main.getRewards().find(filter).first();
                if (reward == null) {
                    Message.REWARD_NONEXISTENT.get().send(sender);
                    break;
                }
                // TODO msg
            }
            case "cancel" -> {
                if (!(sender instanceof Player player)) {
                    Message.PLAYER_ONLY.get().send(sender);
                    return true;
                }
                if (main.getPlayerModes().containsKey(player.getUniqueId())) {
                    main.getPlayerModes().remove(player.getUniqueId());
                    Message.CANCEL.get().send(player);
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
