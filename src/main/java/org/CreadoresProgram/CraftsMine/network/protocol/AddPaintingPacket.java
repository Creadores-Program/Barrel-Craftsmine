/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.CreadoresProgram.CraftsMine.network.protocol;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.CreadoresProgram.CraftsMine.network.protocol.base.MCDPacket;
import org.CreadoresProgram.CraftsMine.network.protocol.Interface.ProtocolInfo;

public class AddPaintingPacket extends MCDPacket {

    public long eid;
    public int x;
    public int y;
    public int z;
    public int direction;
    public String title;

    @Override
    public byte pid() {
        return ProtocolInfo.ADD_PAINTING_PACKET;
    }

    @Override
    public void encode() {
            this.reset();
            this.putLong(eid);
            this.putInt(x);
            this.putInt(y);
            this.putInt(z);
            this.putInt(direction);
            this.putString(title);
    }

    @Override
    public void decode() {
    }

}
