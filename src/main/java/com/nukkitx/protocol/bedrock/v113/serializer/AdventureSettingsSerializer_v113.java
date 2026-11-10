package com.nukkitx.protocol.bedrock.v113.serializer;

import com.nukkitx.network.VarInts;
import com.nukkitx.protocol.bedrock.BedrockPacketHelper;
import com.nukkitx.protocol.bedrock.BedrockPacketSerializer;
import com.nukkitx.protocol.bedrock.data.AdventureSetting;
import com.nukkitx.protocol.bedrock.data.command.CommandPermission;
import com.nukkitx.protocol.bedrock.packet.AdventureSettingsPacket;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Set;

import static com.nukkitx.protocol.bedrock.data.AdventureSetting.*;

@SuppressWarnings("PointlessBitwiseExpression")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AdventureSettingsSerializer_v113 implements BedrockPacketSerializer<AdventureSettingsPacket> {
    public static final AdventureSettingsSerializer_v113 INSTANCE = new AdventureSettingsSerializer_v113();

    private static final CommandPermission[] COMMAND_PERMISSIONS = CommandPermission.values();
    private static final AdventureSetting[] ADVENTURE_SETTINGS = AdventureSetting.values();

    private static final Object2IntMap<AdventureSetting> FLAGS_TO_BIT = new Object2IntOpenHashMap<>();

    static {
        FLAGS_TO_BIT.put(WORLD_IMMUTABLE, (1 << 0));
        FLAGS_TO_BIT.put(NO_PVP, (1 << 1));
        FLAGS_TO_BIT.put(NO_PVM, (1 << 2));
        FLAGS_TO_BIT.put(NO_MVP, (1 << 3));
        FLAGS_TO_BIT.put(NO_EVP, (1 << 4));
        FLAGS_TO_BIT.put(AUTO_JUMP, (1 << 5));
        FLAGS_TO_BIT.put(ALLOW_FLIGHT, (1 << 6));
        FLAGS_TO_BIT.put(NO_CLIP, (1 << 7));
        FLAGS_TO_BIT.put(FLYING, (1 << 8));
        FLAGS_TO_BIT.put(MUTED, (1 << 9));
    }


    @Override
    public void serialize(ByteBuf buffer, BedrockPacketHelper helper, AdventureSettingsPacket packet) {
        int flags = 0;
        for (AdventureSetting setting : packet.getSettings()) {
            flags |= FLAGS_TO_BIT.getInt(setting);
        }
        VarInts.writeUnsignedInt(buffer, flags);
        VarInts.writeUnsignedInt(buffer, packet.getCommandPermission().ordinal());
    }

    @Override
    public void deserialize(ByteBuf buffer, BedrockPacketHelper helper, AdventureSettingsPacket packet) {
        int flags = VarInts.readUnsignedInt(buffer);

        packet.setCommandPermission(COMMAND_PERMISSIONS[VarInts.readUnsignedInt(buffer)]);

        readFlags(flags, packet.getSettings());
    }

    protected static void readFlags(int flags, Set<AdventureSetting> settings) {
        for (int i = 0; i < ADVENTURE_SETTINGS.length; i++) {
            if ((flags & (1 << i)) != 0) {
                settings.add(ADVENTURE_SETTINGS[i]);
            }
        }
    }
}
