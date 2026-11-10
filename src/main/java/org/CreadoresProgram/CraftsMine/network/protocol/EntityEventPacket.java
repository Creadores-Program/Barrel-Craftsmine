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

public class EntityEventPacket extends MCDPacket {

    public static final byte HURT_ANIMATION = 2;
    public static final byte DEATH_ANIMATION = 3;

    public static final byte TAME_FAIL = 6;
    public static final byte TAME_SUCCESS = 7;
    public static final byte SHAKE_WET = 8;
    public static final byte USE_ITEM = 9;
    public static final byte EAT_GRASS_ANIMATION = 10;
    public static final byte FISH_HOOK_BUBBLE = 11;
    public static final byte FISH_HOOK_POSITION = 12;
    public static final byte FISH_HOOK_HOOK = 13;
    public static final byte FISH_HOOK_TEASE = 14;
    public static final byte SQUID_INK_CLOUD = 15;
    public static final byte AMBIENT_SOUND = 16;
    public static final byte RESPAWN = 17;
    
    public long eid;
    public byte event;
    
    @Override
    public byte pid() {
        return ProtocolInfo.ENTITY_EVENT_PACKET;
    }

    @Override
    public void encode() {
        setShouldSendImmediate(true);
            this.reset();
            this.putLong(this.eid);
            this.putByte(event);
    }

    @Override
    public void decode() {
            this.eid = this.getLong();
            this.event = (byte) this.getByte();
    }

}
