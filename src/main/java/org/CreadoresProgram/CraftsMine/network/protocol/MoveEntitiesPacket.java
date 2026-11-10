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
import lombok.Data;
import org.CreadoresProgram.CraftsMine.network.protocol.base.MCDPacket;
import org.CreadoresProgram.CraftsMine.network.protocol.Interface.ProtocolInfo;

public class MoveEntitiesPacket extends MCDPacket {

    public long eid;
    public double x;
    public double y;
    public double z;
    public double yaw;
    public double headYaw;
    public double pitch;

    @Override
    public byte pid() {
        return ProtocolInfo.MOVE_ENTITY_PACKET;
    }

    @Override
    public void encode() {
            this.reset();
        this.putLong(this.eid);
        this.putFloat((float) this.x);
        this.putFloat((float) this.y);
        this.putFloat((float) this.z);
        this.putByte((byte) (this.pitch / (360d / 256d)));
        this.putByte((byte) (this.headYaw / (360d / 256d)));
        this.putByte((byte) (this.yaw / (360d / 256d)));
    }

    @Override
    public void decode() {
        this.eid = this.getLong();
        this.x = this.getFloat();
        this.y = this.getFloat();
        this.z = this.getFloat();
        this.pitch = this.getByte() * (360d / 256d);
        this.yaw = this.getByte() * (360d / 256d);
        this.headYaw = this.getByte() * (360d / 256d);
    }
    public static class Entry {
        public final long eid;
        public final double x;
        public final double y;
        public final double z;
        public final double yaw;
        public final double headyaw;
        public final double pitch;

        public Entry(long eid, double x, double y, double z, double yaw, double headyaw, double pitch) {
            this.eid = eid;
            this.x = x;
            this.y = y;
            this.z = z;
            this.yaw = yaw;
            this.headyaw = headyaw;
            this.pitch = pitch;
        }
    }
}
