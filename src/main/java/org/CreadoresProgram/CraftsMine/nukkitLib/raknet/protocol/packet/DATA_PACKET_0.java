package org.CreadoresProgram.CraftsMine.nukkitLib.raknet.protocol.packet;

import org.CreadoresProgram.CraftsMine.nukkitLib.raknet.protocol.DataPacket;
import org.CreadoresProgram.CraftsMine.nukkitLib.raknet.protocol.Packet;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class DATA_PACKET_0 extends DataPacket {
    public static final byte ID = (byte) 0x80;

    @Override
    public byte getID() {
        return ID;
    }

    public static final class Factory implements Packet.PacketFactory {

        @Override
        public Packet create() {
            return new DATA_PACKET_0();
        }

    }

}
