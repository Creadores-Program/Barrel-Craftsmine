package org.CreadoresProgram.CraftsMine.network.protocol;

import org.CreadoresProgram.CraftsMine.network.protocol.base.MCDPacket;
import org.CreadoresProgram.CraftsMine.network.protocol.Interface.ProtocolInfo;
import org.CreadoresProgram.CraftsMine.dragonetLib.proxy.utilities.Binary;
import java.util.Map;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class SetEntityDataPacket extends MCDPacket {
    public static final byte NETWORK_ID = ProtocolInfo.SET_ENTITY_DATA_PACKET;

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    public long eid;
    public Map<Integer, Object[]> metadata;

    @Override
    public void decode() {

    }

    @Override
    public void encode() {
        this.reset();
        this.putLong(this.eid);
        this.put(Binary.writeMetadata(this.metadata));
    }
}
