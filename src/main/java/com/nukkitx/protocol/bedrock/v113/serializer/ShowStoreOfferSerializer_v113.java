package com.nukkitx.protocol.bedrock.v113.serializer;

import com.nukkitx.protocol.bedrock.BedrockPacketHelper;
import com.nukkitx.protocol.bedrock.BedrockPacketSerializer;
import com.nukkitx.protocol.bedrock.packet.ShowStoreOfferPacket;
import io.netty.buffer.ByteBuf;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ShowStoreOfferSerializer_v113 implements BedrockPacketSerializer<ShowStoreOfferPacket> {
    public static final ShowStoreOfferSerializer_v113 INSTANCE = new ShowStoreOfferSerializer_v113();

    @Override
    public void serialize(ByteBuf buffer, BedrockPacketHelper helper, ShowStoreOfferPacket packet) {
        helper.writeString(buffer, packet.getOfferId());
    }

    @Override
    public void deserialize(ByteBuf buffer, BedrockPacketHelper helper, ShowStoreOfferPacket packet) {
        packet.setOfferId(helper.readString(buffer));
    }
}
