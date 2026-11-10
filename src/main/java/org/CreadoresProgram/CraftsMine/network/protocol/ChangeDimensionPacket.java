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

public class ChangeDimensionPacket extends MCDPacket {

    public byte dimension;
    public float x;
    public float y;
    public float z;
    
    @Override
    public byte pid() {
        return ProtocolInfo.CHANGE_DIMENSION_PACKET;
    }

    @Override
    public void encode() {
            this.reset();
            this.putByte(dimension);
            this.putFloat(x);
            this.putFloat(y);
            this.putFloat(z);
            this.putByte((byte) 0);
    }

    @Override
    public void decode() {
    }

}
