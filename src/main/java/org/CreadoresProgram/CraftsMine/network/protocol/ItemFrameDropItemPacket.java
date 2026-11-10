package org.CreadoresProgram.CraftsMine.network.protocol;

import org.CreadoresProgram.CraftsMine.inventory.Item;
import org.CreadoresProgram.CraftsMine.network.protocol.base.MCDPacket;
import org.CreadoresProgram.CraftsMine.network.protocol.Interface.ProtocolInfo;

/**
 * Created by Pub4Game on 03.07.2016.
 */
public class ItemFrameDropItemPacket extends MCDPacket {

    public static final byte NETWORK_ID = ProtocolInfo.ITEM_FRAME_DROP_ITEM_PACKET;

    public int x;
    public int y;
    public int z;
    public Item dropItem;

    @Override
    public void decode() {
        this.z = getInt();
        this.y = getInt();
        this.x = getInt();
        this.dropItem = getSlot();
    }

    @Override
    public void encode() {

    }

    @Override
    public byte pid() {
        return NETWORK_ID;
    }
}
