package com.nukkitx.protocol.bedrock.v113.serializer;

import com.nukkitx.protocol.bedrock.BedrockPacketHelper;
import com.nukkitx.protocol.bedrock.BedrockPacketSerializer;
import com.nukkitx.protocol.bedrock.BedrockSession;
import com.nukkitx.protocol.bedrock.packet.DropItemPacket;
import io.netty.buffer.ByteBuf;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DropItemSerializer_v113 implements BedrockPacketSerializer<DropItemPacket> {
    public static final DropItemSerializer_v113 INSTANCE = new DropItemSerializer_v113();

    @Override
    public void serialize(ByteBuf buffer, BedrockPacketHelper helper, DropItemPacket packet, BedrockSession session) {
        buffer.writeByte(packet.getAction().ordinal());
        helper.writeItem(buffer, packet.getItem(), session);
    }

    @Override
    public void deserialize(ByteBuf buffer, BedrockPacketHelper helper, DropItemPacket packet, BedrockSession session) {
        packet.setAction(DropItemPacket.Action.values()[buffer.readUnsignedByte()]);
        packet.setItem(helper.readItem(buffer, session));
    }
}
