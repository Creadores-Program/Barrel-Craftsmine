package com.nukkitx.protocol.bedrock.packet;

import com.nukkitx.protocol.bedrock.BedrockPacket;
import com.nukkitx.protocol.bedrock.BedrockPacketType;
import com.nukkitx.protocol.bedrock.handler.BedrockPacketHandler;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(doNotUseGetters = true, callSuper = false)
@ToString(doNotUseGetters = true, exclude = {"data"})
public class FullChunkDataPacket extends BedrockPacket {
    private int chunkX;
    private int chunkZ;

    private byte[] data;

    @Override
    public boolean handle(BedrockPacketHandler handler) {
        return handler.handle(this);
    }

    @Override
    public BedrockPacketType getPacketType() {
        return BedrockPacketType.FULL_CHUNK_DATA;
    }
}
