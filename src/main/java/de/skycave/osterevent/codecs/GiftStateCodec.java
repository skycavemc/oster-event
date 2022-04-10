package de.skycave.osterevent.codecs;

import de.skycave.osterevent.enums.GiftState;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

public class GiftStateCodec implements Codec<GiftState> {

    @Override
    public GiftState decode(BsonReader reader, DecoderContext decoderContext) {
        return GiftState.valueOf(reader.readString());
    }

    @Override
    public void encode(BsonWriter writer, GiftState value, EncoderContext encoderContext) {
        if (value != null) {
            writer.writeName(value.toString());
        }
    }

    @Override
    public Class<GiftState> getEncoderClass() {
        return GiftState.class;
    }
}
