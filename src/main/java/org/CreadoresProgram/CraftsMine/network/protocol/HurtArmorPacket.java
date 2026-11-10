package org.CreadoresProgram.CraftsMine.network.protocol;
import org.CreadoresProgram.CraftsMine.network.protocol.base.MCDPacket;
import org.CreadoresProgram.CraftsMine.network.protocol.Interface.ProtocolInfo;

/**
 * @author Nukkit Project Team
 */
public class HurtArmorPacket extends MCDPacket {

    public static final byte NETWORK_ID = ProtocolInfo.HURT_ARMOR_PACKET;

    public byte health;

    @Override
    public void decode() {

    }

    @Override
    public void encode() {
        reset();
        putByte(health);
    }

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

}
