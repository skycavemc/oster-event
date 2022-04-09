package de.skycave.osterevent;

import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import de.skycave.osterevent.codecs.ItemStackCodec;
import de.skycave.osterevent.codecs.LocationCodec;
import de.skycave.osterevent.codecs.RewardCodecProvider;
import de.skycave.osterevent.models.Reward;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bukkit.plugin.java.JavaPlugin;

public final class OsterEvent extends JavaPlugin {

    private MongoClient client;
    private MongoCollection<Reward> rewards;

    @Override
    public void onEnable() {
        CodecRegistry registry = CodecRegistries.fromRegistries(
                CodecRegistries.fromCodecs(new ItemStackCodec(), new LocationCodec()),
                CodecRegistries.fromProviders(new RewardCodecProvider()),
                MongoClientSettings.getDefaultCodecRegistry()
        );
        MongoClientSettings settings = MongoClientSettings.builder().codecRegistry(registry).build();
        client = MongoClients.create(settings);
        MongoDatabase db = client.getDatabase("oster_event");
        rewards = db.getCollection("rewards", Reward.class);
    }

    @Override
    public void onDisable() {
        client.close();
    }

    public MongoCollection<Reward> getRewards() {
        return rewards;
    }
}
