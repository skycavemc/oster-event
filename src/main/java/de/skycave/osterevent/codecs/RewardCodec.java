package de.skycave.osterevent.codecs;

import de.skycave.osterevent.models.Reward;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

public class RewardCodec implements Codec<Reward> {

    private Codec<Location> locationCodec;
    private Codec<ItemStack> itemStackCodec;

    @Override
    public Reward decode(BsonReader reader, DecoderContext decoderContext) {
        return null;
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
