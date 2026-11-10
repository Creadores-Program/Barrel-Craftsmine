package com.nukkitx.protocol.bedrock.packet;

import com.nukkitx.math.vector.Vector3f;
import com.nukkitx.protocol.bedrock.BedrockPacket;
import com.nukkitx.protocol.bedrock.BedrockPacketType;
import com.nukkitx.protocol.bedrock.handler.BedrockPacketHandler;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(doNotUseGetters = true, callSuper = false)
public class MovePlayerPacket extends BedrockPacket {
    private long runtimeEntityId;

    private Vector3f position;
    private Vector3f rotation;

    private Animation animation;

    private boolean onGround;

    private long ridingRuntimeEntityId;

    private int unknownInt1;
    private int unknownInt2;

    @Override
    public final boolean handle(BedrockPacketHandler handler) {
        return handler.handle(this);
    }

    public BedrockPacketType getPacketType() {
        return BedrockPacketType.MOVE_PLAYER;
    }

    public enum Animation {
        NORMAL,
        RESPAWN,
        TELEPORT,
        HEAD_ROTATION
    }
}
