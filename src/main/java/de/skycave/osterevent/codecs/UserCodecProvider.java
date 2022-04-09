package de.skycave.osterevent.codecs;

import de.skycave.osterevent.models.User;
import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;

public class UserCodecProvider implements CodecProvider {

    @SuppressWarnings("unchecked")
    @Override
    public <T> Codec<T> get(Class<T> clazz, CodecRegistry registry) {
        if (clazz == User.class) {
            return (Codec<T>) new UserCodec(registry);
        }
        return null;
    }

}
