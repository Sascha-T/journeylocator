package de.saschat.journeylocator;

import journeymap.common.network.model.PlayerLocation;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TrackedPlayerContainer {
    private static Map<UUID, Position> PLAYERS = new HashMap<>();
    private static Map<UUID, Integer> ID_CACHE = new HashMap<>();

    public static void receiveTracking(UUID player, Position pos) {
        PLAYERS.put(player, pos);
    }
    public static void removeTracking(UUID player) {
        PLAYERS.remove(player);
        ID_CACHE.remove(player);
    }

    public static void reset() {
        PLAYERS.clear();
        ID_CACHE.clear();
    }

    public static Collection<UUID> getKeys() {
        return PLAYERS.keySet();
    }

    public static Map<UUID, Position> getPlayers() {
        return PLAYERS;
    }

    public static boolean hasId(int id) {
        return ID_CACHE.containsKey(id);
    }

    public static void appendId(int id, UUID id2) {
        ID_CACHE.put(id2, id);
    }

    public static PlayerLocation get(UUID uuid) {
        return PLAYERS.get(uuid);
    }


    public record Position(UUID id, Vec3 pos, ResourceKey<Level> dimension) implements PlayerLocation {

        @Override
        public int getEntityId() {
            return ID_CACHE.getOrDefault(id, 0);
        }

        @Override
        public UUID getUniqueId() {
            return id;
        }

        @Override
        public double getX() {
            return pos.x;
        }

        @Override
        public double getY() {
            return pos.y;
        }

        @Override
        public double getZ() {
            return pos.z;
        }

        @Override
        public byte getYaw() {
            return 0;
        }

        @Override
        public byte getPitch() {
            return 0;
        }

        @Override
        public boolean isVisible() {
            return true;
        }
    }
}
