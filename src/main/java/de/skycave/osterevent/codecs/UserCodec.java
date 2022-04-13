package de.skycave.osterevent.codecs;

import de.skycave.osterevent.models.User;
import org.bson.BsonReader;
import org.bson.BsonType;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.codecs.configuration.CodecRegistry;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UserCodec implements Codec<User> {

    private final Codec<UUID> uuidCodec;

    public UserCodec(@NotNull CodecRegistry registry) {
        uuidCodec = registry.get(UUID.class);
    }

    @Override
    public User decode(@NotNull BsonReader reader, DecoderContext decoderContext) {
        User user = new User();
        reader.readStartDocument();
        while (reader.readBsonType() != BsonType.END_OF_DOCUMENT) {
            switch (reader.readName()) {
                case "_id" -> user.setObjectId(reader.readObjectId());
                case "uuid" -> user.setUuid(uuidCodec.decode(reader, decoderContext));
                case "claimed_rewards" -> {
                    List<Integer> claimedRewards = new ArrayList<>();
                    reader.readStartArray();
                    while (reader.readBsonType() == BsonType.INT32) {
                        claimedRewards.add(reader.readInt32());
                    }
                    reader.readEndArray();
                    user.setClaimedRewards(claimedRewards);
                }
                default -> reader.skipValue();
            }
        }
        reader.readEndDocument();
        return user;
    }

    @Override
    public void encode(BsonWriter writer, User value, EncoderContext encoderContext) {
        if (value != null) {
            writer.writeStartDocument();
            writer.writeName("uuid");
            uuidCodec.encode(writer, value.getUuid(), encoderContext);
            writer.writeStartArray("claimed_rewards");
            for (int id : value.getClaimedRewards()) {
                writer.writeInt32(id);
            }
            writer.writeEndArray();
            writer.writeEndDocument();
        }
    }

    @Override
    public Class<User> getEncoderClass() {
        return null;
    }
}
