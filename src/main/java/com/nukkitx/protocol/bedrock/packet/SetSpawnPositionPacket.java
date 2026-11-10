package com.nukkitx.protocol.bedrock.packet;

import com.nukkitx.math.vector.Vector3i;
import com.nukkitx.protocol.bedrock.BedrockPacket;
import com.nukkitx.protocol.bedrock.BedrockPacketType;
import com.nukkitx.protocol.bedrock.handler.BedrockPacketHandler;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(doNotUseGetters = true, callSuper = false)
public class SetSpawnPositionPacket extends BedrockPacket {
    // spawnType is the type of spawn to set. It is either PLAYER_SPAWN or WORLD_SPAWN, and specifies
    // the behaviour of the spawn set. If WORLD_SPAWN is set, the position to which compasses will point is
    // also changed.
    private Type spawnType;

    // blockPosition is the new position of the spawn that was set. If spawnType is WORLD_SPAWN, compasses will
    // point to this position. As of 1.16, blockPosition is always the position of the player.
    private Vector3i blockPosition;

    private boolean spawnForced;

    @Override
    public final boolean handle(BedrockPacketHandler handler) {
        return handler.handle(this);
    }

    public BedrockPacketType getPacketType() {
        return BedrockPacketType.SET_SPAWN_POSITION;
    }

    public enum Type {
        PLAYER_SPAWN,
        WORLD_SPAWN
    }
}
