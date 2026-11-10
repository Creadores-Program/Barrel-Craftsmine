/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.CreadoresProgram.CraftsMine.network.protocol;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.CreadoresProgram.CraftsMine.network.protocol.base.MCDPacket;
import org.CreadoresProgram.CraftsMine.network.protocol.Interface.ProtocolInfo;

public class InteractPacket extends MCDPacket {

    public static final byte ACTION_RIGHT_CLICK = 1;
    public static final byte ACTION_LEFT_CLICK = 2;
    public static final byte ACTION_VEHICLE_EXIT = 3;
    public byte action;
    public long target;
    public long eid;

    @Override
    public byte pid() {
        return ProtocolInfo.INTERACT_PACKET;
    }

    @Override
    public void encode() {
            reset();
            putByte(action);
            putLong(target);
    }

    @Override
    public void decode() {
            this.action = (byte) getByte();
            this.target = getLong();
    }

}
