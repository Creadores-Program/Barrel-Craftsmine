package com.nukkitx.protocol.bedrock.v113.serializer;

import com.nukkitx.protocol.bedrock.BedrockPacketHelper;
import com.nukkitx.protocol.bedrock.BedrockPacketSerializer;
import com.nukkitx.protocol.bedrock.packet.ResourcePackChunkRequestPacket;
import io.netty.buffer.ByteBuf;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ResourcePackChunkRequestSerializer_v113 implements BedrockPacketSerializer<ResourcePackChunkRequestPacket> {
    public static final ResourcePackChunkRequestSerializer_v113 INSTANCE = new ResourcePackChunkRequestSerializer_v113();

    @Override
    public void serialize(ByteBuf buffer, BedrockPacketHelper helper, ResourcePackChunkRequestPacket packet) {
        helper.writeString(buffer, packet.getPackId().toString());
        buffer.writeIntLE(packet.getChunkIndex());
    }

    @Override
    public void deserialize(ByteBuf buffer, BedrockPacketHelper helper, ResourcePackChunkRequestPacket packet) {
        packet.setPackId(UUID.fromString(helper.readString(buffer)));
        packet.setChunkIndex(buffer.readIntLE());
    }
}
