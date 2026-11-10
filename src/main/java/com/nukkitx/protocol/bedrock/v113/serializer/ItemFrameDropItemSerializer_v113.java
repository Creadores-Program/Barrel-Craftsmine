package com.nukkitx.protocol.bedrock.v113.serializer;

import com.nukkitx.protocol.bedrock.BedrockPacketHelper;
import com.nukkitx.protocol.bedrock.BedrockPacketSerializer;
import com.nukkitx.protocol.bedrock.packet.ItemFrameDropItemPacket;
import io.netty.buffer.ByteBuf;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ItemFrameDropItemSerializer_v113 implements BedrockPacketSerializer<ItemFrameDropItemPacket> {
    public static final ItemFrameDropItemSerializer_v113 INSTANCE = new ItemFrameDropItemSerializer_v113();


    @Override
    public void serialize(ByteBuf buffer, BedrockPacketHelper helper, ItemFrameDropItemPacket packet) {
        helper.writeBlockPosition(buffer, packet.getBlockPosition());
    }

    @Override
    public void deserialize(ByteBuf buffer, BedrockPacketHelper helper, ItemFrameDropItemPacket packet) {
        packet.setBlockPosition(helper.readBlockPosition(buffer));
    }
}
