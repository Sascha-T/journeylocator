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
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.system.macosx.EnumerationMutationHandlerI;

import java.util.Map;
import java.util.UUID;
import java.util.function.BiConsumer;

public final class JourneyLocator {
    public static final String MOD_ID = "journeylocator";
    public static final ResourceLocation XAERO_PACKETS = ResourceLocation.parse("xaerominimap:main");
    public static BiConsumer<SourceDistinction, XaeroSubpacket> SUBPACKET_CONSUMER;
    private static final Logger log = LogManager.getLogger();

    public static void init() {
    }
    private static SourceDistinction preferredSource = null;


    private static void jm_remove(UUID uuid) {
        PlayerRadarManager.getInstance().remove(uuid);
        DataCache.INSTANCE.removePlayer(uuid.toString());
    }
    private static void jm_update(PlayerLocation loc) {
        JourneymapClient.getInstance().getPacketHandler().onPlayerLocationPacket(loc);
    }

    public static void packet(SourceDistinction ps, XaeroSubpacket x) {
        switch (x) {
            case HandshakePacket hp -> {
                SUBPACKET_CONSUMER.accept(ps, hp);
            }
            case ClientboundTrackedPlayerPacket tp -> {
                if(!ENABLED) return;

                if(preferredSource == null) {
                    preferredSource = ps;
                }
                if(ps != preferredSource) return;

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

                if(preferredSource == null) {
                    preferredSource = ps;
                }
                if(ps != preferredSource) return;

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
        if(!TrackedPlayerContainer.hasId(id)) {
            TrackedPlayerContainer.appendId(id, uuid);
            jm_remove(uuid);
            PlayerLocation loc = TrackedPlayerContainer.get(uuid);
            if(loc != null)
                jm_update(loc);
        }
    }

    private static boolean ENABLED = true;
    private static ResourceKey<Level> level = null;

    // reset on logout
    public static void setState(boolean b) {
        ENABLED = b;
    }
    public static void reset() {
        setState(true);
        level = null;
    }

    public static void changeLevel(ResourceKey<Level> to) {
        if(!ENABLED) return;

        ResourceKey<Level> old = level;
        level = to;

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
}
