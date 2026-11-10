package com.nukkitx.protocol.bedrock.v113.serializer;

import com.nukkitx.network.VarInts;
import com.nukkitx.protocol.bedrock.BedrockPacketHelper;
import com.nukkitx.protocol.bedrock.BedrockPacketSerializer;
import com.nukkitx.protocol.bedrock.packet.CommandStepPacket;

import io.netty.buffer.ByteBuf;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommandStepSerializer_v113 implements BedrockPacketSerializer<CommandStepPacket> {
    public static final CommandStepSerializer_v113 INSTANCE = new CommandStepSerializer_v113();

    @Override
    public void serialize(ByteBuf buffer, BedrockPacketHelper helper, CommandStepPacket packet) {
        helper.writeString(buffer, packet.getCommand());
        helper.writeString(buffer, packet.getOverload());
        VarInts.writeUnsignedInt(buffer, packet.getUnknownVarUInt());
        VarInts.writeUnsignedInt(buffer, packet.getCurrentStep());
        buffer.writeBoolean(packet.isDone());
        VarInts.writeUnsignedLong(buffer, packet.getClientId());
        helper.writeString(buffer, packet.getInputJson());
        helper.writeString(buffer, packet.getOutputJson());
        helper.writeCommandOrigin(buffer, packet.getOriginData());
    }

    @Override
    public void deserialize(ByteBuf buffer, BedrockPacketHelper helper, CommandStepPacket packet) {
        packet.setCommand(helper.readString(buffer));
        packet.setOverload(helper.readString(buffer));
        packet.setUnknownVarUInt(VarInts.readUnsignedInt(buffer));
        packet.setCurrentStep(VarInts.readUnsignedInt(buffer));
        packet.setDone(buffer.readBoolean());
        packet.setClientId(VarInts.readUnsignedLong(buffer));
        packet.setInputJson(helper.readString(buffer));
        packet.setOutputJson(helper.readString(buffer));
        packet.setOriginData(helper.readCommandOrigin(buffer));
    }
}
