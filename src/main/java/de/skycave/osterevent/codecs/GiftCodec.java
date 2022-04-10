package de.skycave.osterevent.codecs;

import de.skycave.osterevent.enums.GiftState;
import de.skycave.osterevent.models.Gift;
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

public class GiftCodec implements Codec<Gift> {

    private final Codec<Location> locationCodec;
    private final Codec<ItemStack> itemStackCodec;
    private final Codec<GiftState> giftStateCodec;

    public GiftCodec(@NotNull CodecRegistry registry) {
        locationCodec = registry.get(Location.class);
        itemStackCodec = registry.get(ItemStack.class);
        giftStateCodec = registry.get(GiftState.class);
    }

    @Override
    public Gift decode(BsonReader reader, DecoderContext decoderContext) {
        Gift reward = new Gift();
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
                case "only_once" -> reward.setGiftState(giftStateCodec.decode(reader, decoderContext));
                default -> reader.skipValue();
            }
        }
        return reward;
    }

    @Override
    public void encode(BsonWriter writer, Gift value, EncoderContext encoderContext) {
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
            writer.writeName("gift_state");
            giftStateCodec.encode(writer, value.getGiftState(), encoderContext);
            writer.writeEndDocument();
        }
    }

    @Override
    public Class<Gift> getEncoderClass() {
        return Gift.class;
    }
}
