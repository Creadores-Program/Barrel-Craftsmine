package com.nukkitx.protocol.bedrock.data.event;

import lombok.Value;

@Value
public class CauldronUsedEventData implements EventData {
    int color;

    int potionId;

    int fillLevel;

    @Override
    public EventDataType getType() {
        return EventDataType.CAULDRON_USED;
    }
}
