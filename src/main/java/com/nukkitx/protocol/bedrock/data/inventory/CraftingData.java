package com.nukkitx.protocol.bedrock.data.inventory;

import com.nukkitx.protocol.bedrock.data.inventory.descriptor.ItemDescriptorWithCount;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.RequiredArgsConstructor;
import lombok.Value;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Value
@RequiredArgsConstructor
public class CraftingData {
    CraftingDataType type;

    int width;
    int height;

    int inputId;
    int inputDamage;

    List<ItemDescriptorWithCount> inputDescriptors;
    List<ItemData> outputs;

    UUID uuid;

    public static CraftingData fromFurnaceData(int inputId, int inputDamage, ItemData output) {
        return new CraftingData(CraftingDataType.FURNACE_DATA, -1, -1, inputId, inputDamage,
                null, new ObjectArrayList<>(Collections.singleton(output)), null);
    }

    public static CraftingData fromFurnace(int inputId, ItemData input) {
        return new CraftingData(CraftingDataType.FURNACE, -1, -1, inputId, -1,
                null, new ObjectArrayList<>(Collections.singleton(input)), null);
    }

    public static CraftingData fromShapeless(List<ItemDescriptorWithCount> inputs, List<ItemData> outputs, UUID uuid) {
        return new CraftingData(CraftingDataType.SHAPELESS, -1, -1, -1, -1, inputs, outputs, uuid);
    }

    public static CraftingData fromShaped(int width, int height, List<ItemDescriptorWithCount> inputs,
                                          List<ItemData> outputs, UUID uuid) {
        return new CraftingData(CraftingDataType.SHAPED, width, height, -1, -1, inputs, outputs, uuid);
    }

    public static CraftingData fromShulkerBox(List<ItemDescriptorWithCount> inputs, List<ItemData> outputs, UUID uuid) {
        return new CraftingData(CraftingDataType.SHULKER_BOX, -1, -1, -1, -1, inputs, outputs, uuid);
    }

    public static CraftingData fromMulti(UUID uuid) {
        return new CraftingData(CraftingDataType.MULTI, -1, -1, -1, -1, null, null, uuid);
    }

    public List<ItemData> getInputs() {
        return inputDescriptors.stream()
                .map(ItemDescriptorWithCount::toItem)
                .collect(Collectors.toList());
    }
}
