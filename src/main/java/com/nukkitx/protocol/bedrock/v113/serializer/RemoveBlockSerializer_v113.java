package com.nukkitx.protocol.bedrock.v113.serializer;

import com.nukkitx.protocol.bedrock.BedrockPacketHelper;
import com.nukkitx.protocol.bedrock.BedrockPacketSerializer;
import com.nukkitx.protocol.bedrock.packet.RemoveBlockPacket;
import io.netty.buffer.ByteBuf;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RemoveBlockSerializer_v113 implements BedrockPacketSerializer<RemoveBlockPacket> {
    public static final RemoveBlockSerializer_v113 INSTANCE = new RemoveBlockSerializer_v113();

    @Override
    public void serialize(ByteBuf buffer, BedrockPacketHelper helper, RemoveBlockPacket packet) {
        helper.writeBlockPosition(buffer, packet.getBlockPosition());
    }

    @Override
    public void deserialize(ByteBuf buffer, BedrockPacketHelper helper, RemoveBlockPacket packet) {
        packet.setBlockPosition(helper.readBlockPosition(buffer));
    }
}
