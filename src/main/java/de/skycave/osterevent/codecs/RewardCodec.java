package de.skycave.osterevent.codecs;

import de.skycave.osterevent.models.Reward;
import org.bson.BsonReader;
import org.bson.BsonType;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.codecs.configuration.CodecRegistry;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class RewardCodec implements Codec<Reward> {

    private final Codec<Location> locationCodec;
    private final Codec<ItemStack> itemStackCodec;

    public RewardCodec(@NotNull CodecRegistry registry) {
        locationCodec = registry.get(Location.class);
        itemStackCodec = registry.get(ItemStack.class);
    }

    @Override
    public Reward decode(BsonReader reader, DecoderContext decoderContext) {
        Reward reward = new Reward();
        reader.readStartDocument();
        while (reader.readBsonType() != BsonType.END_OF_DOCUMENT) {
            switch (reader.readName()) {
                case "_id" -> reward.setObjectId(reader.readObjectId());
                case "serial_id" -> reward.setSerialId(reader.readInt32());
                case "location" -> reward.setLocation(locationCodec.decode(reader, decoderContext));
                case "rewards" -> {
                    reader.readStartArray();
                    List<ItemStack> rewards = new ArrayList<>();
                    while (reader.readBsonType() == BsonType.BINARY) {
                        rewards.add(itemStackCodec.decode(reader, decoderContext));
                    }
                    reward.setRewards(rewards);
                    reader.readEndArray();
                }
                default -> reader.skipValue();
            }
        }
        return reward;
    }

    @Override
    public void encode(BsonWriter writer, Reward value, EncoderContext encoderContext) {
        if (value != null) {
            writer.writeStartDocument();
            writer.writeName("serial_id");
            writer.writeInt32(value.getSerialId());
            writer.writeName("location");
            locationCodec.encode(writer, value.getLocation(), encoderContext);
            writer.writeStartArray("rewards");
            for (ItemStack item : value.getRewards()) {
                itemStackCodec.encode(writer, item, encoderContext);
            }
            writer.writeEndArray();
            writer.writeEndDocument();
        }
    }

    @Override
    public Class<Reward> getEncoderClass() {
        return Reward.class;
    }
}
