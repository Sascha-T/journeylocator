package de.saschat.journeylocator.network.subpackets;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.network.FriendlyByteBuf;

public record ClientboundRulesPacket(boolean caveMode, boolean netherCaveMode, boolean radar) implements XaeroSubpacket {
    public static ClientboundRulesPacket read(FriendlyByteBuf buf) {
        CompoundTag tag = (CompoundTag) buf.readNbt(NbtAccounter.unlimitedHeap());
        return new ClientboundRulesPacket(tag.getBoolean("cm"), tag.getBoolean("ncm"), tag.getBoolean("r"));
    }

    public void write(FriendlyByteBuf buf) {
        throw new AssertionError("Not required");
    }

    @Override
    public String toString() {
        return "ClientboundRulesPacket{" +
                "caveMode=" + caveMode +
                ", netherCaveMode=" + netherCaveMode +
                ", radar=" + radar +
                '}';
    }
}
