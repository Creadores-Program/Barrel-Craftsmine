package org.CreadoresProgram.CraftsMine.utils;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.Getter;
import com.nukkitx.nbt.NbtMap;

import java.util.List;

/**
 * Loads the block_palette.nbt bundled with the proxy and exposes a
 * runtime-id -> {@link NbtBlockDefinition} lookup.
 *
 * <p>This class previously implemented the CloudburstMC
 * {@code DefinitionRegistry<BlockDefinition>} interface and the inner
 * {@link NbtBlockDefinition} implemented {@code BlockDefinition}. Neither
 * of those interfaces exists in the vendored NukkitX v113 protocol tree,
 * so the class is now a plain utility without any framework interface.
 * Callers ({@link org.CreadoresProgram.CraftsMine.network.translator.bedrock.UpdateBlock}
 * and StartGame translators) consume the NBT definition directly via
 * {@link #getDefinition(int)} which returns the inner
 * {@link NbtBlockDefinition} holding the runtime id and the original
 * {@link NbtMap} tag.</p>
 */
public class NbtBlockDefinitionRegistry {

    private final Int2ObjectMap<NbtBlockDefinition> definitions = new Int2ObjectOpenHashMap<>();

    public NbtBlockDefinitionRegistry(List<NbtMap> definitions) {
        int counter = 0;
        for (NbtMap definition : definitions) {
            int runtimeId = counter++;
            this.definitions.put(runtimeId, new NbtBlockDefinition(runtimeId, definition));
        }
    }

    public NbtBlockDefinition getDefinition(int runtimeId) {
        return definitions.get(runtimeId);
    }

    public boolean isRegistered(NbtBlockDefinition definition) {
        return definitions.get(definition.getRuntimeId()) == definition;
    }

    /**
     * Plain holder for a block's runtime id and its source NBT definition.
     * The CloudburstMC {@code BlockDefinition} interface has been removed.
     */
    public static class NbtBlockDefinition {
        @Getter
        private final int runtimeId;
        @Getter
        private final NbtMap definition;

        public NbtBlockDefinition(int runtimeId, NbtMap definition) {
            this.runtimeId = runtimeId;
            this.definition = definition;
        }
    }
}
