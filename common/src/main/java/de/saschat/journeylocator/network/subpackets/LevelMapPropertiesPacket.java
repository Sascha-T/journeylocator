package de.saschat.journeylocator.network.subpackets;

import net.minecraft.network.FriendlyByteBuf;

public record LevelMapPropertiesPacket(int id) implements XaeroSubpacket {
    public static LevelMapPropertiesPacket read(FriendlyByteBuf buf) {
        return new LevelMapPropertiesPacket(buf.readInt());
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeInt(id);
    }

    @Override
    public String toString() {
        return "LevelMapPropertiesPacket{" +
                "id=" + id +
                '}';
    }
}
