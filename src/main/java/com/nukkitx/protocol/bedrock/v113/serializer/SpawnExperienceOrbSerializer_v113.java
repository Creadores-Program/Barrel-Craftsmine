package com.nukkitx.protocol.bedrock.v113.serializer;

import com.nukkitx.network.VarInts;
import com.nukkitx.protocol.bedrock.BedrockPacketHelper;
import com.nukkitx.protocol.bedrock.BedrockPacketSerializer;
import com.nukkitx.protocol.bedrock.packet.SpawnExperienceOrbPacket;
import io.netty.buffer.ByteBuf;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SpawnExperienceOrbSerializer_v113 implements BedrockPacketSerializer<SpawnExperienceOrbPacket> {
    public static final SpawnExperienceOrbSerializer_v113 INSTANCE = new SpawnExperienceOrbSerializer_v113();

    @Override
    public void serialize(ByteBuf buffer, BedrockPacketHelper helper, SpawnExperienceOrbPacket packet) {
        helper.writeVector3f(buffer, packet.getPosition());
        VarInts.writeInt(buffer, packet.getAmount());
    }

    @Override
    public void deserialize(ByteBuf buffer, BedrockPacketHelper helper, SpawnExperienceOrbPacket packet) {
        packet.setPosition(helper.readVector3f(buffer));
        packet.setAmount(VarInts.readInt(buffer));
    }
}
