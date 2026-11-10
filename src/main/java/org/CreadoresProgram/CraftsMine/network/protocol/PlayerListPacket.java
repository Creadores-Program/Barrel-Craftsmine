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
import java.util.ArrayList;
import java.util.UUID;
import lombok.Data;
import org.CreadoresProgram.CraftsMine.network.protocol.base.MCDPacket;
import org.CreadoresProgram.CraftsMine.network.protocol.Interface.ProtocolInfo;
import org.CreadoresProgram.CraftsMine.nukkitLib.entity.data.Skin;

public class PlayerListPacket extends MCDPacket {
    
    public static final byte TYPE_ADD = 0;
    public static final byte TYPE_REMOVE = 1;

    public byte type;
    public Entry[] entries = new Entry[0];

    @Override
    public byte pid() {
        return ProtocolInfo.PLAYER_LIST_PACKET;
    }

    @Override
    public void encode() {
            this.reset();
        this.putByte(this.type);
        this.putInt(this.entries.length);
        for (Entry entry : this.entries) {
            if (type == TYPE_ADD) {
                this.putUUID(entry.uuid);
                this.putLong(entry.entityId);
                this.putString(entry.name);
                this.putSkin(entry.skin);
            } else {
                this.putUUID(entry.uuid);
            }
        }
    }

    @Override
    public void decode() {
    }

    public static class Entry {

        public final UUID uuid;
        public long entityId = 0;
        public String name = "";
        public Skin skin;

        public Entry(UUID uuid) {
            this.uuid = uuid;
        }

        public Entry(UUID uuid, long entityId, String name, Skin skin) {
            this.uuid = uuid;
            this.entityId = entityId;
            this.name = name;
            this.skin = skin;
        }
    }

}
