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

public class RespawnPacket extends MCDPacket {

    public float x;
    public float y;
    public float z;

    @Override
    public byte pid() {
        return ProtocolInfo.RESPAWN_PACKET;
    }

    @Override
    public void encode() {
        setShouldSendImmediate(true);
            this.reset();
        this.putFloat(this.x);
        this.putFloat(this.y);
        this.putFloat(this.z);
    }

    @Override
    public void decode() {
        this.x = getFloat();
        this.y = getFloat();
        this.z = getFloat();
    }

}
