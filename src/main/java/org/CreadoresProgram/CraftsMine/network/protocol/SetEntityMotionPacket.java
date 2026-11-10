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

public class SetEntityMotionPacket extends MCDPacket {

    public Entry[] entities = new Entry[0];

    @Override
    public byte pid() {
        return ProtocolInfo.SET_ENTITY_MOTION_PACKET;
    }

    @Override
    public MCDPacket clean() {
        this.entities = new Entry[0];
        return super.clean();
    }

    @Override
    public void encode() {
        this.reset();
        for (Entry entry : this.entities) {
            this.putLong(entry.entityId);
            this.putFloat((float) entry.motionX);
            this.putFloat((float) entry.motionY);
            this.putFloat((float) entry.motionZ);
        }
    }

    @Override
    public void decode() {
    }
    
    public static class Entry {
        public final long entityId;
        public final double motionX;
        public final double motionY;
        public final double motionZ;

        public Entry(long entityId, double motionX, double motionY, double motionZ) {
            this.entityId = entityId;
            this.motionX = motionX;
            this.motionY = motionY;
            this.motionZ = motionZ;
        }
    }
}
