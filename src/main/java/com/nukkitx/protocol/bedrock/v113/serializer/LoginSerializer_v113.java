package com.nukkitx.protocol.bedrock.v113.serializer;

import com.nukkitx.network.VarInts;
import com.nukkitx.protocol.bedrock.BedrockPacketHelper;
import com.nukkitx.protocol.bedrock.BedrockPacketSerializer;
import com.nukkitx.protocol.bedrock.packet.LoginPacket;
import io.netty.buffer.ByteBuf;
import io.netty.util.AsciiString;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LoginSerializer_v113 implements BedrockPacketSerializer<LoginPacket> {
    public static final LoginSerializer_v113 INSTANCE = new LoginSerializer_v113();

    @Override
    public void serialize(ByteBuf buffer, BedrockPacketHelper helper, LoginPacket packet) {
        buffer.writeInt(packet.getProtocolVersion());
        buffer.writeByte(packet.getVersion().ordinal());

        AsciiString chainData = packet.getChainData();
        AsciiString clientData = packet.getClientData();

        VarInts.writeUnsignedInt(buffer, chainData.length() + clientData.length() + 8);

        helper.writeLEAsciiString(buffer, chainData);
        helper.writeLEAsciiString(buffer, clientData);
    }

    @Override
    public void deserialize(ByteBuf buffer, BedrockPacketHelper helper, LoginPacket packet) {
        packet.setProtocolVersion(buffer.readInt());
        packet.setVersion(LoginPacket.Version.values()[buffer.readByte()]);

        ByteBuf jwt = buffer.readSlice(VarInts.readUnsignedInt(buffer)); // Get the JWT.
        packet.setChainData(helper.readLEAsciiString(jwt));
        packet.setClientData(helper.readLEAsciiString(jwt));
    }
}
