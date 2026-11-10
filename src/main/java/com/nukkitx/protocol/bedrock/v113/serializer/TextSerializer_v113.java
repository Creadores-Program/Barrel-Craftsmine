package com.nukkitx.protocol.bedrock.v113.serializer;

import com.nukkitx.protocol.bedrock.BedrockPacketHelper;
import com.nukkitx.protocol.bedrock.BedrockPacketSerializer;
import com.nukkitx.protocol.bedrock.packet.TextPacket;
import io.netty.buffer.ByteBuf;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TextSerializer_v113 implements BedrockPacketSerializer<TextPacket> {
    public static final TextSerializer_v113 INSTANCE = new TextSerializer_v113();

    @Override
    public void serialize(ByteBuf buffer, BedrockPacketHelper helper, TextPacket packet) {
        TextPacket.Type type = packet.getType();
        buffer.writeByte(type.ordinal());

        switch (type) {
            case CHAT:
            case WHISPER:
            case POPUP:
            case ANNOUNCEMENT:
                helper.writeString(buffer, packet.getSender());
            case RAW:
            case TIP:
            case SYSTEM:
                helper.writeString(buffer, packet.getMessage());
                break;
            case TRANSLATION:
                helper.writeString(buffer, packet.getMessage());
                helper.writeArray(buffer, packet.getParameters(), helper::writeString);
                break;
            default:
                throw new UnsupportedOperationException("Unsupported TextType " + type);
        }
    }

    @Override
    public void deserialize(ByteBuf buffer, BedrockPacketHelper helper, TextPacket packet) {
        TextPacket.Type type = TextPacket.Type.values()[buffer.readUnsignedByte()];
        packet.setType(type);

        switch (type) {
            case CHAT:
            case WHISPER:
            case POPUP:
            case ANNOUNCEMENT:
                packet.setSender(helper.readString(buffer));
            case RAW:
            case TIP:
            case SYSTEM:
                packet.setMessage(helper.readString(buffer));
                break;
            case TRANSLATION:
                packet.setMessage(helper.readString(buffer));
                helper.readArray(buffer, packet.getParameters(), helper::readString);
                break;
            default:
                throw new UnsupportedOperationException("Unsupported TextType " + type);
        }
    }
}
