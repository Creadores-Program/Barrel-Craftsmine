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

public class PlayerActionPacket extends MCDPacket {

    public static final byte ACTION_START_BREAK = 0;
    public static final byte ACTION_CANCEL_BREAK = 1;
    public static final byte ACTION_FINISH_BREAK = 2;
    public static final byte ACTION_RELEASE_ITEM = 3;
    public static final byte ACTION_STOP_SLEEPING = 4;
    public static final byte ACTION_RESPAWN = 5;
    public static final byte ACTION_JUMP = 6;
    public static final byte ACTION_START_SPRINT = 7;
    public static final byte ACTION_STOP_SPRINT = 8;
    public static final byte ACTION_START_SNEAK = 9;
    public static final byte ACTION_STOP_SNEAK = 10;
    public static final byte ACTION_DIMENSION_CHANGE = 11;
    public static final byte ACTION_NETHER_UNKNOWN = 12;

    public long entityId;
    public int action;
    public int x;
    public int y;
    public int z;
    public int face;

    @Override
    public byte pid() {
        return ProtocolInfo.PLAYER_ACTION_PACKET;
    }

    @Override
    public void encode() {
        reset();
        putLong(entityId);
        putInt(action);
        putInt(x);
        putInt(y);
        putInt(z);
        putInt(face);
    }

    @Override
    public void decode() {
        entityId = getLong();
        action = getInt();
        x = getInt();
        y = getInt();
        z = getInt();
        face = getInt();
    }

}
