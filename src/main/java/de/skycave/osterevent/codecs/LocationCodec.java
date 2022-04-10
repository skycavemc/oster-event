package de.skycave.osterevent.codecs;

import org.bson.BsonReader;
import org.bson.BsonType;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class LocationCodec implements Codec<Location> {

    @Override
    public Location decode(@NotNull BsonReader reader, DecoderContext decoderContext) {
        Location location = new Location(null, 0, 0, 0);
        reader.readStartDocument();
        while (reader.readBsonType() != BsonType.END_OF_DOCUMENT) {
            switch (reader.readName()) {
                case "x" -> location.setX(reader.readDouble());
                case "y" -> location.setY(reader.readDouble());
                case "z" -> location.setZ(reader.readDouble());
                case "yaw" -> location.setYaw((float) reader.readDouble());
                case "pitch" -> location.setPitch((float) reader.readDouble());
                case "world" -> location.setWorld(Bukkit.getWorld(UUID.fromString(reader.readString())));
                default -> reader.skipValue();
            }
        }
        reader.readEndDocument();
        return location;
    }

    @Override
    public void encode(BsonWriter writer, Location value, EncoderContext encoderContext) {
        if (value == null) {
            writer.writeNull();
            return;
        }
        writer.writeStartDocument();
        writer.writeName("x");
        writer.writeDouble(value.getX());
        writer.writeName("y");
        writer.writeDouble(value.getY());
        writer.writeName("z");
        writer.writeDouble(value.getZ());
        writer.writeName("yaw");
        writer.writeDouble(value.getYaw());
        writer.writeName("pitch");
        writer.writeDouble(value.getPitch());
        writer.writeName("world");
        writer.writeString(value.getWorld().getUID().toString());
        writer.writeEndDocument();
    }

    @Override
    public Class<Location> getEncoderClass() {
        return Location.class;
    }
}
