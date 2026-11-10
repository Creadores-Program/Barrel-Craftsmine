package com.nukkitx.protocol.bedrock.packet;

import com.nukkitx.protocol.bedrock.BedrockPacket;
import com.nukkitx.protocol.bedrock.BedrockPacketType;
import com.nukkitx.protocol.bedrock.handler.BedrockPacketHandler;
import io.netty.util.AsciiString;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(doNotUseGetters = true, callSuper = false, exclude = {"clientData"})
@ToString(exclude = {"chainData", "clientData"})
public class LoginPacket extends BedrockPacket {
    private int protocolVersion;

    private Version version;

    private AsciiString chainData;
    private AsciiString clientData;

    @Override
    public final boolean handle(BedrockPacketHandler handler) {
        return handler.handle(this);
    }

    public BedrockPacketType getPacketType() {
        return BedrockPacketType.LOGIN;
    }

    public enum Version {
        VANILLA,
        EDUCATION
    }
}
