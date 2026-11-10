package com.nukkitx.protocol.bedrock.v113.serializer;

import com.nukkitx.network.VarInts;
import com.nukkitx.protocol.bedrock.BedrockPacketHelper;
import com.nukkitx.protocol.bedrock.BedrockPacketSerializer;
import com.nukkitx.protocol.bedrock.data.DifficultyType;
import com.nukkitx.protocol.bedrock.packet.SetDifficultyPacket;
import io.netty.buffer.ByteBuf;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SetDifficultySerializer_v113 implements BedrockPacketSerializer<SetDifficultyPacket> {
    public static final SetDifficultySerializer_v113 INSTANCE = new SetDifficultySerializer_v113();

    @Override
    public void serialize(ByteBuf buffer, BedrockPacketHelper helper, SetDifficultyPacket packet) {
        VarInts.writeUnsignedInt(buffer, packet.getDifficulty().ordinal());
    }

    @Override
    public void deserialize(ByteBuf buffer, BedrockPacketHelper helper, SetDifficultyPacket packet) {
        packet.setDifficulty(DifficultyType.from(VarInts.readUnsignedInt(buffer)));
    }
}
