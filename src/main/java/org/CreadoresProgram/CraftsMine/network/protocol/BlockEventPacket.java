package org.CreadoresProgram.CraftsMine.network.protocol;
import org.CreadoresProgram.CraftsMine.network.protocol.base.MCDPacket;
import org.CreadoresProgram.CraftsMine.network.protocol.Interface.ProtocolInfo;
/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class BlockEventPacket extends MCDPacket {
    public static final byte NETWORK_ID = ProtocolInfo.BLOCK_EVENT_PACKET;

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    public int x;
    public int y;
    public int z;
    public int case1;
    public int case2;

    @Override
    public void decode() {

    }

    @Override
    public void encode() {
        this.reset();
        this.putInt(this.x);
        this.putInt(this.y);
        this.putInt(this.z);
        this.putInt(this.case1);
        this.putInt(this.case2);
    }
}
