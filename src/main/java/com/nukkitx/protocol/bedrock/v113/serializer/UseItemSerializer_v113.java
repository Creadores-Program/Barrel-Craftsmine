package com.nukkitx.protocol.bedrock.v113.serializer;

import com.nukkitx.network.VarInts;
import com.nukkitx.protocol.bedrock.BedrockPacketHelper;
import com.nukkitx.protocol.bedrock.BedrockPacketSerializer;
import com.nukkitx.protocol.bedrock.BedrockSession;
import com.nukkitx.protocol.bedrock.packet.UseItemPacket;
import io.netty.buffer.ByteBuf;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UseItemSerializer_v113 implements BedrockPacketSerializer<UseItemPacket> {
    public static final UseItemSerializer_v113 INSTANCE = new UseItemSerializer_v113();

    @Override
    public void serialize(ByteBuf buffer, BedrockPacketHelper helper, UseItemPacket packet, BedrockSession session) {
        helper.writeBlockPosition(buffer, packet.getBlockPosition());
        VarInts.writeUnsignedInt(buffer, packet.getHotbarSlot());
        VarInts.writeInt(buffer, packet.getFace());
        helper.writeVector3f(buffer, packet.getFacePosition());
        helper.writeVector3f(buffer, packet.getPosition());
        VarInts.writeInt(buffer, packet.getSlot());
        helper.writeItem(buffer, packet.getItem(), session);
    }

    @Override
    public void deserialize(ByteBuf buffer, BedrockPacketHelper helper, UseItemPacket packet, BedrockSession session) {
        packet.setBlockPosition(helper.readBlockPosition(buffer));
        packet.setHotbarSlot(VarInts.readUnsignedInt(buffer));
        packet.setFace(VarInts.readInt(buffer));
        packet.setFacePosition(helper.readVector3f(buffer));
        packet.setPosition(helper.readVector3f(buffer));
        packet.setSlot(VarInts.readInt(buffer));
        packet.setItem(helper.readItem(buffer, session));
    }
}
