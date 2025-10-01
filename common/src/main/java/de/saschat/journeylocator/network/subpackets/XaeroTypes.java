package de.saschat.journeylocator.network.subpackets;

import net.minecraft.network.FriendlyByteBuf;

import java.util.function.BiConsumer;
import java.util.function.Function;

public enum XaeroTypes {
    LEVEL_MAP_PROPERTIES(0, LevelMapPropertiesPacket::read, LevelMapPropertiesPacket::write, LevelMapPropertiesPacket.class),
    HANDSHAKE(1, HandshakePacket::read, HandshakePacket::write, HandshakePacket.class),
    TRACKED_PLAYER(2, ClientboundTrackedPlayerPacket::read, ClientboundTrackedPlayerPacket::write, ClientboundTrackedPlayerPacket.class),
    TRACKER_RESET(3, ClientboundTrackerResetPacket::read, ClientboundTrackerResetPacket::write, ClientboundTrackerResetPacket.class),
    RULES(4, ClientboundRulesPacket::read, ClientboundRulesPacket::write, ClientboundRulesPacket.class);

    int id; Function<FriendlyByteBuf, XaeroSubpacket> read; BiConsumer<XaeroSubpacket, FriendlyByteBuf> write; Class clz;

    <T extends XaeroSubpacket> XaeroTypes(int id, Function<FriendlyByteBuf, T> read, BiConsumer<T, FriendlyByteBuf> write, Class<T> clz) {
        this.id = id;
        this.write = (BiConsumer<XaeroSubpacket, FriendlyByteBuf>) write;
        this.read = (Function<FriendlyByteBuf, XaeroSubpacket>) read;
        this.clz = clz;
    }

    public static void write(XaeroSubpacket packet, FriendlyByteBuf buf) {
        for (XaeroTypes value : values()) {
            if(value.clz == packet.getClass()) {
                buf.writeInt(value.id);
                value.write.accept(packet, buf);
            }
        }
    }
    public static XaeroSubpacket read(FriendlyByteBuf buf) {
        byte id = buf.readByte();
        System.out.println("NOW READING XAERO PACKET: " + id);
        for (XaeroTypes value : values()) {
            if(value.id == id) {
                var x = value.read.apply(buf);
                System.out.println("RECEIVED: " + x.toString());
                return x;
            }
        }
        return null;
    }
}
