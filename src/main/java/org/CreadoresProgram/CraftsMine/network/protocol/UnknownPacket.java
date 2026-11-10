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
import org.CreadoresProgram.CraftsMine.network.protocol.base.MCDPacket;

public class UnknownPacket extends MCDPacket {

    private final byte packetId;

    public UnknownPacket(byte packetId, byte[] buffer) {
        this.packetId = packetId;
        this.setBuffer(buffer);
    }
    
    @Override
    public byte pid() {
        return packetId;
    }

    @Override
    public void encode() {
    }

    @Override
    public void decode() {
    }
    
}
