package de.skycave.osterevent;

import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import de.skycave.osterevent.annotations.CreateDataFolder;
import de.skycave.osterevent.annotations.Prefix;
import de.skycave.osterevent.codecs.*;
import de.skycave.osterevent.commands.OsternCommand;
import de.skycave.osterevent.enums.PlayerMode;
import de.skycave.osterevent.listeners.BlockBreakListener;
import de.skycave.osterevent.listeners.InventoryCloseListener;
import de.skycave.osterevent.listeners.PlayerInteractListener;
import de.skycave.osterevent.models.Gift;
import de.skycave.osterevent.models.SkyCavePlugin;
import de.skycave.osterevent.models.User;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@CreateDataFolder
@Prefix("&2&lOstern &8» ")
public final class OsterEvent extends SkyCavePlugin {

    private MongoClient client;
    private MongoCollection<Gift> gifts;
    private MongoCollection<User> users;
    private final Map<UUID, PlayerMode> playerModes = new HashMap<>();
    private final Map<UUID, Gift> giftCache = new HashMap<>();

    @Override
    public void onEnable() {
        super.onEnable();
        // database
        CodecRegistry registry = CodecRegistries.fromRegistries(
                CodecRegistries.fromCodecs(new ItemStackCodec(), new LocationCodec(), new UUIDCodec()),
                CodecRegistries.fromProviders(new GiftCodecProvider(), new UserCodecProvider()),
                MongoClientSettings.getDefaultCodecRegistry()
        );
        MongoClientSettings settings = MongoClientSettings.builder().codecRegistry(registry).build();
        client = MongoClients.create(settings);
        MongoDatabase db = client.getDatabase("oster_event");
        gifts = db.getCollection("gifts", Gift.class);
        users = db.getCollection("users", User.class);

        // resources
        if (copyResource("config.yml")) {
            reloadConfig();
        }

        // commands
        registerCommand("ostern", new OsternCommand(this));

        // listeners
        registerEvents(
                new InventoryCloseListener(this),
                new PlayerInteractListener(this),
                new BlockBreakListener(this)
        );
    }

    @Override
    public void onDisable() {
        client.close();
        saveConfig();
    }

    public MongoCollection<Gift> getGifts() {
        return gifts;
    }

    public MongoCollection<User> getUsers() {
        return users;
    }

    public Map<UUID, PlayerMode> getPlayerModes() {
        return playerModes;
    }

    public Map<UUID, Gift> getGiftCache() {
        return giftCache;
    }
}
