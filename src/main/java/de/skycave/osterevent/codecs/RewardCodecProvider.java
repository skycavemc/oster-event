package de.skycave.osterevent.codecs;

import de.skycave.osterevent.models.Reward;
import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;

public class RewardCodecProvider implements CodecProvider {

    @SuppressWarnings("unchecked")
    @Override
    public <T> Codec<T> get(Class<T> clazz, CodecRegistry registry) {
        if (clazz == Reward.class) {
            return (Codec<T>) new RewardCodec(registry);
        }
        return null;
    }

}
