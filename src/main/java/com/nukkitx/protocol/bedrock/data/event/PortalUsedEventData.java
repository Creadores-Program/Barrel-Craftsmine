package com.nukkitx.protocol.bedrock.data.event;

import com.nukkitx.protocol.bedrock.data.DimensionType;
import lombok.Value;

@Value
public class PortalUsedEventData implements EventData {
    DimensionType from;
    DimensionType to;

    @Override
    public EventDataType getType() {
        return EventDataType.PORTAL_USED;
    }
}
