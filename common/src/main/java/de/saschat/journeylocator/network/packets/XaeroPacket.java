package de.saschat.journeylocator.network.packets;

import de.saschat.journeylocator.network.subpackets.XaeroSubpacket;

public interface XaeroPacket {
    XaeroSubpacket getSubpacket();
}
