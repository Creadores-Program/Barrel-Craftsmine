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

public class TextPacket extends MCDPacket {

    public static final byte TYPE_RAW = 0;
    public static final byte TYPE_CHAT = 1;
    public static final byte TYPE_TRANSLATION = 2;
    public static final byte TYPE_POPUP = 3;
    public static final byte TYPE_TIP = 4;
    public static final byte TYPE_SYSTEM = 5;

    public byte type;
    public String source = "";
    public String message = "";
    public String[] parameters = new String[0];

    @Override
    public byte pid() {
        return ProtocolInfo.TEXT_PACKET;
    }

    @Override
    public void encode() {
        this.reset();
        this.putByte(this.type);
        switch (this.type) {
            case TYPE_POPUP:
            case TYPE_CHAT:
                this.putString(this.source);
            case TYPE_RAW:
            case TYPE_TIP:
            case TYPE_SYSTEM:
                this.putString(this.message);
                break;

            case TYPE_TRANSLATION:
                this.putString(this.message);
                this.putByte((byte) this.parameters.length);
                for (String parameter : this.parameters) {
                    this.putString(parameter);
                }
        }
    }

    @Override
    public void decode() {
        this.type = (byte) getByte();
        switch (type) {
            case TYPE_POPUP:
            case TYPE_CHAT:
                this.source = this.getString();
            case TYPE_RAW:
            case TYPE_TIP:
            case TYPE_SYSTEM:
                this.message = this.getString();
                break;

            case TYPE_TRANSLATION:
                this.message = this.getString();
                int count = this.getByte();
                parameters = new String[count];
                for (int i = 0; i < count; i++) {
                    parameters[i] = getString();
                }
        }
    }

}
