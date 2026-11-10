package com.nukkitx.protocol.bedrock.packet;

import com.nukkitx.math.vector.Vector3f;
import com.nukkitx.protocol.bedrock.BedrockPacket;
import com.nukkitx.protocol.bedrock.BedrockPacketType;
import com.nukkitx.protocol.bedrock.data.entity.EntityDataMap;
import com.nukkitx.protocol.bedrock.data.inventory.ItemData;
import com.nukkitx.protocol.bedrock.handler.BedrockPacketHandler;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.UUID;

@Data
@EqualsAndHashCode(doNotUseGetters = true, callSuper = false)
public class AddPlayerPacket extends BedrockPacket {
    private UUID uuid;
    private String username;

    private long uniqueEntityId;
    private long runtimeEntityId;

    private Vector3f position;
    private Vector3f motion;
    private Vector3f rotation;

    private ItemData hand;

    private final EntityDataMap metadata = new EntityDataMap();

    @Override
    public final boolean handle(BedrockPacketHandler handler) {
        return handler.handle(this);
    }

    public BedrockPacketType getPacketType() {
        return BedrockPacketType.ADD_PLAYER;
    }
}
