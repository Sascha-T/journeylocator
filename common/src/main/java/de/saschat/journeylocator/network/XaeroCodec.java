package de.saschat.journeylocator.network;

import de.saschat.journeylocator.network.packets.XaeroWorldmapPacket;
import de.saschat.journeylocator.network.subpackets.XaeroSubpacket;
import de.saschat.journeylocator.network.subpackets.XaeroTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.function.Function;

public class XaeroCodec {
    public static <T> StreamCodec<FriendlyByteBuf, T> codec(Function<XaeroSubpacket, T> a, Function<T, XaeroSubpacket> b) {
        return new StreamCodec<FriendlyByteBuf, T>() {
            @Override
            public T decode(FriendlyByteBuf object) {
                return a.apply(XaeroTypes.read(object));
            }

            @Override
            public void encode(FriendlyByteBuf object, T object2) {
                XaeroTypes.write(b.apply(object2), object);
            }
        };
    }
}
