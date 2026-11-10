package com.nukkitx.protocol.bedrock.v113.serializer;

import com.nukkitx.protocol.bedrock.BedrockPacketHelper;
import com.nukkitx.protocol.bedrock.BedrockPacketSerializer;
import com.nukkitx.protocol.bedrock.BedrockSession;
import com.nukkitx.protocol.bedrock.packet.ReplaceItemInSlotPacket;
import io.netty.buffer.ByteBuf;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReplaceItemInSlotSerializer_v113 implements BedrockPacketSerializer<ReplaceItemInSlotPacket> {
    public static final ReplaceItemInSlotSerializer_v113 INSTANCE = new ReplaceItemInSlotSerializer_v113();

    @Override
    public void serialize(ByteBuf buffer, BedrockPacketHelper helper, ReplaceItemInSlotPacket packet, BedrockSession session) {
        helper.writeItem(buffer, packet.getItem(), session);
    }

    @Override
    public void deserialize(ByteBuf buffer, BedrockPacketHelper helper, ReplaceItemInSlotPacket packet, BedrockSession session) {
        packet.setItem(helper.readItem(buffer, session));
    }
}
