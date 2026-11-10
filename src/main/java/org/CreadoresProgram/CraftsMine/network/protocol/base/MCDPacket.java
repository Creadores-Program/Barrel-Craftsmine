package org.CreadoresProgram.CraftsMine.network.protocol.base;
import lombok.Getter;
import lombok.Setter;
import org.CreadoresProgram.CraftsMine.nukkitLib.raknet.protocol.EncapsulatedPacket;
import org.CreadoresProgram.CraftsMine.dragonetLib.proxy.utilities.BinaryStream;
public abstract class MCDPacket extends BinaryStream implements Cloneable{
    private int length;

    public boolean isEncoded = false;

    @Getter
    @Setter
    private int channel = 0;

    public EncapsulatedPacket encapsulatedPacket;

    public byte reliability;

    public Integer orderIndex = null;

    public Integer orderChannel = null;

    public abstract byte pid();

    public abstract void encode();

    public abstract void decode();

    @Override
    public void reset() {
        super.reset();
        this.putByte(this.pid());
    }

    public MCDPacket clean() {
        this.setBuffer(null);

        this.isEncoded = false;
        this.offset = 0;
        return this;
    }

    @Getter
    @Setter
    private boolean shouldSendImmediate;

    public final void setLength(int length) {
        this.length = length;
    }

    public final int getLength() {
        return this.length;
    }
    @Override
    public MCDPacket clone() {
        try {
            return (MCDPacket) super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }
}
