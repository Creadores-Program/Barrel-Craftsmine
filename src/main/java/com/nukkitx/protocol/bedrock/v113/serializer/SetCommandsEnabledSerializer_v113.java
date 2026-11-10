package com.nukkitx.protocol.bedrock.v113.serializer;

import com.nukkitx.protocol.bedrock.BedrockPacketHelper;
import com.nukkitx.protocol.bedrock.BedrockPacketSerializer;
import com.nukkitx.protocol.bedrock.packet.SetCommandsEnabledPacket;
import io.netty.buffer.ByteBuf;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SetCommandsEnabledSerializer_v113 implements BedrockPacketSerializer<SetCommandsEnabledPacket> {
    public static final SetCommandsEnabledSerializer_v113 INSTANCE = new SetCommandsEnabledSerializer_v113();

    @Override
    public void serialize(ByteBuf buffer, BedrockPacketHelper helper, SetCommandsEnabledPacket packet) {
        buffer.writeBoolean(packet.isCommandsEnabled());
    }

    @Override
    public void deserialize(ByteBuf buffer, BedrockPacketHelper helper, SetCommandsEnabledPacket packet) {
        packet.setCommandsEnabled(buffer.readBoolean());
    }
}
