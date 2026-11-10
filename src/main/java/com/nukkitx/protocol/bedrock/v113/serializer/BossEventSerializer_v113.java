package com.nukkitx.protocol.bedrock.v113.serializer;

import com.nukkitx.network.VarInts;
import com.nukkitx.protocol.bedrock.BedrockPacketHelper;
import com.nukkitx.protocol.bedrock.BedrockPacketSerializer;
import com.nukkitx.protocol.bedrock.packet.BossEventPacket;
import io.netty.buffer.ByteBuf;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BossEventSerializer_v113 implements BedrockPacketSerializer<BossEventPacket> {
    public static final BossEventSerializer_v113 INSTANCE = new BossEventSerializer_v113();

    @Override
    public void serialize(ByteBuf buffer, BedrockPacketHelper helper, BossEventPacket packet) {
        VarInts.writeLong(buffer, packet.getBossUniqueEntityId());
        VarInts.writeUnsignedInt(buffer, packet.getAction().ordinal());
        this.serializeAction(buffer, helper, packet);
    }

    @Override
    public void deserialize(ByteBuf buffer, BedrockPacketHelper helper, BossEventPacket packet) {
        packet.setBossUniqueEntityId(VarInts.readLong(buffer));

        BossEventPacket.Action[] actions = BossEventPacket.Action.values();
        int action = VarInts.readUnsignedInt(buffer);

        if (action >= 0 && action < actions.length) {
            packet.setAction(actions[action]);

            this.deserializeAction(buffer, helper, packet);
        }
    }

    protected void serializeAction(ByteBuf buffer, BedrockPacketHelper helper, BossEventPacket packet) {
        switch (packet.getAction()) {
            case REGISTER_PLAYER:
            case UNREGISTER_PLAYER:
                VarInts.writeLong(buffer, packet.getPlayerUniqueEntityId());
                break;
            case SHOW:
                helper.writeString(buffer, packet.getTitle());
                buffer.writeFloatLE(packet.getHealthPercentage());
                // fall through
            case UPDATE_PROPERTIES:
                buffer.writeShortLE(packet.getDarkenSky());
                // fall through
            case UPDATE_STYLE:
                VarInts.writeUnsignedInt(buffer, packet.getColor());
                VarInts.writeUnsignedInt(buffer, packet.getOverlay());
                break;
            case UPDATE_PERCENTAGE:
                buffer.writeFloatLE(packet.getHealthPercentage());
                break;
            case UPDATE_NAME:
                helper.writeString(buffer, packet.getTitle());
                break;
            case REMOVE:
                break;
            default:
                throw new RuntimeException("BossEvent transactionType was unknown!");
        }
    }

    protected void deserializeAction(ByteBuf buffer, BedrockPacketHelper helper, BossEventPacket packet) {
        switch (packet.getAction()) {
            case REGISTER_PLAYER:
            case UNREGISTER_PLAYER:
                packet.setPlayerUniqueEntityId(VarInts.readLong(buffer));
                break;
            case SHOW:
                packet.setTitle(helper.readString(buffer));
                packet.setHealthPercentage(buffer.readFloatLE());
                // fall through
            case UPDATE_PROPERTIES:
                packet.setDarkenSky(buffer.readUnsignedShortLE());
                // fall through
            case UPDATE_STYLE:
                packet.setColor(VarInts.readUnsignedInt(buffer));
                packet.setOverlay(VarInts.readUnsignedInt(buffer));
                break;
            case UPDATE_PERCENTAGE:
                packet.setHealthPercentage(buffer.readFloatLE());
                break;
            case UPDATE_NAME:
                packet.setTitle(helper.readString(buffer));
                break;
        }
    }
}
