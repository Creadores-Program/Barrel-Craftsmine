/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.CreadoresProgram.CraftsMine.network.protocol;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.CreadoresProgram.CraftsMine.network.protocol.base.MCDPacket;
import org.CreadoresProgram.CraftsMine.network.protocol.Interface.ProtocolInfo;

public class WindowDataPacket extends MCDPacket {

    public byte windowid;
    public int property;
    public int value;

    @Override
    public byte pid() {
        return ProtocolInfo.CONTAINER_SET_DATA_PACKET;
    }

    @Override
    public void encode() {
        setShouldSendImmediate(true);
        this.reset();
        this.putByte(this.windowid);
        this.putShort(this.property);
        this.putShort(this.value);
    }

    @Override
    public void decode() {
    }

}
