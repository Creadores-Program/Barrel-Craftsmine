package com.nukkitx.protocol.bedrock.v113.serializer;

import com.nukkitx.protocol.bedrock.BedrockPacketHelper;
import com.nukkitx.protocol.bedrock.BedrockPacketSerializer;
import com.nukkitx.protocol.bedrock.packet.AddBehaviorTreePacket;
import io.netty.buffer.ByteBuf;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AddBehaviorTreeSerializer_v113 implements BedrockPacketSerializer<AddBehaviorTreePacket> {
    public static final AddBehaviorTreeSerializer_v113 INSTANCE = new AddBehaviorTreeSerializer_v113();

    @Override
    public void serialize(ByteBuf buffer, BedrockPacketHelper helper, AddBehaviorTreePacket packet) {
        helper.writeString(buffer, packet.getBehaviorTreeJson());
    }

    @Override
    public void deserialize(ByteBuf buffer, BedrockPacketHelper helper, AddBehaviorTreePacket packet) {
        packet.setBehaviorTreeJson(helper.readString(buffer));
    }
}
