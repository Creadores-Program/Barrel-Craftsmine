package com.nukkitx.protocol.bedrock.v113.serializer;

import com.nukkitx.network.VarInts;
import com.nukkitx.protocol.bedrock.BedrockPacketHelper;
import com.nukkitx.protocol.bedrock.BedrockPacketSerializer;
import com.nukkitx.protocol.bedrock.data.DifficultyType;
import com.nukkitx.protocol.bedrock.data.DimensionType;
import com.nukkitx.protocol.bedrock.data.GameType;
import com.nukkitx.protocol.bedrock.data.GeneratorType;
import com.nukkitx.protocol.bedrock.packet.StartGamePacket;
import io.netty.buffer.ByteBuf;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StartGameSerializer_v113 implements BedrockPacketSerializer<StartGamePacket> {
    public static final StartGameSerializer_v113 INSTANCE = new StartGameSerializer_v113();

    @Override
    public void serialize(ByteBuf buffer, BedrockPacketHelper helper, StartGamePacket packet) {
        VarInts.writeLong(buffer, packet.getUniqueEntityId());
        VarInts.writeUnsignedLong(buffer, packet.getRuntimeEntityId());
        VarInts.writeInt(buffer, packet.getPlayerGameType().ordinal());
        helper.writeVector3f(buffer, packet.getPlayerPosition());
        helper.writeVector2f(buffer, packet.getPlayerRotation());

        this.writeLevelSettings(buffer, helper, packet);
    }

    @Override
    public void deserialize(ByteBuf buffer, BedrockPacketHelper helper, StartGamePacket packet) {
        packet.setUniqueEntityId(VarInts.readLong(buffer));
        packet.setRuntimeEntityId(VarInts.readUnsignedLong(buffer));
        packet.setPlayerGameType(GameType.from(VarInts.readInt(buffer)));
        packet.setPlayerPosition(helper.readVector3f(buffer));
        packet.setPlayerRotation(helper.readVector2f(buffer));

        this.readLevelSettings(buffer, helper, packet);
    }

    protected void writeLevelSettings(ByteBuf buffer, BedrockPacketHelper helper, StartGamePacket packet) {
        VarInts.writeInt(buffer, (int) packet.getSeed());
        VarInts.writeInt(buffer, packet.getDimension().ordinal());
        VarInts.writeInt(buffer, packet.getGenerator().ordinal());
        VarInts.writeInt(buffer, packet.getWorldGameType().ordinal());
        VarInts.writeInt(buffer, packet.getDifficulty().ordinal());
        helper.writeBlockPosition(buffer, packet.getSpawnPosition());
        buffer.writeBoolean(packet.isAchievementsDisabled());
        VarInts.writeInt(buffer, packet.getDayCycleStopTime());
        buffer.writeBoolean(packet.isEduMode());
        buffer.writeFloatLE(packet.getRainLevel());
        buffer.writeFloatLE(packet.getLightningLevel());
        buffer.writeBoolean(packet.isCommandsEnabled());
        buffer.writeBoolean(packet.isTexturePacksRequired());
        helper.writeArray(buffer, packet.getGamerules(), helper::writeGameRule);
        helper.writeString(buffer, packet.getLevelId());
        helper.writeString(buffer, packet.getWorldName());
        helper.writeString(buffer, packet.getPremiumWorldTemplate());
        buffer.writeBoolean(packet.isTrial());
        buffer.writeLongLE(packet.getCurrentTick());
    }

    protected void readLevelSettings(ByteBuf buffer, BedrockPacketHelper helper, StartGamePacket packet) {
        packet.setSeed(VarInts.readInt(buffer));
        packet.setDimension(DimensionType.from(VarInts.readInt(buffer)));
        packet.setGenerator(GeneratorType.from(VarInts.readInt(buffer)));
        packet.setWorldGameType(GameType.from(VarInts.readInt(buffer)));
        packet.setDifficulty(DifficultyType.from(VarInts.readInt(buffer)));
        packet.setSpawnPosition(helper.readBlockPosition(buffer));
        packet.setAchievementsDisabled(buffer.readBoolean());
        packet.setDayCycleStopTime(VarInts.readInt(buffer));
        packet.setEduMode(buffer.readBoolean());
        packet.setRainLevel(buffer.readFloatLE());
        packet.setLightningLevel(buffer.readFloatLE());
        packet.setCommandsEnabled(buffer.readBoolean());
        packet.setTexturePacksRequired(buffer.readBoolean());
        helper.readArray(buffer, packet.getGamerules(), helper::readGameRule);
        packet.setLevelId(helper.readString(buffer));
        packet.setWorldName(helper.readString(buffer));
        packet.setPremiumWorldTemplate(helper.readString(buffer));
        packet.setTrial(buffer.readBoolean());
        packet.setCurrentTick(buffer.readLongLE());
    }
}
