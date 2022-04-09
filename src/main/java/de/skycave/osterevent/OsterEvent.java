package de.skycave.osterevent;

import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import de.skycave.osterevent.codecs.*;
import de.skycave.osterevent.enums.PlayerMode;
import de.skycave.osterevent.interfaces.PrefixHolder;
import de.skycave.osterevent.models.AutoSaveConfig;
import de.skycave.osterevent.models.Reward;
import de.skycave.osterevent.models.User;
import de.skycave.osterevent.utils.FileUtils;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class OsterEvent extends JavaPlugin implements PrefixHolder {

    private MongoClient client;
    private MongoCollection<Reward> rewards;
    private MongoCollection<User> users;
    private final Map<UUID, PlayerMode> playerModes = new HashMap<>();
    private final Map<UUID, Reward> rewardCache = new HashMap<>();
    private AutoSaveConfig configuration;

    @Override
    public void onEnable() {
        // database
        CodecRegistry registry = CodecRegistries.fromRegistries(
                CodecRegistries.fromCodecs(new ItemStackCodec(), new LocationCodec(), new UUIDCodec()),
                CodecRegistries.fromProviders(new RewardCodecProvider(), new UserCodecProvider()),
                MongoClientSettings.getDefaultCodecRegistry()
        );
        MongoClientSettings settings = MongoClientSettings.builder().codecRegistry(registry).build();
        client = MongoClients.create(settings);
        MongoDatabase db = client.getDatabase("oster_event");
        rewards = db.getCollection("rewards", Reward.class);
        users = db.getCollection("users", User.class);

        if (FileUtils.copyResource(this, "config.yml")) {
            configuration = new AutoSaveConfig(new File(getDataFolder(), "config.yml"));
        }
    }

    @Override
    public void onDisable() {
        client.close();
    }

    public MongoCollection<Reward> getRewards() {
        return rewards;
    }

    public MongoCollection<User> getUsers() {
        return users;
    }

    public Map<UUID, PlayerMode> getPlayerModes() {
        return playerModes;
    }

    public Map<UUID, Reward> getRewardCache() {
        return rewardCache;
    }

    public AutoSaveConfig getConfiguration() {
        return configuration;
    }

    @Override
    public String getPrefix() {
        return "&a&lOstern &8»";
    }
}
