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

public class WindowClosePacket extends MCDPacket {

    public int windowid;

    @Override
    public byte pid() {
        return ProtocolInfo.CONTAINER_CLOSE_PACKET;
    }

    @Override
    public void encode() {
        setShouldSendImmediate(true);
        this.reset();
        this.putByte((byte) this.windowid);
    }

    @Override
    public void decode() {
        this.windowid = this.getByte();
    }

}
