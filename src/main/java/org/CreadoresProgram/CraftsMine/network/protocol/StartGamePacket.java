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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.CreadoresProgram.CraftsMine.network.protocol.base.MCDPacket;
import org.CreadoresProgram.CraftsMine.network.protocol.Interface.ProtocolInfo;

public class StartGamePacket extends MCDPacket {

    public int seed;
    public byte dimension;
    public int generator;
    public int gamemode;
    public long eid;
    public int spawnX;
    public int spawnY;
    public int spawnZ;
    public float x;
    public float y;
    public float z;
    public boolean b1;
    public boolean b2;
    public boolean b3;
    public String unknownstr;

    @Override
    public byte pid() {
        return ProtocolInfo.START_GAME_PACKET;
    }

    @Override
    public void encode() {
        this.reset();
        this.putInt(seed);
        this.putByte(dimension);
        this.putInt(generator);
        this.putInt(gamemode);
        this.putLong(eid);
        this.putInt(spawnX);
        this.putInt(spawnY);
        this.putInt(spawnZ);
        this.putFloat(x);
        this.putFloat(y);
        this.putFloat(z);
        this.putBoolean(b1);
        this.putBoolean(b2);
        this.putBoolean(b3);
        this.putString(unknownstr);
    }

    @Override
    public void decode() {
            seed = getInt();
            dimension = (byte) getByte();
            generator = getInt();
            gamemode = getInt();
            eid = getLong();
            spawnX = getInt();
            spawnY = getInt();
            spawnZ = getInt();
            x = getFloat();
            y = getFloat() - 1.62f;
            z = getFloat();
    }

}
