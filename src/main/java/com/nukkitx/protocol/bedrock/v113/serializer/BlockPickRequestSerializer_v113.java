package com.nukkitx.protocol.bedrock.v113.serializer;

import com.nukkitx.protocol.bedrock.BedrockPacketHelper;
import com.nukkitx.protocol.bedrock.BedrockPacketSerializer;
import com.nukkitx.protocol.bedrock.packet.BlockPickRequestPacket;
import io.netty.buffer.ByteBuf;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BlockPickRequestSerializer_v113 implements BedrockPacketSerializer<BlockPickRequestPacket> {
    public static final BlockPickRequestSerializer_v113 INSTANCE = new BlockPickRequestSerializer_v113();

    @Override
    public void serialize(ByteBuf buffer, BedrockPacketHelper helper, BlockPickRequestPacket packet) {
        helper.writeVector3i(buffer, packet.getBlockPosition());
        buffer.writeByte(packet.getHotbarSlot());
    }

    @Override
    public void deserialize(ByteBuf buffer, BedrockPacketHelper helper, BlockPickRequestPacket packet) {
        packet.setBlockPosition(helper.readVector3i(buffer));
        packet.setHotbarSlot(buffer.readUnsignedByte());
    }
}
