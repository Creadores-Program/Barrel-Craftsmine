package com.nukkitx.protocol.bedrock.packet;

import com.nukkitx.math.vector.Vector2f;
import com.nukkitx.math.vector.Vector3f;
import com.nukkitx.math.vector.Vector3i;
import com.nukkitx.protocol.bedrock.BedrockPacket;
import com.nukkitx.protocol.bedrock.BedrockPacketType;
import com.nukkitx.protocol.bedrock.data.*;
import com.nukkitx.protocol.bedrock.handler.BedrockPacketHandler;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.*;

import java.util.List;

@Data
@EqualsAndHashCode(doNotUseGetters = true, callSuper = false)
public class StartGamePacket extends BedrockPacket {
    private static final InternalLogger log = InternalLoggerFactory.getInstance(StartGamePacket.class);

    /**
     * Player's entity id that uniquely identifies the entity of the server.
     */
    private long uniqueEntityId;
    private long runtimeEntityId;

    /**
     * Player's gamemode that may differ from the world's gamemode
     */
    private GameType playerGameType;

    private Vector3f playerPosition;
    private Vector2f playerRotation;

    private long seed;

    private DimensionType dimension;
    private GeneratorType generator;

    private GameType worldGameType;

    private DifficultyType difficulty;

    private Vector3i spawnPosition;

    private boolean achievementsDisabled;

    private int dayCycleStopTime;

    private boolean eduMode;

    private float rainLevel;
    private float lightningLevel;

    private boolean commandsEnabled;

    private boolean texturePacksRequired;

    private final List<GameRuleData<?>> gamerules = new ObjectArrayList<>();

    private String levelId;
    private String worldName;
    private String premiumWorldTemplate;

    private boolean trial;

    private long currentTick;

    @Override
    public final boolean handle(BedrockPacketHandler handler) {
        return handler.handle(this);
    }

    public BedrockPacketType getPacketType() {
        return BedrockPacketType.START_GAME;
    }
}
