/*
 * GNU LESSER GENERAL PUBLIC LICENSE
 *                       Version 3, 29 June 2007
 *
 * Copyright (C) 2007 Free Software Foundation, Inc. <http://fsf.org/>
 * Everyone is permitted to copy and distribute verbatim copies
 * of this license document, but changing it is not allowed.
 *
 * You can view LICENCE file for details. 
 *
 * @author The Dragonet Team
 */
package org.CreadoresProgram.CraftsMine.network.protocol;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.CreadoresProgram.CraftsMine.network.protocol.base.MCDPacket;
import org.CreadoresProgram.CraftsMine.network.protocol.Interface.ProtocolInfo;

public class SetTimePacket extends MCDPacket {

    public int time;
    public boolean started = true;

    @Override
    public byte pid() {
        return ProtocolInfo.SET_TIME_PACKET;
    }

    @Override
    public void encode() {
        this.reset();
        this.putInt(this.time);
        this.putByte((byte) (this.started ? 1 : 0));
    }

    @Override
    public void decode() {
    }

}
