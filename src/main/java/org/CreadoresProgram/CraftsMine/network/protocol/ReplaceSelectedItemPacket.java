package org.CreadoresProgram.CraftsMine.network.protocol;

import org.CreadoresProgram.CraftsMine.network.protocol.base.MCDPacket;
import org.CreadoresProgram.CraftsMine.network.protocol.Interface.ProtocolInfo;
/**
 * Created by Pub4Game on 29.04.2016.
 */
public class ReplaceSelectedItemPacket extends MCDPacket {

    public static final byte NETWORK_ID = ProtocolInfo.REPLACE_SELECTED_ITEM_PACKET;

    @Override
    public void decode() {

    }

    @Override
    public void encode() {

    }

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

}
