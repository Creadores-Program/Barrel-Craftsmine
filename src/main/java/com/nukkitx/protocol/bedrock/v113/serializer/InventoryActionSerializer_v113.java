package com.nukkitx.protocol.bedrock.v113.serializer;

import com.nukkitx.network.VarInts;
import com.nukkitx.protocol.bedrock.BedrockPacketHelper;
import com.nukkitx.protocol.bedrock.BedrockPacketSerializer;
import com.nukkitx.protocol.bedrock.BedrockSession;
import com.nukkitx.protocol.bedrock.packet.InventoryActionPacket;
import io.netty.buffer.ByteBuf;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class InventoryActionSerializer_v113 implements BedrockPacketSerializer<InventoryActionPacket> {
    public static final InventoryActionSerializer_v113 INSTANCE = new InventoryActionSerializer_v113();

    @Override
    public void serialize(ByteBuf buffer, BedrockPacketHelper helper, InventoryActionPacket packet, BedrockSession session) {
        VarInts.writeInt(buffer, packet.getAction().ordinal());
        helper.writeItem(buffer, packet.getItem(), session);
        VarInts.writeInt(buffer, packet.getEnchantmentId());
        VarInts.writeInt(buffer, packet.getEnchantmentLevel());
    }

    @Override
    public void deserialize(ByteBuf buffer, BedrockPacketHelper helper, InventoryActionPacket packet, BedrockSession session) {
        packet.setAction(InventoryActionPacket.Action.values()[VarInts.readInt(buffer)]);
        packet.setItem(helper.readItem(buffer, session));
        packet.setEnchantmentId(VarInts.readInt(buffer));
        packet.setEnchantmentLevel(VarInts.readInt(buffer));
    }
}
