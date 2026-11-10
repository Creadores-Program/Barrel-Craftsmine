package com.nukkitx.protocol.bedrock.util;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.ByteBufUtil;

import java.nio.charset.StandardCharsets;

public class LittleEndianByteBufOutputStream extends ByteBufOutputStream {
    private final ByteBuf buffer;

    public LittleEndianByteBufOutputStream(ByteBuf buffer) {
        super(buffer);
        this.buffer = buffer;
    }

    @Override
    public void writeChar(int v) {
        this.buffer.writeChar(Character.reverseBytes((char) v));
    }

    @Override
    public void writeDouble(double v) {
        this.buffer.writeDoubleLE(v);
    }

    @Override
    public void writeFloat(float v) {
        this.buffer.writeFloatLE(v);
    }

    @Override
    public void writeShort(int val) {
        this.buffer.writeShortLE(val);
    }

    @Override
    public void writeLong(long val) {
        this.buffer.writeLongLE(val);
    }

    @Override
    public void writeInt(int val) {
        this.buffer.writeIntLE(val);
    }

    @Override
    public void writeUTF(String string) {
        this.writeShort(ByteBufUtil.utf8Bytes(string));
        this.buffer.writeCharSequence(string, StandardCharsets.UTF_8);
    }
}
