package com.nukkitx.protocol.bedrock.v113.serializer;

import com.nukkitx.network.VarInts;
import com.nukkitx.protocol.bedrock.BedrockPacketHelper;
import com.nukkitx.protocol.bedrock.BedrockPacketSerializer;
import com.nukkitx.protocol.bedrock.BedrockSession;
import com.nukkitx.protocol.bedrock.packet.AddPlayerPacket;
import io.netty.buffer.ByteBuf;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AddPlayerSerializer_v113 implements BedrockPacketSerializer<AddPlayerPacket> {
    public static final AddPlayerSerializer_v113 INSTANCE = new AddPlayerSerializer_v113();

    @Override
    public void serialize(ByteBuf buffer, BedrockPacketHelper helper, AddPlayerPacket packet, BedrockSession session) {
        helper.writeUuid(buffer, packet.getUuid());
        helper.writeString(buffer, packet.getUsername());
        VarInts.writeLong(buffer, packet.getUniqueEntityId());
        VarInts.writeUnsignedLong(buffer, packet.getRuntimeEntityId());
        helper.writeVector3f(buffer, packet.getPosition());
        helper.writeVector3f(buffer, packet.getMotion());
        helper.writeVector3f(buffer, packet.getRotation());
        helper.writeItem(buffer, packet.getHand(), session);
        helper.writeEntityData(buffer, packet.getMetadata());
    }

    @Override
    public void deserialize(ByteBuf buffer, BedrockPacketHelper helper, AddPlayerPacket packet, BedrockSession session) {
        packet.setUuid(helper.readUuid(buffer));
        packet.setUsername(helper.readString(buffer));
        packet.setUniqueEntityId(VarInts.readLong(buffer));
        packet.setRuntimeEntityId(VarInts.readUnsignedLong(buffer));
        packet.setPosition(helper.readVector3f(buffer));
        packet.setMotion(helper.readVector3f(buffer));
        packet.setRotation(helper.readVector3f(buffer));
        packet.setHand(helper.readItem(buffer, session));
        helper.readEntityData(buffer, packet.getMetadata());
    }
}
