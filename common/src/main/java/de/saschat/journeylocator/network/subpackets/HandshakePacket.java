package de.saschat.journeylocator.network.subpackets;

import net.minecraft.network.FriendlyByteBuf;

public record HandshakePacket(int id) implements XaeroSubpacket {
    public static HandshakePacket read(FriendlyByteBuf buf) {
        return new HandshakePacket(buf.readInt());
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeInt(id);
    }

    @Override
    public String toString() {
        return "HandshakePacket{" +
                "id=" + id +
                '}';
    }
}
