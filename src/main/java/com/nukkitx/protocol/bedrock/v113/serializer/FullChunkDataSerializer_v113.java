package com.nukkitx.protocol.bedrock.v113.serializer;

import com.nukkitx.network.VarInts;
import com.nukkitx.protocol.bedrock.BedrockPacketHelper;
import com.nukkitx.protocol.bedrock.BedrockPacketSerializer;
import com.nukkitx.protocol.bedrock.packet.FullChunkDataPacket;
import io.netty.buffer.ByteBuf;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FullChunkDataSerializer_v113 implements BedrockPacketSerializer<FullChunkDataPacket> {
    public static final FullChunkDataSerializer_v113 INSTANCE = new FullChunkDataSerializer_v113();

    @Override
    public void serialize(ByteBuf buffer, BedrockPacketHelper helper, FullChunkDataPacket packet) {
        VarInts.writeInt(buffer, packet.getChunkX());
        VarInts.writeInt(buffer, packet.getChunkZ());
        helper.writeByteArray(buffer, packet.getData());
    }

    @Override
    public void deserialize(ByteBuf buffer, BedrockPacketHelper helper, FullChunkDataPacket packet) {
        packet.setChunkX(VarInts.readInt(buffer));
        packet.setChunkZ(VarInts.readInt(buffer));
        packet.setData(helper.readByteArray(buffer));
    }
}
