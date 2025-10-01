package de.saschat.journeylocator.neoforge;

import de.saschat.journeylocator.JourneyLocator;
import de.saschat.journeylocator.network.SourceDistinction;
import de.saschat.journeylocator.network.XaeroCodec;
import de.saschat.journeylocator.network.packets.XaeroMinimapPacket;
import de.saschat.journeylocator.network.packets.XaeroWorldmapPacket;
import de.saschat.journeylocator.network.subpackets.XaeroSubpacket;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.EntityEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;

@Mod(JourneyLocator.MOD_ID)
public final class JourneylocatorNeoForge {
    public JourneylocatorNeoForge(IEventBus bus) {
        // Run our common setup.
        JourneyLocator.init();

        bus.addListener(this::network);
        NeoForge.EVENT_BUS.addListener(this::join);
        NeoForge.EVENT_BUS.addListener(this::level);
        NeoForge.EVENT_BUS.addListener(this::entity);

        JourneyLocator.SUBPACKET_CONSUMER = this::sendPacket;
    }

    private void sendPacket(SourceDistinction sourceDistinction, XaeroSubpacket xaeroSubpacket) {
        PacketDistributor.sendToServer(switch (sourceDistinction) {
            case WORLDMAP -> new XaeroWorldmapPacket(xaeroSubpacket);
            case MINIMAP -> new XaeroMinimapPacket(xaeroSubpacket);
        });
    }

    private void entity(EntityJoinLevelEvent level) {
        if(level.getEntity() instanceof Player p) {
            JourneyLocator.reportPlayer(p.getUUID(), p.getId());
        }
    }
    private void level(PlayerEvent.PlayerChangedDimensionEvent event) {
        JourneyLocator.reset();
    }
    private void join(ClientPlayerNetworkEvent.LoggingOut event) {
        JourneyLocator.setState(true);
    }
    private void network(RegisterPayloadHandlersEvent event) {

        event.registrar("1.0").playBidirectional(XaeroWorldmapPacket.TYPE, XaeroCodec.codec(XaeroWorldmapPacket::new, XaeroWorldmapPacket::getSubpacket), this::handlerWm);
        event.registrar("1.0").playBidirectional(XaeroMinimapPacket.TYPE, XaeroCodec.codec(XaeroMinimapPacket::new, XaeroMinimapPacket::getSubpacket), this::handlerMm);
    }

    private void handlerWm(XaeroWorldmapPacket xaerosPacket, IPayloadContext iPayloadContext) {
        JourneyLocator.packet(SourceDistinction.WORLDMAP, xaerosPacket.packet);
    }
    private void handlerMm(XaeroMinimapPacket xaerosPacket, IPayloadContext iPayloadContext) {
        JourneyLocator.packet(SourceDistinction.MINIMAP, xaerosPacket.packet);
    }
}
