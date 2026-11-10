package com.nukkitx.protocol.bedrock.v113.serializer;

import com.nukkitx.protocol.bedrock.BedrockPacketHelper;
import com.nukkitx.protocol.bedrock.BedrockPacketSerializer;
import com.nukkitx.protocol.bedrock.annotation.NoEncryption;
import com.nukkitx.protocol.bedrock.packet.ServerToClientHandshakePacket;
import io.netty.buffer.ByteBuf;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoEncryption // This is sent in plain text to complete the Diffie Hellman key exchange.
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ServerToClientHandshakeSerializer_v113 implements BedrockPacketSerializer<ServerToClientHandshakePacket> {
    public static final ServerToClientHandshakeSerializer_v113 INSTANCE = new ServerToClientHandshakeSerializer_v113();

    @Override
    public void serialize(ByteBuf buffer, BedrockPacketHelper helper, ServerToClientHandshakePacket packet) {
        // Rooti/Nukkit 1.1.x writes BOTH fields as strings.
        helper.writeString(buffer, packet.getPublicKey());
        helper.writeString(buffer, packet.getServerToken());
    }

    @Override
    public void deserialize(ByteBuf buffer, BedrockPacketHelper helper, ServerToClientHandshakePacket packet) {
        // Read BOTH as strings to match the wire format used by real
        // MCPE 1.1 servers (Rooti/Nukkit). The previous byte[] read caused
        // a deserialization mismatch that broke the encryption handshake.
        packet.setPublicKey(helper.readString(buffer));
        packet.setServerToken(helper.readString(buffer));
    }
}
