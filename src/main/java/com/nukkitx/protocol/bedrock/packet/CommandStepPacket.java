package com.nukkitx.protocol.bedrock.packet;

import com.nukkitx.protocol.bedrock.BedrockPacket;
import com.nukkitx.protocol.bedrock.BedrockPacketType;
import com.nukkitx.protocol.bedrock.data.command.CommandOriginData;
import com.nukkitx.protocol.bedrock.handler.BedrockPacketHandler;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(doNotUseGetters = true, callSuper = false)
public class CommandStepPacket extends BedrockPacket {
    private String command;

    private String overload;

    private int unknownVarUInt; // it seems to be always 0
    private int currentStep;

    private boolean done;

    private long clientId;

    private String inputJson;
    private String outputJson;

    private CommandOriginData originData;

    @Override
    public boolean handle(BedrockPacketHandler handler) {
        return handler.handle(this);
    }

    @Override
    public BedrockPacketType getPacketType() {
        return BedrockPacketType.COMMAND_STEP;
    }
}
