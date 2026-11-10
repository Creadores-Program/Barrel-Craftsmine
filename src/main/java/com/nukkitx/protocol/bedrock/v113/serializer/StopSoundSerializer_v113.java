package com.nukkitx.protocol.bedrock.v113.serializer;

import com.nukkitx.protocol.bedrock.BedrockPacketHelper;
import com.nukkitx.protocol.bedrock.BedrockPacketSerializer;
import com.nukkitx.protocol.bedrock.packet.StopSoundPacket;
import io.netty.buffer.ByteBuf;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StopSoundSerializer_v113 implements BedrockPacketSerializer<StopSoundPacket> {
    public static final StopSoundSerializer_v113 INSTANCE = new StopSoundSerializer_v113();

    @Override
    public void serialize(ByteBuf buffer, BedrockPacketHelper helper, StopSoundPacket packet) {
        helper.writeString(buffer, packet.getSoundName());
        buffer.writeBoolean(packet.isStoppingAllSound());
    }

    @Override
    public void deserialize(ByteBuf buffer, BedrockPacketHelper helper, StopSoundPacket packet) {
        packet.setSoundName(helper.readString(buffer));
        packet.setStoppingAllSound(buffer.readBoolean());
    }
}
