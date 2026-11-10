package com.nukkitx.protocol.bedrock.v113.serializer;

import com.nukkitx.network.VarInts;
import com.nukkitx.protocol.bedrock.BedrockPacketHelper;
import com.nukkitx.protocol.bedrock.BedrockPacketSerializer;
import com.nukkitx.protocol.bedrock.BedrockSession;
import com.nukkitx.protocol.bedrock.packet.ContainerSetContentPacket;
import io.netty.buffer.ByteBuf;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ContainerSetContentSerializer_v113 implements BedrockPacketSerializer<ContainerSetContentPacket> {
    public static final ContainerSetContentSerializer_v113 INSTANCE = new ContainerSetContentSerializer_v113();

    @Override
    public void serialize(ByteBuf buffer, BedrockPacketHelper helper, ContainerSetContentPacket packet, BedrockSession session) {
        VarInts.writeUnsignedInt(buffer, packet.getWindowId());
        VarInts.writeLong(buffer, packet.getEntityRuntimeId());
        helper.writeArray(buffer, packet.getItems(), (buf, item) -> helper.writeItem(buf, item, session));

        VarInts.writeUnsignedInt(buffer, packet.getHotbar().length);

        for (int slot : packet.getHotbar())
            VarInts.writeInt(buffer, slot);
    }

    @Override
    public void deserialize(ByteBuf buffer, BedrockPacketHelper helper, ContainerSetContentPacket packet, BedrockSession session) {
        packet.setWindowId(VarInts.readUnsignedInt(buffer));
        packet.setEntityRuntimeId(VarInts.readLong(buffer));
        helper.readArray(buffer, packet.getItems(), buf -> helper.readItem(buf, session));

        int[] hotbar = new int[VarInts.readUnsignedInt(buffer)];
        for (int i = 0; i < hotbar.length; i++)
            hotbar[i] = VarInts.readInt(buffer);

        packet.setHotbar(hotbar);
    }
}
