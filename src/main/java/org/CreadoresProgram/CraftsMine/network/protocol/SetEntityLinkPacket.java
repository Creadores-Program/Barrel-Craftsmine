package org.CreadoresProgram.CraftsMine.network.protocol;
import org.CreadoresProgram.CraftsMine.network.protocol.base.MCDPacket;
import org.CreadoresProgram.CraftsMine.network.protocol.Interface.ProtocolInfo;

/**
 * Created on 15-10-22.
 */
public class SetEntityLinkPacket extends MCDPacket {

    public static final byte NETWORK_ID = ProtocolInfo.SET_ENTITY_LINK_PACKET;

    public static final byte TYPE_REMOVE = 0;
    public static final byte TYPE_RIDE = 1;
    public static final byte TYPE_PASSENGER = 2;

    public long rider;
    public long riding;
    public byte type;

    @Override
    public void decode() {

    }

    @Override
    public void encode() {
        reset();
        putLong(rider);
        putLong(riding);
        putByte(type);
    }

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

}
