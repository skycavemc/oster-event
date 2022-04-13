package de.skycave.osterevent.listeners;

import com.mongodb.client.model.Filters;
import de.skycave.osterevent.OsterEvent;
import de.skycave.osterevent.enums.GiftState;
import de.skycave.osterevent.enums.Message;
import de.skycave.osterevent.enums.PlayerMode;
import de.skycave.osterevent.models.Gift;
import de.skycave.osterevent.models.User;
import de.skycave.osterevent.utils.Utils;
import org.bson.conversions.Bson;
import org.bukkit.Color;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
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
            event.setCancelled(true);

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
                    Utils.startEdit(player, gift, main);
                    return;
                }
            }
        }

        Bson giftFilter = Filters.eq("location", block.getLocation());
        Gift gift = main.getGifts().find(giftFilter).first();
        if (gift == null) return;
        event.setCancelled(true);
        Bson filter = Filters.eq("uuid", uuid);
        User user = main.getUsers().find(filter).first();
        if (user == null) {
            user = new User();
            user.setUuid(uuid);
            user.setClaimedRewards(new ArrayList<>());
            main.getUsers().insertOne(user);
        }

        switch (gift.getGiftState()) {
            case CLAIMED -> {
                if (user.getClaimedRewards().contains(gift.getSerialId())) {
                    Message.CLAIM_ALREADY.get().send(player);
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASEDRUM, 1.0f, 1.2f);
                    break;
                }
                Message.CLAIM_ALREADY_ONCE.get().send(player);
            }
            case CLAIMABLE -> {
                if (user.getClaimedRewards().contains(gift.getSerialId())) {
                    Message.CLAIM_ALREADY.get().send(player);
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASEDRUM, 1.0f, 1.2f);
                    break;
                }
                int rewardAmount = gift.getRewards().size();
                if (!hasSpace(player.getInventory(), rewardAmount)) {
                    Message.CLAIM_NO_SPACE.get().replace("%amount", "" + rewardAmount);
                    break;
                }
                claimRewards(player, gift, filter, user);
            }
            case CLAIMABLE_ONCE -> {
                int rewardAmount = gift.getRewards().size();
                if (!hasSpace(player.getInventory(), rewardAmount)) {
                    Message.CLAIM_NO_SPACE.get().replace("%amount", "" + rewardAmount);
                    break;
                }
                gift.setGiftState(GiftState.CLAIMED);
                main.getGifts().replaceOne(giftFilter, gift);
                claimRewards(player, gift, filter, user);
                Message.CLAIM_ONCE.get().send(player);
            }
        }
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private boolean hasSpace(@NotNull Inventory inv, int required) {
        int free = 0;
        for (int i = 0; i < 36; i++) {
            if (inv.getItem(i) == null) {
                free++;
            }
        }
        return free >= required;
    }

    private void claimRewards(Player player, @NotNull Gift gift, Bson filter, @NotNull User user) {
        user.getClaimedRewards().add(gift.getSerialId());
        main.getUsers().replaceOne(filter, user);
        for (ItemStack item : gift.getRewards()) {
            player.getInventory().addItem(item);
        }
        Message.CLAIM.get().replace("%rewards",
                Utils.itemStacksAsString(gift.getRewards(), "&7, &e")
        ).send(player);
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.2f);
        Particle.DustOptions options = new Particle.DustOptions(Color.LIME, 1.2f);
        player.spawnParticle(Particle.REDSTONE, gift.getLocation().add(.5, 1, .5), 50, options);
    }

}
