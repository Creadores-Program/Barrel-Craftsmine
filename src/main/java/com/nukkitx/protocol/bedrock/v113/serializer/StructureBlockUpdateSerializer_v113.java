package com.nukkitx.protocol.bedrock.v113.serializer;

import com.nukkitx.protocol.bedrock.BedrockPacketHelper;
import com.nukkitx.protocol.bedrock.BedrockPacketSerializer;
import com.nukkitx.protocol.bedrock.packet.StructureBlockUpdatePacket;
import io.netty.buffer.ByteBuf;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StructureBlockUpdateSerializer_v113 implements BedrockPacketSerializer<StructureBlockUpdatePacket> {
    public static final StructureBlockUpdateSerializer_v113 INSTANCE = new StructureBlockUpdateSerializer_v113();

    @Override
    public void serialize(ByteBuf buffer, BedrockPacketHelper helper, StructureBlockUpdatePacket packet) {
    }

    @Override
    public void deserialize(ByteBuf buffer, BedrockPacketHelper helper, StructureBlockUpdatePacket packet) {
    }
}
