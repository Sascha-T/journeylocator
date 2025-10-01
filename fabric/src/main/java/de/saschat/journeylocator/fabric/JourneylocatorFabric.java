package de.saschat.journeylocator.fabric;

import de.saschat.journeylocator.JourneyLocator;
import de.saschat.journeylocator.TrackedPlayerContainer;
import de.saschat.journeylocator.network.SourceDistinction;
import de.saschat.journeylocator.network.XaeroCodec;
import de.saschat.journeylocator.network.packets.XaeroMinimapPacket;
import de.saschat.journeylocator.network.packets.XaeroWorldmapPacket;
import de.saschat.journeylocator.network.subpackets.XaeroSubpacket;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientEntityEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientWorldEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientLoginConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.EntityTrackingEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

public final class JourneylocatorFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.

        PayloadTypeRegistry.playS2C().register(XaeroWorldmapPacket.TYPE, XaeroCodec.codec(XaeroWorldmapPacket::new, XaeroWorldmapPacket::getSubpacket));
        PayloadTypeRegistry.playS2C().register(XaeroMinimapPacket.TYPE, XaeroCodec.codec(XaeroMinimapPacket::new, XaeroMinimapPacket::getSubpacket));
        ClientPlayNetworking.registerGlobalReceiver(XaeroWorldmapPacket.TYPE, this::packetWm);
        ClientPlayNetworking.registerGlobalReceiver(XaeroMinimapPacket.TYPE, this::packetMm);
        JourneyLocator.SUBPACKET_CONSUMER = this::sendPacket;
        // Run our common setup.
        JourneyLocator.init();

        ClientPlayConnectionEvents.DISCONNECT.register(this::reset);
        ClientWorldEvents.AFTER_CLIENT_WORLD_CHANGE.register(this::worldChange);
        ClientEntityEvents.ENTITY_LOAD.register(this::load);
    }

    private void load(Entity entity, ClientLevel clientLevel) {
        if(entity instanceof Player p) {
            JourneyLocator.reportPlayer(p.getUUID(), p.getId());
        }
    }

    private void worldChange(Minecraft minecraft, ClientLevel clientLevel) {
        JourneyLocator.changeLevel(clientLevel.dimension());
    }

    private void reset(ClientPacketListener clientPacketListener, Minecraft minecraft) {
        JourneyLocator.reset();
    }

    private void sendPacket(SourceDistinction sourceDistinction, XaeroSubpacket xaeroSubpacket) {
        ClientPlayNetworking.send(switch (sourceDistinction) {
            case WORLDMAP -> new XaeroWorldmapPacket(xaeroSubpacket);
            case MINIMAP -> new XaeroMinimapPacket(xaeroSubpacket);
        });
    }


    private void packetWm(XaeroWorldmapPacket xaerosPacket, ClientPlayNetworking.Context context) {
        JourneyLocator.packet(SourceDistinction.WORLDMAP, xaerosPacket.packet);
    }

    private void packetMm(XaeroMinimapPacket xaerosPacket, ClientPlayNetworking.Context context) {
        JourneyLocator.packet(SourceDistinction.MINIMAP, xaerosPacket.packet);
    }
}
