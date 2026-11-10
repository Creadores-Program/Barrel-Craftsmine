package com.nukkitx.protocol.bedrock.data.event;

import com.nukkitx.protocol.bedrock.data.DimensionType;
import lombok.Value;

@Value
public class PortalBuiltEventData implements EventData {
    DimensionType where;

    @Override
    public EventDataType getType() {
        return EventDataType.PORTAL_BUILT;
    }
}
