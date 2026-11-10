package org.CreadoresProgram.CraftsMine.network.protocol;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
import org.CreadoresProgram.CraftsMine.network.protocol.base.MCDPacket;
import org.CreadoresProgram.CraftsMine.network.protocol.Interface.ProtocolInfo;
import org.CreadoresProgram.CraftsMine.network.protocol.Interface.Protocol;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;
public class BatchPacket extends MCDPacket {
    public byte[] payload;

    @Override
    public byte pid() {
        return ProtocolInfo.BATCH_PACKET;
    }

    @Override
    public void encode() {
            setShouldSendImmediate(true);    //We don't waste our memory

            //Combine all packets
            this.reset();
            this.putInt(this.payload.length);
            this.put(this.payload);
    }

    @Override
    public void decode() {
            this.payload = this.get(this.getInt());
    }
}
