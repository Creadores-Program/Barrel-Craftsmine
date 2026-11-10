package com.nukkitx.protocol.bedrock.v113.serializer;

import com.nukkitx.protocol.bedrock.BedrockPacketHelper;
import com.nukkitx.protocol.bedrock.BedrockPacketSerializer;
import com.nukkitx.protocol.bedrock.packet.ResourcePacksInfoPacket;
import io.netty.buffer.ByteBuf;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import static java.util.Objects.requireNonNull;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ResourcePacksInfoSerializer_v113 implements BedrockPacketSerializer<ResourcePacksInfoPacket> {
    public static final ResourcePacksInfoSerializer_v113 INSTANCE = new ResourcePacksInfoSerializer_v113();

    @Override
    public void serialize(ByteBuf buffer, BedrockPacketHelper helper, ResourcePacksInfoPacket packet) {
        buffer.writeBoolean(packet.isForcedToAccept());
        helper.writeArray(buffer, packet.getBehaviorPackInfos(), ByteBuf::writeShortLE, this::writeEntry);
        helper.writeArray(buffer, packet.getResourcePackInfos(), ByteBuf::writeShortLE, this::writeEntry);
    }

    @Override
    public void deserialize(ByteBuf buffer, BedrockPacketHelper helper, ResourcePacksInfoPacket packet) {
        packet.setForcedToAccept(buffer.readBoolean());
        helper.readArray(buffer, packet.getBehaviorPackInfos(), ByteBuf::readUnsignedShortLE, this::readEntry);
        helper.readArray(buffer, packet.getResourcePackInfos(), ByteBuf::readUnsignedShortLE, this::readEntry);
    }

    public ResourcePacksInfoPacket.Entry readEntry(ByteBuf buffer, BedrockPacketHelper helper) {
        return new ResourcePacksInfoPacket.Entry(
                helper.readString(buffer),
                helper.readString(buffer),
                buffer.readLongLE()
        );
    }

    public void writeEntry(ByteBuf buffer, BedrockPacketHelper helper, ResourcePacksInfoPacket.Entry entry) {
        requireNonNull(entry, "ResourcePacketInfoPacket entry was null");

        helper.writeString(buffer, entry.getPackId());
        helper.writeString(buffer, entry.getPackVersion());
        buffer.writeLongLE(entry.getPackSize());
    }
}
