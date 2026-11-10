package com.nukkitx.protocol.bedrock.v113.serializer;

import com.nukkitx.network.VarInts;
import com.nukkitx.protocol.bedrock.BedrockPacketHelper;
import com.nukkitx.protocol.bedrock.BedrockPacketSerializer;
import com.nukkitx.protocol.bedrock.BedrockSession;
import com.nukkitx.protocol.bedrock.packet.ContainerSetSlotPacket;
import io.netty.buffer.ByteBuf;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ContainerSetSlotSerializer_v113 implements BedrockPacketSerializer<ContainerSetSlotPacket> {
    public static final ContainerSetSlotSerializer_v113 INSTANCE = new ContainerSetSlotSerializer_v113();

    @Override
    public void serialize(ByteBuf buffer, BedrockPacketHelper helper, ContainerSetSlotPacket packet, BedrockSession session) {
        buffer.writeByte(packet.getWindowId());
        VarInts.writeInt(buffer, packet.getSlot());
        VarInts.writeInt(buffer, packet.getHotbarSlot());
        helper.writeItem(buffer, packet.getItem(), session);
        buffer.writeByte(packet.getSelectSlot());
    }

    @Override
    public void deserialize(ByteBuf buffer, BedrockPacketHelper helper, ContainerSetSlotPacket packet, BedrockSession session) {
        packet.setWindowId(buffer.readUnsignedByte());
        packet.setSlot(VarInts.readInt(buffer));
        packet.setHotbarSlot(VarInts.readInt(buffer));
        packet.setItem(helper.readItem(buffer, session));
        packet.setSelectSlot(buffer.readUnsignedByte());
    }
}
