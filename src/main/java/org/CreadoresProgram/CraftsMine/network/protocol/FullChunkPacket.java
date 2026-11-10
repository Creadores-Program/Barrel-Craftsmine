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

public class FullChunkPacket extends MCDPacket {

    public static final byte ORDER_COLUMNS = 0;
    public static final byte ORDER_LAYERED = 1;
    public int chunkX;
    public int chunkZ;
    public byte order = ORDER_COLUMNS;
    public byte[] cdata;

    @Override
    public byte pid() {
        return ProtocolInfo.FULL_CHUNK_DATA_PACKET;
    }

    @Override
    public void encode() {
            setShouldSendImmediate(false);
            this.reset();
            this.putInt(this.chunkX);
            this.putInt(this.chunkZ);
            this.putByte(this.order);
            this.putInt(this.cdata.length);
            this.put(this.cdata);
    }

    @Override
    public void decode() {
    }

}
