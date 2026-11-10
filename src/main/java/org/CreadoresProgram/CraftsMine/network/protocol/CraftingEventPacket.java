package org.CreadoresProgram.CraftsMine.network.protocol;


import org.CreadoresProgram.CraftsMine.inventory.Item;

import java.util.UUID;
import org.CreadoresProgram.CraftsMine.network.protocol.base.MCDPacket;
import org.CreadoresProgram.CraftsMine.network.protocol.Interface.ProtocolInfo;

/**
 * @author Nukkit Project Team
 */
public class CraftingEventPacket extends MCDPacket {

    public static final byte NETWORK_ID = ProtocolInfo.CRAFTING_EVENT_PACKET;

    public int windowId;
    public int type;
    public UUID id;

    public Item[] input;
    public Item[] output;

    @Override
    public void decode() {
        windowId = getByte();
        type = getInt();
        id = getUUID();

        int inputSize = getInt();
        input = new Item[inputSize];
        for (int i = 0; i < inputSize && i < 128; ++i) {
            input[i] = getSlot();
        }

        int outputSize = getInt();
        output = new Item[outputSize];
        for (int i = 0; i < outputSize && i < 128; ++i) {
            output[i] = getSlot();
        }
    }

    @Override
    public void encode() {

    }

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

}
