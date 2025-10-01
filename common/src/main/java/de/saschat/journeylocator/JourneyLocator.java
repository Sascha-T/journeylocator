package de.saschat.journeylocator;

import de.saschat.journeylocator.network.SourceDistinction;
import de.saschat.journeylocator.network.subpackets.ClientboundTrackedPlayerPacket;
import de.saschat.journeylocator.network.subpackets.ClientboundTrackerResetPacket;
import de.saschat.journeylocator.network.subpackets.HandshakePacket;
import de.saschat.journeylocator.network.subpackets.XaeroSubpacket;
import journeymap.client.JourneymapClient;
import journeymap.client.data.DataCache;
import journeymap.common.network.model.PlayerLocation;
import journeymap.common.util.PlayerRadarManager;
import journeymap.common.waypoint.WaypointStore;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;
import java.util.UUID;
import java.util.function.BiConsumer;

public final class JourneyLocator {
    public static final String MOD_ID = "journeylocator";
    public static final ResourceLocation XAERO_PACKETS = ResourceLocation.parse("xaerominimap:main");
    public static BiConsumer<SourceDistinction, XaeroSubpacket> SUBPACKET_CONSUMER;
    private static void sendPacket(SourceDistinction to, XaeroSubpacket packet) {
        log.info("Sending {} to {}", packet.getClass().toString(), to.name());
        SUBPACKET_CONSUMER.accept(to, packet);
    }
    private static final Logger log = LogManager.getLogger();

    public static void init() {
    }

    private static void jm_remove(UUID uuid) {
        PlayerRadarManager.getInstance().remove(uuid);
        DataCache.INSTANCE.removePlayer(uuid.toString());
    }
    private static void jm_update(PlayerLocation loc) {
        JourneymapClient.getInstance().getPacketHandler().onPlayerLocationPacket(loc);
    }

    public static void packet(SourceDistinction ps, XaeroSubpacket x) {
        if(x.getClass() != ClientboundTrackedPlayerPacket.class)
            log.info("Received {} from {}", x.getClass().getSimpleName(), ps.name());
        switch (x) {
            case HandshakePacket hp -> {
                sendPacket(ps, hp);
            }
            case ClientboundTrackedPlayerPacket tp -> {
                if(!ENABLED) return;

                if(tp.remove()) {
                    jm_remove(tp.id());
                    TrackedPlayerContainer.removeTracking(tp.id());
                } else {
                    TrackedPlayerContainer.receiveTracking(tp.id(), tp.pos().get());

                    jm_update(tp.pos().get());
                }
            }
            case ClientboundTrackerResetPacket tr -> {
                if(!ENABLED) return;

                for (UUID key : TrackedPlayerContainer.getKeys()) { // ik its not sent during usage
                    jm_remove(key);
                }

                TrackedPlayerContainer.reset();
            }
            default -> {
                // unhandled
            }
        }
    }

    public static void reportPlayer(UUID uuid, int id) {
        /*log.info("Entity id {} associated with {}", id, uuid.toString());
        if(!TrackedPlayerContainer.hasId(id)) {
            TrackedPlayerContainer.appendId(id, uuid);

            jm_remove(uuid);
            PlayerLocation loc = TrackedPlayerContainer.get(uuid);
            if(loc != null)
                jm_update(loc);
        }*/
    }

    private static boolean ENABLED = true;
    private static ResourceKey<Level> level = null;

    // reset on logout
    public static void setState(boolean b) {
        log.info("Mod set to state: {}", b);
        ENABLED = b;
    }
    public static void reset() {
        log.info("Resetting mod state");
        setState(true);
        level = null;
    }

    public static void changeLevel(ResourceKey<Level> to) {
        if(!ENABLED) return;

        ResourceKey<Level> old = level;
        level = to;

        String tmp = "";
        if(old != null) {
            tmp = " from " + old.location().toString();
        }

        log.info("Changing level{} to {}", tmp, to.location().toString());

        if(old != null && !old.equals(to)) { // if not new state, and dimension changes, reprocess players
            for (Map.Entry<UUID, TrackedPlayerContainer.Position> p : TrackedPlayerContainer.getPlayers().entrySet()) {
                if(p.getValue().dimension().equals(old)) {
                    jm_remove(p.getKey());
                }
                if(p.getValue().dimension().equals(to)) {
                    jm_update(p.getValue());
                }
            }
        }
    }

    public static boolean getState() {
        return ENABLED;
    }
}
