package com.nukkitx.protocol.bedrock.v113.serializer;

import com.nukkitx.network.VarInts;
import com.nukkitx.protocol.bedrock.BedrockPacketHelper;
import com.nukkitx.protocol.bedrock.BedrockPacketSerializer;
import com.nukkitx.protocol.bedrock.packet.MoveEntityPacket;
import io.netty.buffer.ByteBuf;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MoveEntitySerializer_v113 implements BedrockPacketSerializer<MoveEntityPacket> {
    public static final MoveEntitySerializer_v113 INSTANCE = new MoveEntitySerializer_v113();

    @Override
    public void serialize(ByteBuf buffer, BedrockPacketHelper helper, MoveEntityPacket packet) {
        VarInts.writeUnsignedLong(buffer, packet.getRuntimeEntityId());
        helper.writeVector3f(buffer, packet.getPosition());
        buffer.writeByte(packet.pitch);
        buffer.writeByte(packet.headYaw);
        buffer.writeByte(packet.yaw);
        buffer.writeBoolean(packet.isOnGround());
        buffer.writeBoolean(packet.isTeleported());
    }

    @Override
    public void deserialize(ByteBuf buffer, BedrockPacketHelper helper, MoveEntityPacket packet) {
        packet.setRuntimeEntityId(VarInts.readUnsignedLong(buffer));
        packet.setPosition(helper.readVector3f(buffer));
        packet.setPitch(buffer.readByte());
        packet.setHeadYaw(buffer.readByte());
        packet.setYaw(buffer.readByte());
        packet.setOnGround(buffer.readBoolean());
        packet.setTeleported(buffer.readBoolean());
    }
}
