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

    public Reward() {}

    public Reward(int serialId, Location location, List<ItemStack> rewards) {
        this.serialId = serialId;
        this.location = location;
        this.rewards = rewards;
    }

    public ObjectId getObjectId() {
        return objectId;
    }

    public int getSerialId() {
        return serialId;
    }

    public Location getLocation() {
        return location;
    }

    public List<ItemStack> getRewards() {
        return rewards;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public void setRewards(List<ItemStack> rewards) {
        this.rewards = rewards;
    }
}
