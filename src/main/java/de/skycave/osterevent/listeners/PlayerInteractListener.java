package de.skycave.osterevent.listeners;

import com.mongodb.client.model.Filters;
import de.skycave.osterevent.OsterEvent;
import de.skycave.osterevent.enums.GiftState;
import de.skycave.osterevent.enums.Message;
import de.skycave.osterevent.enums.PlayerMode;
import de.skycave.osterevent.models.Gift;
import de.skycave.osterevent.models.User;
import de.skycave.osterevent.utils.PlayerUtils;
import org.bson.conversions.Bson;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.StringJoiner;
import java.util.UUID;

public class PlayerInteractListener implements Listener {

    private final OsterEvent main;

    public PlayerInteractListener(OsterEvent main) {
        this.main = main;
    }

    @EventHandler
    public void on(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        PlayerMode mode = main.getPlayerModes().get(uuid);
        Block block = event.getClickedBlock();
        if (block == null) return;
        if (event.getAction() != Action.LEFT_CLICK_BLOCK && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        if (mode != null) {
            if (!player.hasPermission("skycave.ostern")) {
                main.getPlayerModes().remove(uuid);
                return;
            }
            Gift reward = main.getRewardCache().get(uuid);
            if (reward == null) return;

            switch (mode) {
                case MOVE -> {
                    reward.setLocation(event.getClickedBlock().getLocation());
                    Message.MOVE_SUCCESS.get().replace("%id", "" + reward.getSerialId()).send(player);
                    main.getPlayerModes().remove(uuid);
                    main.getRewardCache().remove(uuid);
                    return;
                }
                case CREATE -> {
                    reward.setLocation(event.getClickedBlock().getLocation());
                    Message.MOVE_SUCCESS.get().replace("%id", "" + reward.getSerialId()).send(player);
                    PlayerUtils.startEdit(player, reward, main);
                    return;
                }
            }
        }

        Bson rewardFilter = Filters.eq("location", block.getLocation());
        Gift reward = main.getRewards().find(rewardFilter).first();
        if (reward == null) return;
        Bson filter = Filters.eq("uuid", uuid);
        User user = main.getUsers().find(filter).first();
        if (user == null) {
            user = new User();
            user.setUuid(uuid);
            user.setClaimedRewards(new ArrayList<>());
        }

        switch (reward.getGiftState()) {
            case CLAIMED -> {
                if (user.getClaimedRewards().contains(reward.getSerialId())) {
                    Message.CLAIM_ALREADY.get().send(player);
                    break;
                }
                Message.CLAIM_ALREADY_ONCE.get().send(player);
            }
            case CLAIMABLE -> {
                if (user.getClaimedRewards().contains(reward.getSerialId())) {
                    Message.CLAIM_ALREADY.get().send(player);
                    break;
                }
                // TODO check if player has space
                claimRewards(player, reward, filter, user);
            }
            case CLAIMABLE_ONCE -> {
                // TODO check if player has space
                reward.setGiftState(GiftState.CLAIMED);
                main.getRewards().replaceOne(rewardFilter, reward);
                claimRewards(player, reward, filter, user);
                Message.CLAIM_ONCE.get().send(player);
            }
        }
    }

    private void claimRewards(Player player, @NotNull Gift reward, Bson filter, @NotNull User user) {
        user.getClaimedRewards().add(reward.getSerialId());
        main.getUsers().replaceOne(filter, user);
        StringJoiner sj = new StringJoiner("&7, &e");
        for (ItemStack item : reward.getRewards()) {
            player.getInventory().addItem(item);
            //noinspection deprecation
            sj.add(item.getAmount() + "x " + item.getItemMeta().getDisplayName());
        }
        Message.CLAIM.get().replace("%rewards", sj.toString()).send(player);
    }

}
