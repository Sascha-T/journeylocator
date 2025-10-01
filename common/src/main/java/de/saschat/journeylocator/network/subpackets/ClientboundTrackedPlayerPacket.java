package de.saschat.journeylocator.network.subpackets;

import de.saschat.journeylocator.TrackedPlayerContainer;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;
import java.util.UUID;

public record ClientboundTrackedPlayerPacket(boolean remove, UUID id,
                                             Optional<TrackedPlayerContainer.Position> pos) implements XaeroSubpacket {
    public static ClientboundTrackedPlayerPacket read(FriendlyByteBuf buf) {
        CompoundTag tag = (CompoundTag) buf.readNbt(NbtAccounter.unlimitedHeap());
        boolean remove = tag.getBoolean("r");
        UUID id = tag.getUUID("i");
        Optional<TrackedPlayerContainer.Position> pos = Optional.empty();
        if (!remove) {
            pos = Optional.of(new TrackedPlayerContainer.Position(
                    id,
                    new Vec3(
                            tag.getDouble("x"),
                            tag.getDouble("y"),
                            tag.getDouble("z")
                    ),
                    ResourceKey.create(
                            Registries.DIMENSION, ResourceLocation.parse(tag.getString("d"))
                    )
            ));
        }
        return new ClientboundTrackedPlayerPacket(remove, id, pos);
    }

    public void write(FriendlyByteBuf buf) {
        throw new AssertionError("Not required");
    }

    @Override
    public String toString() {
        return "ClientboundTrackedPlayerPacket{" +
                "remove=" + remove +
                ", id=" + id +
                ", pos=" + pos +
                '}';
    }
}
