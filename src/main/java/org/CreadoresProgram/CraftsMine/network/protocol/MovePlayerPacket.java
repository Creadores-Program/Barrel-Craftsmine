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

public class MovePlayerPacket extends MCDPacket {

    public static final byte MODE_NORMAL = 0;
    public static final byte MODE_RESET = 1;
    public static final byte MODE_ROTATION = 2;

    public long eid;
    public float x;
    public float y;
    public float z;
    public float yaw;
    public float pitch;
    public float headYaw;
    public byte mode = MODE_NORMAL;
    public boolean onGround;

    @Override
    public byte pid() {
        return ProtocolInfo.MOVE_PLAYER_PACKET;
    }

    @Override
    public void encode() {
        setShouldSendImmediate(true);
        reset();
        putLong(eid);
        putFloat(x);
        putFloat(y);
        putFloat(z);
        putFloat(yaw);
        putFloat(headYaw);
        putFloat(pitch);
        putByte(mode);
        putByte(onGround ? (byte) 1 : 0);
    }

    @Override
    public void decode() {
        eid = getLong();
        x = getFloat();
        y = getFloat();
        z = getFloat();
        yaw = getFloat();
        headYaw = getFloat();
        pitch = getFloat();
        mode = (byte) getByte();
        onGround = getByte() > 0;
    }

}
