package com.nukkitx.protocol.bedrock.v113.serializer;

import com.nukkitx.protocol.bedrock.BedrockPacketHelper;
import com.nukkitx.protocol.bedrock.BedrockPacketSerializer;
import com.nukkitx.protocol.bedrock.packet.SetEntityLinkPacket;
import io.netty.buffer.ByteBuf;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SetEntityLinkSerializer_v113 implements BedrockPacketSerializer<SetEntityLinkPacket> {
    public static final SetEntityLinkSerializer_v113 INSTANCE = new SetEntityLinkSerializer_v113();

    @Override
    public void serialize(ByteBuf buffer, BedrockPacketHelper helper, SetEntityLinkPacket packet) {
        helper.writeEntityLink(buffer, packet.getEntityLink());
    }

    @Override
    public void deserialize(ByteBuf buffer, BedrockPacketHelper helper, SetEntityLinkPacket packet) {
        packet.setEntityLink(helper.readEntityLink(buffer));
    }
}
