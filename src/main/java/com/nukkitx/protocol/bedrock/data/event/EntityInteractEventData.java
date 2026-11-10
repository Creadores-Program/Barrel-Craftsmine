package com.nukkitx.protocol.bedrock.data.event;

import lombok.Value;

@Value
public class EntityInteractEventData implements EventData {
    int interactionType;

    int entityColor;

    @Override
    public EventDataType getType() {
        return EventDataType.ENTITY_INTERACT;
    }
}
