package org.CreadoresProgram.CraftsMine.network.protocol;

import org.CreadoresProgram.CraftsMine.inventory.Item;
import org.CreadoresProgram.CraftsMine.network.protocol.base.MCDPacket;
import org.CreadoresProgram.CraftsMine.network.protocol.Interface.ProtocolInfo;

/**
 * @author Nukkit Project Team
 */
public class DropItemPacket extends MCDPacket {

    public static final byte NETWORK_ID = ProtocolInfo.DROP_ITEM_PACKET;

    public int type;
    public Item item;

    @Override
    public void decode() {
        type = getByte();
        item = getSlot();
    }

    @Override
    public void encode() {

    }

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

}
