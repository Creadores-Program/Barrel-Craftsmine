package com.nukkitx.protocol.bedrock.v113.serializer;

import com.nukkitx.protocol.bedrock.BedrockPacketHelper;
import com.nukkitx.protocol.bedrock.BedrockPacketSerializer;
import com.nukkitx.protocol.bedrock.packet.AvailableCommandsPacket;
import io.netty.buffer.ByteBuf;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AvailableCommandsSerializer_v113 implements BedrockPacketSerializer<AvailableCommandsPacket> {
    public static final AvailableCommandsSerializer_v113 INSTANCE = new AvailableCommandsSerializer_v113();

    @Override
    public void serialize(ByteBuf buffer, BedrockPacketHelper helper, AvailableCommandsPacket packet) {
        helper.writeString(buffer, packet.getCommandsJson());
        helper.writeString(buffer, packet.getUnknownString());
    }

    @Override
    public void deserialize(ByteBuf buffer, BedrockPacketHelper helper, AvailableCommandsPacket packet) {
        packet.setCommandsJson(helper.readString(buffer));
        packet.setUnknownString(helper.readString(buffer));
    }
}