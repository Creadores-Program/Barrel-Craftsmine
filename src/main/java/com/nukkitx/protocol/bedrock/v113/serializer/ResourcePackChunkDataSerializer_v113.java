package com.nukkitx.protocol.bedrock.v113.serializer;

import com.nukkitx.protocol.bedrock.BedrockPacketHelper;
import com.nukkitx.protocol.bedrock.BedrockPacketSerializer;
import com.nukkitx.protocol.bedrock.packet.ResourcePackChunkDataPacket;
import io.netty.buffer.ByteBuf;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ResourcePackChunkDataSerializer_v113 implements BedrockPacketSerializer<ResourcePackChunkDataPacket> {
    public static final ResourcePackChunkDataSerializer_v113 INSTANCE = new ResourcePackChunkDataSerializer_v113();

    @Override
    public void serialize(ByteBuf buffer, BedrockPacketHelper helper, ResourcePackChunkDataPacket packet) {
        helper.writeString(buffer, packet.getPackId().toString());
        buffer.writeIntLE(packet.getChunkIndex());
        buffer.writeLongLE(packet.getProgress());
        byte[] data = packet.getData();
        buffer.writeIntLE(data.length);
        buffer.writeBytes(data);
    }

    @Override
    public void deserialize(ByteBuf buffer, BedrockPacketHelper helper, ResourcePackChunkDataPacket packet) {
        packet.setPackId(UUID.fromString(helper.readString(buffer)));
        packet.setChunkIndex(buffer.readIntLE());
        packet.setProgress(buffer.readLongLE());
        byte[] data = new byte[buffer.readIntLE()];
        buffer.readBytes(data);
        packet.setData(data);
    }
}
