package de.skycave.osterevent;

import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import de.skycave.osterevent.codecs.*;
import de.skycave.osterevent.commands.OsternCommand;
import de.skycave.osterevent.enums.PlayerMode;
import de.skycave.osterevent.interfaces.PrefixHolder;
import de.skycave.osterevent.listeners.InventoryCloseListener;
import de.skycave.osterevent.listeners.PlayerInteractListener;
import de.skycave.osterevent.models.AutoSaveConfig;
import de.skycave.osterevent.models.Gift;
import de.skycave.osterevent.models.User;
import de.skycave.osterevent.utils.FileUtils;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class OsterEvent extends JavaPlugin implements PrefixHolder {

    private MongoClient client;
    private MongoCollection<Gift> gifts;
    private MongoCollection<User> users;
    private final Map<UUID, PlayerMode> playerModes = new HashMap<>();
    private final Map<UUID, Gift> giftCache = new HashMap<>();
    private AutoSaveConfig configuration;

    @Override
    public void onEnable() {
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
        if (FileUtils.copyResource(this, "config.yml")) {
            configuration = new AutoSaveConfig(new File(getDataFolder(), "config.yml"));
        }

        // commands
        registerCommand("ostern", new OsternCommand(this));

        // listeners
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new InventoryCloseListener(this), this);
        pm.registerEvents(new PlayerInteractListener(this), this);
    }

    @SuppressWarnings("SameParameterValue")
    private void registerCommand(String command, CommandExecutor executor) {
        PluginCommand cmd = getCommand(command);
        if (cmd == null) {
            getLogger().severe("No entry for the command " + command + " found in the plugin.yml.");
            return;
        }
        cmd.setExecutor(executor);
    }

    @Override
    public void onDisable() {
        client.close();
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

    public AutoSaveConfig getConfiguration() {
        return configuration;
    }

    @Contract(pure = true)
    @Override
    public @NotNull String getPrefix() {
        return "&a&lOstern &8»";
    }
}
