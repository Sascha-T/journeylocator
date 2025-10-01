package de.saschat.journeylocator.network.subpackets;

import net.minecraft.network.FriendlyByteBuf;

public record ClientboundTrackerResetPacket() implements XaeroSubpacket {
    public static ClientboundTrackerResetPacket read(FriendlyByteBuf buf) {
        return new ClientboundTrackerResetPacket();
    }

    public void write(FriendlyByteBuf buf) {
    }

    @Override
    public String toString() {
        return "ClientboundTrackerResetPacket{}";
    }
}
