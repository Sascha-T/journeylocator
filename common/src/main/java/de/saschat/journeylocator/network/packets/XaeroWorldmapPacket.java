package de.saschat.journeylocator.network.packets;

import de.saschat.journeylocator.network.subpackets.XaeroSubpacket;
import de.saschat.journeylocator.network.subpackets.XaeroTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public class XaeroWorldmapPacket implements CustomPacketPayload, XaeroPacket {
    public static final CustomPacketPayload.Type<XaeroWorldmapPacket> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("xaeroworldmap", "main"));

    public XaeroSubpacket packet;

    public XaeroWorldmapPacket(XaeroSubpacket packet) {
        this.packet = packet;
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    @Override
    public XaeroSubpacket getSubpacket() {
        return packet;
    }
}
