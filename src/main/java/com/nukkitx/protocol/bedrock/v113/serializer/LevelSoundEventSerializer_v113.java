package com.nukkitx.protocol.bedrock.v113.serializer;

import com.nukkitx.network.VarInts;
import com.nukkitx.protocol.bedrock.BedrockPacketHelper;
import com.nukkitx.protocol.bedrock.BedrockPacketSerializer;
import com.nukkitx.protocol.bedrock.packet.LevelSoundEventPacket;
import io.netty.buffer.ByteBuf;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LevelSoundEventSerializer_v113 implements BedrockPacketSerializer<LevelSoundEventPacket> {
    public static final LevelSoundEventSerializer_v113 INSTANCE = new LevelSoundEventSerializer_v113();

    @Override
    public void serialize(ByteBuf buffer, BedrockPacketHelper helper, LevelSoundEventPacket packet) {
        buffer.writeByte(helper.getSoundEventId(packet.getSound()));
        helper.writeVector3f(buffer, packet.getPosition());
        VarInts.writeUnsignedInt(buffer, packet.getVolume());
        VarInts.writeInt(buffer, packet.getPitch());
        buffer.writeBoolean(packet.isBabySound());
        buffer.writeBoolean(packet.isRelativeVolumeDisabled());
    }

    @Override
    public void deserialize(ByteBuf buffer, BedrockPacketHelper helper, LevelSoundEventPacket packet) {
        packet.setSound(helper.getSoundEvent(buffer.readUnsignedByte()));
        packet.setPosition(helper.readVector3f(buffer));
        packet.setVolume(VarInts.readUnsignedInt(buffer));
        packet.setPitch(VarInts.readInt(buffer));
        packet.setBabySound(buffer.readBoolean());
        packet.setRelativeVolumeDisabled(buffer.readBoolean());
    }
}
