package com.nukkitx.protocol.bedrock.data.event;

import com.nukkitx.protocol.bedrock.data.entity.EntityDamageCause;
import lombok.Value;

@Value
public class MobKilledEventData implements EventData {
    long killerUniqueEntityId;
    long victimUniqueEntityId;

    EntityDamageCause damageCause;

    @Override
    public EventDataType getType() {
        return EventDataType.MOB_KILLED;
    }
}
