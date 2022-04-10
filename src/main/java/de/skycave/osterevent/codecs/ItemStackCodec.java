package de.skycave.osterevent.codecs;

import org.bson.BsonBinary;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ItemStackCodec implements Codec<ItemStack> {
    @Override
    public ItemStack decode(@NotNull BsonReader reader, DecoderContext decoderContext) {
        return ItemStack.deserializeBytes(reader.readBinaryData().getData());
    }

    @Override
    public void encode(BsonWriter writer, ItemStack value, EncoderContext encoderContext) {
        if (value == null) {
            writer.writeNull();
            return;
        }
        writer.writeBinaryData(new BsonBinary(value.serializeAsBytes()));
    }

    @Override
    public Class<ItemStack> getEncoderClass() {
        return ItemStack.class;
    }
}
