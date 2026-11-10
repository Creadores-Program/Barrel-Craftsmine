package com.nukkitx.protocol.bedrock.v113.serializer;

import com.nukkitx.network.VarInts;
import com.nukkitx.network.util.Preconditions;
import com.nukkitx.protocol.bedrock.BedrockPacketHelper;
import com.nukkitx.protocol.bedrock.BedrockPacketSerializer;
import com.nukkitx.protocol.bedrock.data.DimensionType;
import com.nukkitx.protocol.bedrock.data.entity.EntityDamageCause;
import com.nukkitx.protocol.bedrock.data.event.*;
import com.nukkitx.protocol.bedrock.packet.EventPacket;
import com.nukkitx.protocol.bedrock.util.TriConsumer;
import io.netty.buffer.ByteBuf;

import java.util.EnumMap;
import java.util.function.BiFunction;

import static com.nukkitx.protocol.bedrock.data.event.EventDataType.*;

public class EventSerializer_v113 implements BedrockPacketSerializer<EventPacket> {
    public static final EventSerializer_v113 INSTANCE = new EventSerializer_v113();

    protected static final EventDataType[] VALUES = EventDataType.values();

    protected final EnumMap<EventDataType, BiFunction<ByteBuf, BedrockPacketHelper, EventData>> readers = new EnumMap<>(EventDataType.class);
    protected final EnumMap<EventDataType, TriConsumer<ByteBuf, BedrockPacketHelper, EventData>> writers = new EnumMap<>(EventDataType.class);

    protected EventSerializer_v113() {
        this.readers.put(ACHIEVEMENT_AWARDED, this::readAchievementAwarded);
        this.readers.put(ENTITY_INTERACT, this::readEntityInteract);
        this.readers.put(PORTAL_BUILT, this::readPortalBuilt);
        this.readers.put(PORTAL_USED, this::readPortalUsed);
        this.readers.put(MOB_KILLED, this::readMobKilled);
        this.readers.put(CAULDRON_USED, this::readCauldronUsed);
        this.readers.put(PLAYER_DIED, this::readPlayerDied);
        this.readers.put(BOSS_KILLED, this::readBossKilled);
        this.readers.put(AGENT_COMMAND, this::readAgentCommand);
        this.readers.put(AGENT_CREATED, (b, h) -> AgentCreatedEventData.INSTANCE);

        this.writers.put(ACHIEVEMENT_AWARDED, this::writeAchievementAwarded);
        this.writers.put(ENTITY_INTERACT, this::writeEntityInteract);
        this.writers.put(PORTAL_BUILT, this::writePortalBuilt);
        this.writers.put(PORTAL_USED, this::writePortalUsed);
        this.writers.put(MOB_KILLED, this::writeMobKilled);
        this.writers.put(CAULDRON_USED, this::writeCauldronUsed);
        this.writers.put(PLAYER_DIED, this::writePlayerDied);
        this.writers.put(BOSS_KILLED, this::writeBossKilled);
        this.writers.put(AGENT_COMMAND, this::writeAgentCommand);
        this.writers.put(AGENT_CREATED, (b, h, e) -> {
        });
    }

    @Override
    public void serialize(ByteBuf buffer, BedrockPacketHelper helper, EventPacket packet) {
        VarInts.writeLong(buffer, packet.getUniqueEntityId());
        EventData eventData = packet.getEventData();
        VarInts.writeInt(buffer, eventData.getType().ordinal());
        buffer.writeByte(packet.getUsePlayerId());

        TriConsumer<ByteBuf, BedrockPacketHelper, EventData> function = this.writers.get(eventData.getType());

        if (function == null) {
            throw new UnsupportedOperationException("Unknown event type " + eventData.getType());
        }

        function.accept(buffer, helper, eventData);
    }

    @Override
    public void deserialize(ByteBuf buffer, BedrockPacketHelper helper, EventPacket packet) {
        packet.setUniqueEntityId(VarInts.readLong(buffer));

        int eventId = VarInts.readInt(buffer);
        Preconditions.checkElementIndex(eventId, VALUES.length, "EventDataType");
        EventDataType type = VALUES[eventId];

        packet.setUsePlayerId(buffer.readByte());

        BiFunction<ByteBuf, BedrockPacketHelper, EventData> function = this.readers.get(type);

        if (function == null) {
            throw new UnsupportedOperationException("Unknown event type " + type);
        }

        packet.setEventData(function.apply(buffer, helper));
    }

    protected AchievementAwardedEventData readAchievementAwarded(ByteBuf buffer, BedrockPacketHelper helper) {
        int achievementId = VarInts.readInt(buffer);
        return new AchievementAwardedEventData(achievementId);
    }

    protected void writeAchievementAwarded(ByteBuf buffer, BedrockPacketHelper helper, EventData eventData) {
        AchievementAwardedEventData event = (AchievementAwardedEventData) eventData;
        VarInts.writeInt(buffer, event.getAchievementId());
    }

    protected EntityInteractEventData readEntityInteract(ByteBuf buffer, BedrockPacketHelper helper) {
        int interactionType = VarInts.readInt(buffer);
        int entityColor = buffer.readUnsignedByte();
        return new EntityInteractEventData(interactionType, entityColor);
    }

    protected void writeEntityInteract(ByteBuf buffer, BedrockPacketHelper helper, EventData eventData) {
        EntityInteractEventData event = (EntityInteractEventData) eventData;
        VarInts.writeInt(buffer, event.getInteractionType());
        buffer.writeByte(event.getEntityColor());
    }

    protected PortalBuiltEventData readPortalBuilt(ByteBuf buffer, BedrockPacketHelper helper) {
        return new PortalBuiltEventData(DimensionType.from(VarInts.readInt(buffer)));
    }

    protected void writePortalBuilt(ByteBuf buffer, BedrockPacketHelper helper, EventData eventData) {
        PortalBuiltEventData event = (PortalBuiltEventData) eventData;
        VarInts.writeInt(buffer, event.getWhere().ordinal());
    }

    protected PortalUsedEventData readPortalUsed(ByteBuf buffer, BedrockPacketHelper helper) {
        int from = VarInts.readInt(buffer);
        int to = VarInts.readInt(buffer);
        return new PortalUsedEventData(DimensionType.from(from), DimensionType.from(to));
    }

    protected void writePortalUsed(ByteBuf buffer, BedrockPacketHelper helper, EventData eventData) {
        PortalUsedEventData event = (PortalUsedEventData) eventData;
        VarInts.writeInt(buffer, event.getFrom().ordinal());
        VarInts.writeInt(buffer, event.getTo().ordinal());
    }

    protected MobKilledEventData readMobKilled(ByteBuf buffer, BedrockPacketHelper helper) {
        long killerUniqueEntityId = VarInts.readLong(buffer);
        long victimUniqueEntityId = VarInts.readLong(buffer);
        EntityDamageCause damageCause = EntityDamageCause.from(VarInts.readInt(buffer));
        return new MobKilledEventData(killerUniqueEntityId, victimUniqueEntityId, damageCause);
    }

    protected void writeMobKilled(ByteBuf buffer, BedrockPacketHelper helper, EventData eventData) {
        MobKilledEventData event = (MobKilledEventData) eventData;
        VarInts.writeLong(buffer, event.getKillerUniqueEntityId());
        VarInts.writeLong(buffer, event.getVictimUniqueEntityId());
        VarInts.writeInt(buffer, event.getDamageCause().ordinal());
    }

    protected CauldronUsedEventData readCauldronUsed(ByteBuf buffer, BedrockPacketHelper helper) {
        int color = VarInts.readInt(buffer);
        int potionId = VarInts.readUnsignedInt(buffer);
        int fillLevel = VarInts.readInt(buffer);
        return new CauldronUsedEventData(color, potionId, fillLevel);
    }

    protected void writeCauldronUsed(ByteBuf buffer, BedrockPacketHelper helper, EventData eventData) {
        CauldronUsedEventData event = (CauldronUsedEventData) eventData;
        VarInts.writeInt(buffer, event.getColor());
        VarInts.writeUnsignedInt(buffer, event.getPotionId());
        VarInts.writeInt(buffer, event.getFillLevel());
    }

    protected PlayerDiedEventData readPlayerDied(ByteBuf buffer, BedrockPacketHelper helper) {
        int attackerEntityId = VarInts.readInt(buffer);
        EntityDamageCause damageCause = EntityDamageCause.from(VarInts.readInt(buffer));
        return new PlayerDiedEventData(attackerEntityId, -1, damageCause);
    }

    protected void writePlayerDied(ByteBuf buffer, BedrockPacketHelper helper, EventData eventData) {
        PlayerDiedEventData event = (PlayerDiedEventData) eventData;
        VarInts.writeInt(buffer, event.getAttackerEntityId());
        VarInts.writeInt(buffer, event.getDamageCause().ordinal());
    }

    protected BossKilledEventData readBossKilled(ByteBuf buffer, BedrockPacketHelper helper) {
        long bossUniqueEntityId = VarInts.readLong(buffer);
        int playerPartySize = VarInts.readInt(buffer);
        int interactionEntityType = VarInts.readInt(buffer);
        return new BossKilledEventData(bossUniqueEntityId, playerPartySize, interactionEntityType);
    }

    protected void writeBossKilled(ByteBuf buffer, BedrockPacketHelper helper, EventData eventData) {
        BossKilledEventData event = (BossKilledEventData) eventData;
        VarInts.writeLong(buffer, event.getBossUniqueEntityId());
        VarInts.writeInt(buffer, event.getPlayerPartySize());
        VarInts.writeInt(buffer, event.getBossEntityType());
    }

    protected AgentCommandEventData readAgentCommand(ByteBuf buffer, BedrockPacketHelper helper) {
        int agentResult = VarInts.readInt(buffer);
        int dataValue = VarInts.readInt(buffer);
        String command = helper.readString(buffer);
        String dataKey = helper.readString(buffer);
        String output = helper.readString(buffer);
        return new AgentCommandEventData(agentResult, command, dataKey, dataValue, output);
    }

    protected void writeAgentCommand(ByteBuf buffer, BedrockPacketHelper helper, EventData eventData) {
        AgentCommandEventData event = (AgentCommandEventData) eventData;
        VarInts.writeInt(buffer, event.getAgentResult());
        VarInts.writeInt(buffer, event.getDataValue());
        helper.writeString(buffer, event.getCommand());
        helper.writeString(buffer, event.getDataKey());
        helper.writeString(buffer, event.getOutput());
    }
}
