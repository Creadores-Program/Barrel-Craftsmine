package com.nukkitx.protocol.bedrock.v113.serializer;

import com.nukkitx.network.VarInts;
import com.nukkitx.protocol.bedrock.BedrockPacketHelper;
import com.nukkitx.protocol.bedrock.BedrockPacketSerializer;
import com.nukkitx.protocol.bedrock.packet.RemoveEntityPacket;
import io.netty.buffer.ByteBuf;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RemoveEntitySerializer_v113 implements BedrockPacketSerializer<RemoveEntityPacket> {
    public static final RemoveEntitySerializer_v113 INSTANCE = new RemoveEntitySerializer_v113();

    @Override
    public void serialize(ByteBuf buffer, BedrockPacketHelper helper, RemoveEntityPacket packet) {
        VarInts.writeLong(buffer, packet.getUniqueEntityId());
    }

    @Override
    public void deserialize(ByteBuf buffer, BedrockPacketHelper helper, RemoveEntityPacket packet) {
        packet.setUniqueEntityId(VarInts.readLong(buffer));
    }
}
