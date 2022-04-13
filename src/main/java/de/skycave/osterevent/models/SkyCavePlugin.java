package de.skycave.osterevent.models;

import com.google.common.io.Files;
import com.google.common.io.Resources;
import de.skycave.osterevent.annotations.CreateDataFolder;
import de.skycave.osterevent.annotations.Prefix;
import org.apache.commons.lang.Validate;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public abstract class SkyCavePlugin extends JavaPlugin {

    private String prefix = "";

    public String getPrefix() {
        return prefix;
    }

    @Override
    public void onEnable() {
        Class<? extends SkyCavePlugin> clazz = SkyCavePlugin.this.getClass();
        if (clazz.isAnnotationPresent(CreateDataFolder.class)) {
            if (!getDataFolder().isDirectory()) {
                //noinspection ResultOfMethodCallIgnored
                getDataFolder().mkdirs();
            }
        }
        if (clazz.isAnnotationPresent(Prefix.class)) {
            prefix = clazz.getAnnotation(Prefix.class).value();
        }
    }

    public void registerCommand(String command, CommandExecutor executor) {
        PluginCommand cmd = getCommand(command);
        if (cmd == null) {
            getLogger().severe("No entry for the command " + command + " found in the plugin.yml.");
            return;
        }
        cmd.setExecutor(executor);
    }

    public void registerEvents(Listener @NotNull ... events) {
        Validate.notNull(events);
        for (Listener event : events) {
            getServer().getPluginManager().registerEvents(event, this);
        }
    }

    @SuppressWarnings("UnstableApiUsage")
    public boolean copyResource(@NotNull String resourceName) {
        Validate.notNull(resourceName);
        File destination = new File(getDataFolder(), resourceName);
        URL resource = getClass().getClassLoader().getResource(resourceName);

        if (resource == null) {
            getLogger().severe("The resource " + resourceName + " does not exist.");
            return false;
        }

        if (destination.exists()) {
            getLogger().info("The file " + resourceName + " already exists.");
            return true;
        }

        try {
            //noinspection ResultOfMethodCallIgnored
            destination.createNewFile();
            Resources.asByteSource(resource).copyTo(Files.asByteSink(destination));
            getLogger().info("The file " + resourceName + " has been created.");
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

}
