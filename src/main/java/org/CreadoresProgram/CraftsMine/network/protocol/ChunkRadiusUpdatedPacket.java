package org.CreadoresProgram.CraftsMine.network.protocol;

import org.CreadoresProgram.CraftsMine.network.protocol.base.MCDPacket;
import org.CreadoresProgram.CraftsMine.network.protocol.Interface.ProtocolInfo;
/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ChunkRadiusUpdatedPacket extends MCDPacket {

    public static final byte NETWORK_ID = ProtocolInfo.CHUNK_RADIUS_UPDATED_PACKET;

    public int radius;

    @Override
    public void decode() {
    }

    @Override
    public void encode() {
        super.reset();
        this.putInt(this.radius);
    }

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

}
