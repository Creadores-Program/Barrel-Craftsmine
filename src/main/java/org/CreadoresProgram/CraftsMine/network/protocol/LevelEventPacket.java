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

public class LevelEventPacket extends MCDPacket {

    public static class Events {

        public final static int EVENT_SOUND_CLICK = 1000;
        public final static int EVENT_SOUND_CLICK_FAIL = 1001;
        public final static int EVENT_SOUND_SHOOT = 1002;
        
        public final static int EVENT_SOUND_DOOR = 1003;
        public final static int EVENT_SOUND_DOOR_OPEN = EVENT_SOUND_DOOR;
        public final static int EVENT_SOUND_DOOR_CLOSE = EVENT_SOUND_DOOR;
        
        public final static int EVENT_SOUND_FIZZ = 1004;
        public final static int EVENT_SOUND_TNT = 1005;

        public final static int EVENT_SOUND_GHAST = 1007;
        public final static int EVENT_SOUND_GHAST_SHOOT = 1008;
        public final static int EVENT_SOUND_BLAZE_SHOOT = 1009;

        public final static int EVENT_SOUND_DOOR_BUMP = 1010;
        public final static int EVENT_SOUND_POUND_WOODEN_DOOR = EVENT_SOUND_DOOR_BUMP;
        public final static int EVENT_SOUND_POUND_METAL_DOOR = EVENT_SOUND_DOOR_BUMP;
        public final static int EVENT_SOUND_BREAK_WOODEN_DOOR = 1012;

        public final static int EVENT_SOUND_BAT_FLY = 1015;
        public final static int EVENT_SOUND_ZOMBIE_INFECT = 1016;
        public final static int EVENT_SOUND_ZOMBIE_HEAL = 1017;
        public final static int EVENT_SOUND_ENDERMAN_TELEPORT = 1018;

        public final static int EVENT_SOUND_ANVIL_BREAK = 1020;
        public final static int EVENT_SOUND_ANVIL_USE = 1021;
        public final static int EVENT_SOUND_ANVIL_LAND = 1022;

        public final static int EVENT_SOUND_ITEM_DROP = 1030;
        public final static int EVENT_SOUND_ITEM_THROWN = 1031;

        public final static int EVENT_SOUND_ITEM_FRAME_ITEM_ADDED = 1040;
        public final static int EVENT_SOUND_ITEM_FRAME_PLACED = 1041;
        public final static int EVENT_SOUND_ITEM_FRAME_REMOVED = 1042;
        public final static int EVENT_SOUND_ITEM_FRAME_ITEM_REMOVED = 1043;
        public final static int EVENT_SOUND_ITEM_FRAME_ITEM_ROTATED = 1044;

        public final static int EVENT_SOUND_CAMERA_TAKE_PICTURE = 1050;
        public final static int EVENT_SOUND_EXPERIENCE_ORB = 1051;
        public final static int EVENT_SOUND_BLOCK_PLACE = 1052;

        public final static int EVENT_PARTICLE_SHOOT = 2000;
        public final static int EVENT_PARTICLE_DESTROY = 2001;
        public final static int EVENT_PARTICLE_SPLASH = 2002;
        public final static int EVENT_PARTICLE_EYE_DESPAWN = 2003;
        public final static int EVENT_PARTICLE_SPAWN = 2004;
        public final static int EVENT_PARTICLE_UNKNOWN = 2005;

        public final static int EVENT_START_RAIN = 3001;
        public final static int EVENT_START_THUNDER = 3002;
        public final static int EVENT_STOP_RAIN = 3003;
        public final static int EVENT_STOP_THUNDER = 3004;

        public final static int EVENT_SOUND_BUTTON_CLICK = 3500;
        public final static int EVENT_SOUND_CAULDRON = 3501;
        public final static int EVENT_SOUND_CAULDRON_DYE_ARMOR = 3502;
        public final static int EVENT_SOUND_CAULDRON_FILL_POTION = 3504;
        public final static int EVENT_SOUND_CAULDRON_FILL_WATER = 3506;

        public final static int EVENT_SET_DATA = 4000;

        public final static int EVENT_PLAYERS_SLEEPING = 9800;

        public final static int EVENT_ADD_PARTICLE_MASK = 0x4000;
    }

    public int eventID;
    public float x;
    public float y;
    public float z;
    public int datae;

    @Override
    public byte pid() {
        return ProtocolInfo.LEVEL_EVENT_PACKET;
    }

    @Override
    public void encode() {
        this.reset();
        this.putShort(this.eventID);
        this.putFloat(this.x);
        this.putFloat(this.y);
        this.putFloat(this.z);
        this.putInt(this.datae);
    }

    @Override
    public void decode() {
        eventID = this.getShort();
        x = this.getFloat();
        y = this.getFloat();
        z = this.getFloat();
        datae = this.getInt();
    }

}
