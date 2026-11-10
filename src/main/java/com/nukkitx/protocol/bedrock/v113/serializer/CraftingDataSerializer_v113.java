package com.nukkitx.protocol.bedrock.v113.serializer;

import com.nukkitx.network.VarInts;
import com.nukkitx.protocol.bedrock.BedrockPacketHelper;
import com.nukkitx.protocol.bedrock.BedrockPacketSerializer;
import com.nukkitx.protocol.bedrock.BedrockSession;
import com.nukkitx.protocol.bedrock.data.inventory.CraftingData;
import com.nukkitx.protocol.bedrock.data.inventory.CraftingDataType;
import com.nukkitx.protocol.bedrock.data.inventory.ItemData;
import com.nukkitx.protocol.bedrock.data.inventory.descriptor.ItemDescriptorWithCount;
import com.nukkitx.protocol.bedrock.packet.CraftingDataPacket;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CraftingDataSerializer_v113 implements BedrockPacketSerializer<CraftingDataPacket> {
    public static final CraftingDataSerializer_v113 INSTANCE = new CraftingDataSerializer_v113();

    @Override
    public void serialize(ByteBuf buffer, BedrockPacketHelper helper, CraftingDataPacket packet, BedrockSession session) {
        helper.writeArray(buffer, packet.getCraftingData(), (buf, craftingData) -> {
            VarInts.writeInt(buf, craftingData.getType().ordinal());
            switch (craftingData.getType()) {
                case SHAPELESS:
                case SHULKER_BOX:
                    this.writeShapelessRecipe(buf, helper, craftingData, session);
                    break;
                case SHAPED:
                    this.writeShapedRecipe(buf, helper, craftingData, session);
                    break;
                case FURNACE:
                    this.writeFurnaceRecipe(buf, helper, craftingData, session);
                case FURNACE_DATA:
                    this.writeFurnaceDataRecipe(buf, helper, craftingData, session);
                    break;
                case MULTI:
                    this.writeMultiRecipe(buf, helper, craftingData);
                    break;
            }
        });
        buffer.writeBoolean(packet.isCleanRecipes());
    }

    @Override
    public void deserialize(ByteBuf buffer, BedrockPacketHelper helper, CraftingDataPacket packet, BedrockSession session) {
        helper.readArray(buffer, packet.getCraftingData(), buf -> {
            int typeInt = VarInts.readInt(buf);
            CraftingDataType type = CraftingDataType.byId(typeInt);

            switch (type) {
                case SHAPELESS:
                case SHULKER_BOX:
                    return this.readShapelessRecipe(buf, helper, session);
                case SHAPED:
                    return this.readShapedRecipe(buf, helper, session);
                case FURNACE:
                    return this.readFurnaceRecipe(buf, helper, session);
                case FURNACE_DATA:
                    return this.readFurnaceDataRecipe(buf, helper, session);
                case MULTI:
                    return this.readMultiRecipe(buf, helper);
                default:
                    throw new IllegalArgumentException("Unhandled crafting data type: " + type);
            }
        });
        packet.setCleanRecipes(buffer.readBoolean());
    }

    protected CraftingData readShapelessRecipe(ByteBuf buffer, BedrockPacketHelper helper, BedrockSession session) {
        List<ItemDescriptorWithCount> inputs = new ObjectArrayList<>();
        helper.readArray(buffer, inputs, buf -> ItemDescriptorWithCount.fromItem(helper.readItem(buf, session)));

        List<ItemData> outputs = new ObjectArrayList<>();
        helper.readArray(buffer, outputs, buf -> helper.readItem(buf, session));

        UUID uuid = helper.readUuid(buffer);
        return CraftingData.fromShapeless(inputs, outputs, uuid);
    }

    protected void writeShapelessRecipe(ByteBuf buffer, BedrockPacketHelper helper, CraftingData data, BedrockSession session) {
        helper.writeArray(buffer, data.getInputs(), (buf, item) -> helper.writeItem(buf, item, session));
        helper.writeArray(buffer, data.getOutputs(), (buf, item) -> helper.writeItem(buf, item, session));
        helper.writeUuid(buffer, data.getUuid());
    }

    protected CraftingData readShapedRecipe(ByteBuf buffer, BedrockPacketHelper helper, BedrockSession session) {
        int width = VarInts.readInt(buffer);
        int height = VarInts.readInt(buffer);
        int inputCount = width * height;
        List<ItemDescriptorWithCount> inputs = new ObjectArrayList<>(inputCount);
        for (int i = 0; i < inputCount; i++) {
            inputs.add(ItemDescriptorWithCount.fromItem(helper.readItem(buffer, session)));
        }
        List<ItemData> outputs = new ObjectArrayList<>();
        helper.readArray(buffer, outputs, buf -> helper.readItem(buf, session));
        UUID uuid = helper.readUuid(buffer);
        return CraftingData.fromShaped(width, height, inputs, outputs, uuid);
    }

    protected void writeShapedRecipe(ByteBuf buffer, BedrockPacketHelper helper, CraftingData data, BedrockSession session) {
        VarInts.writeInt(buffer, data.getWidth());
        VarInts.writeInt(buffer, data.getHeight());
        int count = data.getWidth() * data.getHeight();
        List<ItemData> inputs = data.getInputs();
        for (int i = 0; i < count; i++) {
            helper.writeItem(buffer, inputs.get(i), session);
        }
        helper.writeArray(buffer, data.getOutputs(), (buf, item) -> helper.writeItem(buf, item, session));
        helper.writeUuid(buffer, data.getUuid());
    }

    protected CraftingData readFurnaceRecipe(ByteBuf buffer, BedrockPacketHelper helper, BedrockSession session) {
        int inputId = VarInts.readInt(buffer);
        ItemData output = helper.readItem(buffer, session);
        return CraftingData.fromFurnace(inputId, output);
    }

    protected CraftingData readFurnaceDataRecipe(ByteBuf buffer, BedrockPacketHelper helper, BedrockSession session) {
        int inputId = VarInts.readInt(buffer);
        int inputDamage = VarInts.readInt(buffer);
        ItemData output = helper.readItem(buffer, session);
        return CraftingData.fromFurnaceData(inputId, inputDamage, output);
    }

    protected void writeFurnaceRecipe(ByteBuf buffer, BedrockPacketHelper helper, CraftingData data, BedrockSession session) {
        VarInts.writeInt(buffer, data.getInputId());
        helper.writeItem(buffer, data.getOutputs().get(0), session);
    }

    protected void writeFurnaceDataRecipe(ByteBuf buffer, BedrockPacketHelper helper, CraftingData data, BedrockSession session) {
        VarInts.writeInt(buffer, data.getInputId());
        VarInts.writeInt(buffer, data.getInputDamage());
        helper.writeItem(buffer, data.getOutputs().get(0), session);
    }

    protected CraftingData readMultiRecipe(ByteBuf buffer, BedrockPacketHelper helper) {
        UUID uuid = helper.readUuid(buffer);
        return CraftingData.fromMulti(uuid);
    }

    protected void writeMultiRecipe(ByteBuf buffer, BedrockPacketHelper helper, CraftingData data) {
        helper.writeUuid(buffer, data.getUuid());
    }
}
