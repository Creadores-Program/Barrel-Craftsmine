package com.nukkitx.protocol.bedrock.packet;

import com.nukkitx.nbt.NbtMap;
import com.nukkitx.protocol.bedrock.BedrockPacket;
import com.nukkitx.protocol.bedrock.BedrockPacketType;
import com.nukkitx.protocol.bedrock.data.inventory.ContainerType;
import com.nukkitx.protocol.bedrock.handler.BedrockPacketHandler;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(doNotUseGetters = true, callSuper = false)
public class UpdateTradePacket extends BedrockPacket {
    private int containerId;
    private ContainerType containerType;

    private int size; // Hardcoded to 0

    private boolean usingEconomyTrade;

    private boolean isWilling;

    private long traderUniqueEntityId;
    private long playerUniqueEntityId;

    private String displayName;

    private NbtMap offers;

    @Override
    public final boolean handle(BedrockPacketHandler handler) {
        return handler.handle(this);
    }

    public BedrockPacketType getPacketType() {
        return BedrockPacketType.UPDATE_TRADE;
    }
}
