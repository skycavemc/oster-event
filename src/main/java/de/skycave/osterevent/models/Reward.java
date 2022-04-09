package de.skycave.osterevent.models;

import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.types.ObjectId;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class Reward {

    @BsonId
    private ObjectId objectId;
    @BsonProperty("serial_id")
    private int serialId;
    private Location location;
    private List<ItemStack> rewards;
    @BsonProperty("only_once")
    private boolean onlyOnce;

    public Reward() {}

    public ObjectId getObjectId() {
        return objectId;
    }

    public void setObjectId(ObjectId objectId) {
        this.objectId = objectId;
    }

    public int getSerialId() {
        return serialId;
    }

    public void setSerialId(int serialId) {
        this.serialId = serialId;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public List<ItemStack> getRewards() {
        return rewards;
    }

    public void setRewards(List<ItemStack> rewards) {
        this.rewards = rewards;
    }

    public boolean isOnlyOnce() {
        return onlyOnce;
    }

    public void setOnlyOnce(boolean onlyOnce) {
        this.onlyOnce = onlyOnce;
    }
}
