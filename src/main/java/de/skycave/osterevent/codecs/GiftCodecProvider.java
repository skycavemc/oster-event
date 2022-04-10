package de.skycave.osterevent.codecs;

import de.skycave.osterevent.models.Gift;
import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;

public class GiftCodecProvider implements CodecProvider {

    @SuppressWarnings("unchecked")
    @Override
    public <T> Codec<T> get(Class<T> clazz, CodecRegistry registry) {
        if (clazz == Gift.class) {
            return (Codec<T>) new GiftCodec(registry);
        }
        return null;
    }

}
