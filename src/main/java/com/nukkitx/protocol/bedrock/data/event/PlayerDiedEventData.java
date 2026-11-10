package com.nukkitx.protocol.bedrock.data.event;

import com.nukkitx.protocol.bedrock.data.entity.EntityDamageCause;
import lombok.Value;

@Value
public class PlayerDiedEventData implements EventData {
    int attackerEntityId;
    int attackerVariant;

    EntityDamageCause damageCause;

    @Override
    public EventDataType getType() {
        return EventDataType.PLAYER_DIED;
    }
}