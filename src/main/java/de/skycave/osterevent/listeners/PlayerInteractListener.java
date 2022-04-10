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
    public void on(@NotNull PlayerInteractEvent event) {
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
            Gift gift = main.getGiftCache().get(uuid);
            if (gift == null) return;

            switch (mode) {
                case MOVE -> {
                    gift.setLocation(event.getClickedBlock().getLocation());
                    main.getGifts().replaceOne(Filters.eq("_id", gift.getObjectId()), gift);
                    Message.MOVE_SUCCESS.get().replace("%id", "" + gift.getSerialId()).send(player);
                    main.getPlayerModes().remove(uuid);
                    main.getGiftCache().remove(uuid);
                    return;
                }
                case CREATE -> {
                    gift.setLocation(event.getClickedBlock().getLocation());
                    main.getGifts().insertOne(gift);
                    main.getConfiguration().set("current_id", gift.getSerialId());
                    Message.MOVE_SUCCESS.get().replace("%id", "" + gift.getSerialId()).send(player);
                    PlayerUtils.startEdit(player, gift, main);
                    return;
                }
            }
        }

        Bson giftFilter = Filters.eq("location", block.getLocation());
        Gift gift = main.getGifts().find(giftFilter).first();
        if (gift == null) return;
        Bson filter = Filters.eq("uuid", uuid);
        User user = main.getUsers().find(filter).first();
        if (user == null) {
            user = new User();
            user.setUuid(uuid);
            user.setClaimedRewards(new ArrayList<>());
        }

        switch (gift.getGiftState()) {
            case CLAIMED -> {
                if (user.getClaimedRewards().contains(gift.getSerialId())) {
                    Message.CLAIM_ALREADY.get().send(player);
                    break;
                }
                Message.CLAIM_ALREADY_ONCE.get().send(player);
            }
            case CLAIMABLE -> {
                if (user.getClaimedRewards().contains(gift.getSerialId())) {
                    Message.CLAIM_ALREADY.get().send(player);
                    break;
                }
                // TODO check if player has space
                claimRewards(player, gift, filter, user);
            }
            case CLAIMABLE_ONCE -> {
                // TODO check if player has space
                gift.setGiftState(GiftState.CLAIMED);
                main.getGifts().replaceOne(giftFilter, gift);
                claimRewards(player, gift, filter, user);
                Message.CLAIM_ONCE.get().send(player);
            }
        }
    }

    private void claimRewards(Player player, @NotNull Gift gift, Bson filter, @NotNull User user) {
        user.getClaimedRewards().add(gift.getSerialId());
        main.getUsers().replaceOne(filter, user);
        StringJoiner sj = new StringJoiner("&7, &e");
        for (ItemStack item : gift.getRewards()) {
            player.getInventory().addItem(item);
            //noinspection deprecation
            sj.add(item.getAmount() + "x " + item.getItemMeta().getDisplayName());
        }
        Message.CLAIM.get().replace("%rewards", sj.toString()).send(player);
    }

}
