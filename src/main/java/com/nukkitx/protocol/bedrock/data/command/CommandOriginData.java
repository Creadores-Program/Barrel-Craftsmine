package com.nukkitx.protocol.bedrock.data.command;

import lombok.Value;

@Value
public class CommandOriginData {
    int unknownVarUInt1;
    int unknownVarUInt2;

    long playerUniqueId;
}
