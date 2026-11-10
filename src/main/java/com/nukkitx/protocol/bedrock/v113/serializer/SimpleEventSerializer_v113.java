package com.nukkitx.protocol.bedrock.v113.serializer;

import com.nukkitx.protocol.bedrock.BedrockPacketHelper;
import com.nukkitx.protocol.bedrock.BedrockPacketSerializer;
import com.nukkitx.protocol.bedrock.data.SimpleEventType;
import com.nukkitx.protocol.bedrock.packet.SimpleEventPacket;
import io.netty.buffer.ByteBuf;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SimpleEventSerializer_v113 implements BedrockPacketSerializer<SimpleEventPacket> {
    public static final SimpleEventSerializer_v113 INSTANCE = new SimpleEventSerializer_v113();

    @Override
    public void serialize(ByteBuf buffer, BedrockPacketHelper helper, SimpleEventPacket packet) {
        buffer.writeShortLE(packet.getEvent().ordinal());
    }

    @Override
    public void deserialize(ByteBuf buffer, BedrockPacketHelper helper, SimpleEventPacket packet) {
        packet.setEvent(SimpleEventType.values()[buffer.readUnsignedShortLE()]);
    }
}
