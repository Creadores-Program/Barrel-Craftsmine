package com.nukkitx.protocol.bedrock.v113.serializer;

import com.nukkitx.network.VarInts;
import com.nukkitx.protocol.bedrock.BedrockPacketHelper;
import com.nukkitx.protocol.bedrock.BedrockPacketSerializer;
import com.nukkitx.protocol.bedrock.packet.SetSpawnPositionPacket;
import io.netty.buffer.ByteBuf;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SetSpawnPositionSerializer_v113 implements BedrockPacketSerializer<SetSpawnPositionPacket> {
    public static final SetSpawnPositionSerializer_v113 INSTANCE = new SetSpawnPositionSerializer_v113();

    @Override
    public void serialize(ByteBuf buffer, BedrockPacketHelper helper, SetSpawnPositionPacket packet) {
        VarInts.writeInt(buffer, packet.getSpawnType().ordinal());
        helper.writeBlockPosition(buffer, packet.getBlockPosition());
        buffer.writeBoolean(packet.isSpawnForced());
    }

    @Override
    public void deserialize(ByteBuf buffer, BedrockPacketHelper helper, SetSpawnPositionPacket packet) {
        packet.setSpawnType(SetSpawnPositionPacket.Type.values()[VarInts.readInt(buffer)]);
        packet.setBlockPosition(helper.readBlockPosition(buffer));
        packet.setSpawnForced(buffer.readBoolean());
    }
}
