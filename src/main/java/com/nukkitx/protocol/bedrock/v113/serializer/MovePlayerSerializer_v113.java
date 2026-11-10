package com.nukkitx.protocol.bedrock.v113.serializer;

import com.nukkitx.network.VarInts;
import com.nukkitx.protocol.bedrock.BedrockPacketHelper;
import com.nukkitx.protocol.bedrock.BedrockPacketSerializer;
import com.nukkitx.protocol.bedrock.packet.MovePlayerPacket;
import io.netty.buffer.ByteBuf;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import static com.nukkitx.protocol.bedrock.packet.MovePlayerPacket.Animation;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MovePlayerSerializer_v113 implements BedrockPacketSerializer<MovePlayerPacket> {
    public static final MovePlayerSerializer_v113 INSTANCE = new MovePlayerSerializer_v113();

    @Override
    public void serialize(ByteBuf buffer, BedrockPacketHelper helper, MovePlayerPacket packet) {
        VarInts.writeUnsignedLong(buffer, packet.getRuntimeEntityId());
        helper.writeVector3f(buffer, packet.getPosition());
        helper.writeVector3f(buffer, packet.getRotation());
        buffer.writeByte(packet.getAnimation().ordinal());
        buffer.writeBoolean(packet.isOnGround());
        VarInts.writeUnsignedLong(buffer, packet.getRidingRuntimeEntityId());
        if (packet.getAnimation() == Animation.TELEPORT) {
            buffer.writeIntLE(packet.getUnknownInt1());
            buffer.writeIntLE(packet.getUnknownInt2());
        }
    }

    @Override
    public void deserialize(ByteBuf buffer, BedrockPacketHelper helper, MovePlayerPacket packet) {
        packet.setRuntimeEntityId(VarInts.readUnsignedLong(buffer));
        packet.setPosition(helper.readVector3f(buffer));
        packet.setRotation(helper.readVector3f(buffer));
        packet.setAnimation(Animation.values()[buffer.readUnsignedByte()]);
        packet.setOnGround(buffer.readBoolean());
        packet.setRidingRuntimeEntityId(VarInts.readUnsignedLong(buffer));
        if (packet.getAnimation() == Animation.TELEPORT) {
            packet.setUnknownInt1(buffer.readIntLE());
            packet.setUnknownInt2(buffer.readIntLE());
        }
    }
}
