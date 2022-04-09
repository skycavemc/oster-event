package de.skycave.osterevent.commands;

import com.mongodb.client.model.Filters;
import de.skycave.osterevent.OsterEvent;
import de.skycave.osterevent.enums.Message;
import de.skycave.osterevent.enums.PlayerMode;
import de.skycave.osterevent.models.Reward;
import org.bson.conversions.Bson;
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
                // TODO msg
                Reward reward = new Reward();
                reward.setOnlyOnce(args.length >= 2);
                reward.setSerialId(main.getConfiguration().getInt("current_id") + 1);
                // TODO msg
                main.getRewardCache().put(player.getUniqueId(), reward);
                main.getPlayerModes().put(player.getUniqueId(), PlayerMode.CREATE);
            }
            case "edit" -> {
                if (!(sender instanceof Player player)) {
                    Message.PLAYER_ONLY.get().send(sender);
                    return true;
                }
                if (args.length < 2) {
                    // TODO syntax
                    break;
                }
                int id;
                try {
                    id = Integer.parseInt(args[1]);
                } catch (NumberFormatException e) {
                    // TODO msg
                    break;
                }
                Bson filter = Filters.eq("serial_id", id);
                Reward reward = main.getRewards().find(filter).first();
                if (reward == null) {
                    // TODO msg
                    break;
                }
                main.getRewardCache().put(player.getUniqueId(), reward);
                main.getPlayerModes().put(player.getUniqueId(), PlayerMode.EDIT);
                // TODO msg
                // TODO edit inventory
            }
            case "move" -> {
                if (!(sender instanceof Player player)) {
                    Message.PLAYER_ONLY.get().send(sender);
                    return true;
                }
                if (args.length < 2) {
                    // TODO syntax
                    break;
                }
                int id;
                try {
                    id = Integer.parseInt(args[1]);
                } catch (NumberFormatException e) {
                    // TODO msg
                    break;
                }
                Bson filter = Filters.eq("serial_id", id);
                Reward reward = main.getRewards().find(filter).first();
                if (reward == null) {
                    // TODO msg
                    break;
                }
                main.getRewardCache().put(player.getUniqueId(), reward);
                main.getPlayerModes().put(player.getUniqueId(), PlayerMode.MOVE);
                // TODO msg
            }
            case "delete" -> {
                if (args.length < 2) {
                    // TODO syntax
                    break;
                }
                int id;
                try {
                    id = Integer.parseInt(args[1]);
                } catch (NumberFormatException e) {
                    // TODO msg
                    break;
                }
                Bson filter = Filters.eq("serial_id", id);
                Reward reward = main.getRewards().findOneAndDelete(filter);
                if (reward == null) {
                    // TODO msg
                    break;
                }
                // TODO msg
            }
            case "list" -> {

            }
            case "info" -> {
                if (args.length < 2) {
                    // TODO syntax
                    break;
                }
                int id;
                try {
                    id = Integer.parseInt(args[1]);
                } catch (NumberFormatException e) {
                    // TODO msg
                    break;
                }
                Bson filter = Filters.eq("serial_id", id);
                Reward reward = main.getRewards().find(filter).first();
                if (reward == null) {
                    // TODO msg
                    break;
                }
                // TODO msg
            }
            case "help" -> sendHelp(sender);
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
        }
        return true;
    }
    private void sendHelp(CommandSender sender) {
        // TODO help
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
            arguments.add("help");
            arguments.add("cancel");
            StringUtil.copyPartialMatches(args[0], arguments, completions);
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("create")) {
                arguments.add("onetime");
            }
            StringUtil.copyPartialMatches(args[1], arguments, completions);
        }

        Collections.sort(completions);
        return completions;
    }

}
