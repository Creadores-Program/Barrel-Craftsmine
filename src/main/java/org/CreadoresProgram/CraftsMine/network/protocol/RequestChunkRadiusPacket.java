package org.CreadoresProgram.CraftsMine.network.protocol;
import org.CreadoresProgram.CraftsMine.network.protocol.base.MCDPacket;
import org.CreadoresProgram.CraftsMine.network.protocol.Interface.ProtocolInfo;
/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class RequestChunkRadiusPacket extends MCDPacket {

    public static final byte NETWORK_ID = ProtocolInfo.REQUEST_CHUNK_RADIUS_PACKET;

    public int radius;

    @Override
    public void decode() {
        this.radius = this.getInt();
    }

    @Override
    public void encode() {

    }

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

}
