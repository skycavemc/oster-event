package de.skycave.osterevent.models;

import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.types.ObjectId;

import java.util.List;
import java.util.UUID;

public class User {

    @BsonId
    private ObjectId objectId;
    private UUID uuid;
    @BsonProperty("claimed_rewards")
    private List<Integer> claimedRewards;

    public User() {}

    public ObjectId getObjectId() {
        return objectId;
    }

    public void setObjectId(ObjectId objectId) {
        this.objectId = objectId;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public List<Integer> getClaimedRewards() {
        return claimedRewards;
    }

    public void setClaimedRewards(List<Integer> claimedRewards) {
        this.claimedRewards = claimedRewards;
    }
}
