package org.CreadoresProgram.CraftsMine.utils;

import com.nukkitx.nbt.NBTInputStream;
import com.nukkitx.nbt.NbtMap;
import com.nukkitx.nbt.NbtUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.Unpooled;

import java.util.HashMap;
import java.util.Map;

/**
 * Converts a Bedrock 1.2+ chunk blob (subchunk format with runtime palettes,
 * chunk version byte = 1) into the legacy 0.15.x column-ordered format that
 * MCPE 0.15.x clients expect.
 *
 * <p>The legacy format is a flat byte array of 83200 bytes + blockEntities:</p>
 * <ul>
 *   <li>orderedIds: 32768 bytes (16*16*128, one byte per block)</li>
 *   <li>orderedData: 16384 bytes (nibbles, 2 per byte)</li>
 *   <li>orderedSkyLight: 16384 bytes</li>
 *   <li>orderedBlockLight: 16384 bytes</li>
 *   <li>orderedHeightMap: 256 bytes</li>
 *   <li>orderedBiomeColors: 1024 bytes (256 ints * 4, big-endian RGBA)</li>
 *   <li>blockEntities NBT (forwarded verbatim)</li>
 * </ul>
 *
 * <p>The subchunk format (chunk version 1) is:</p>
 * <pre>
 * byte: chunkVersion = 1
 * VarInt: subchunkCount
 * for each subchunk:
 *   byte: subchunkVersion
 *   if subchunkVersion &lt; 8:
 *     byte: paletteCount (1 or 2)
 *     for each palette:
 *       byte: flags (bitsPerBlock = flags >> 1, isPersistent = flags & 1)
 *       if isPersistent: VarInt blockCount
 *       blockData: (4096 * bitsPerBlock) / 8 bytes (packed indices)
 *       VarInt: paletteSize
 *       for each entry: NbtMap blockState
 *   else (subchunkVersion == 8):
 *     byte: storageCount
 *     for each storage: (same layout as palette above)
 * heightmap: 256 * 2 bytes (little-endian shorts)
 * biome data:
 *   byte: biomeVersion
 *   if biomeVersion == 0: 256 bytes biome IDs
 *   if biomeVersion >= 1: palette-based biomes
 * (optional border blocks, block entities NBT)
 * </pre>
 *
 * <p>The conversion extracts block IDs from the FIRST storage of each subchunk
 * (layer 0 = solid blocks; layer 1 = waterlogged, which 0.15.x doesn't support).
 * Block states (NbtMap with name/states/version) are mapped to numeric 0.15.x
 * IDs via a name-based lookup table. States not in the table fall back to
 * stone (1) so the world is visible even if some blocks are wrong.</p>
 */
public class ChunkConverter {

    /** Legacy blob size without block entities. */
    public static final int LEGACY_BLOB_SIZE = 83200;
    /** Chunk height in blocks (0.15.x and v113 both use 128). */
    public static final int CHUNK_HEIGHT = 128;
    public static final int SUBCHUNK_SIZE = 16 * 16 * 16; // 4096

    /** Maps minecraft block name → 0.15.x numeric ID. */
    private static final Map<String, Integer> NAME_TO_ID = new HashMap<>();
    static {
        // Common blocks. 0.15.x uses numeric IDs 0..255.
        NAME_TO_ID.put("minecraft:air", 0);
        NAME_TO_ID.put("minecraft:stone", 1);
        NAME_TO_ID.put("minecraft:granite", 1);      // variant, fallback to stone
        NAME_TO_ID.put("minecraft:diorite", 1);
        NAME_TO_ID.put("minecraft:andesite", 1);
        NAME_TO_ID.put("minecraft:grass", 2);
        NAME_TO_ID.put("minecraft:grass_block", 2);
        NAME_TO_ID.put("minecraft:dirt", 3);
        NAME_TO_ID.put("minecraft:coarse_dirt", 3);
        NAME_TO_ID.put("minecraft:cobblestone", 4);
        NAME_TO_ID.put("minecraft:planks", 5);
        NAME_TO_ID.put("minecraft:oak_planks", 5);
        NAME_TO_ID.put("minecraft:spruce_planks", 5);
        NAME_TO_ID.put("minecraft:birch_planks", 5);
        NAME_TO_ID.put("minecraft:jungle_planks", 5);
        NAME_TO_ID.put("minecraft:acacia_planks", 5);
        NAME_TO_ID.put("minecraft:dark_oak_planks", 5);
        NAME_TO_ID.put("minecraft:sapling", 6);
        NAME_TO_ID.put("minecraft:bedrock", 7);
        NAME_TO_ID.put("minecraft:flowing_water", 8);
        NAME_TO_ID.put("minecraft:water", 8);
        NAME_TO_ID.put("minecraft:flowing_lava", 10);
        NAME_TO_ID.put("minecraft:lava", 10);
        NAME_TO_ID.put("minecraft:sand", 12);
        NAME_TO_ID.put("minecraft:red_sand", 12);
        NAME_TO_ID.put("minecraft:gravel", 13);
        NAME_TO_ID.put("minecraft:gold_ore", 14);
        NAME_TO_ID.put("minecraft:iron_ore", 15);
        NAME_TO_ID.put("minecraft:coal_ore", 16);
        NAME_TO_ID.put("minecraft:log", 17);
        NAME_TO_ID.put("minecraft:oak_log", 17);
        NAME_TO_ID.put("minecraft:spruce_log", 17);
        NAME_TO_ID.put("minecraft:birch_log", 17);
        NAME_TO_ID.put("minecraft:jungle_log", 17);
        NAME_TO_ID.put("minecraft:leaves", 18);
        NAME_TO_ID.put("minecraft:oak_leaves", 18);
        NAME_TO_ID.put("minecraft:spruce_leaves", 18);
        NAME_TO_ID.put("minecraft:birch_leaves", 18);
        NAME_TO_ID.put("minecraft:jungle_leaves", 18);
        NAME_TO_ID.put("minecraft:glass", 20);
        NAME_TO_ID.put("minecraft:lapis_ore", 21);
        NAME_TO_ID.put("minecraft:lapis_block", 22);
        NAME_TO_ID.put("minecraft:dispenser", 23);
        NAME_TO_ID.put("minecraft:sandstone", 24);
        NAME_TO_ID.put("minecraft:noteblock", 25);
        NAME_TO_ID.put("minecraft:bed", 26);
        NAME_TO_ID.put("minecraft:golden_rail", 27);
        NAME_TO_ID.put("minecraft:detector_rail", 28);
        NAME_TO_ID.put("minecraft:sticky_piston", 29);
        NAME_TO_ID.put("minecraft:web", 30);
        NAME_TO_ID.put("minecraft:tallgrass", 31);
        NAME_TO_ID.put("minecraft:grass_tall", 31);
        NAME_TO_ID.put("minecraft:deadbush", 32);
        NAME_TO_ID.put("minecraft:piston", 33);
        NAME_TO_ID.put("minecraft:pistonArmCollision", 34);
        NAME_TO_ID.put("minecraft:wool", 35);
        NAME_TO_ID.put("minecraft:flowing_lava", 10);
        NAME_TO_ID.put("minecraft:diamond_ore", 56);
        NAME_TO_ID.put("minecraft:diamond_block", 57);
        NAME_TO_ID.put("minecraft:crafting_table", 58);
        NAME_TO_ID.put("minecraft:furnace", 61);
        NAME_TO_ID.put("minecraft:lit_furnace", 62);
        NAME_TO_ID.put("minecraft:torch", 50);
        NAME_TO_ID.put("minecraft:chest", 54);
        NAME_TO_ID.put("minecraft:bookshelf", 47);
        NAME_TO_ID.put("minecraft:fence", 85);
        NAME_TO_ID.put("minecraft:netherrack", 87);
        NAME_TO_ID.put("minecraft:soul_sand", 88);
        NAME_TO_ID.put("minecraft:glowstone", 89);
        NAME_TO_ID.put("minecraft:obsidian", 49);
        NAME_TO_ID.put("minecraft:ice", 79);
        NAME_TO_ID.put("minecraft:snow", 80);
        NAME_TO_ID.put("minecraft:snow_layer", 78);
        NAME_TO_ID.put("minecraft:cactus", 81);
        NAME_TO_ID.put("minecraft:clay", 82);
        NAME_TO_ID.put("minecraft:brick_block", 45);
        NAME_TO_ID.put("minecraft:pumpkin", 86);
        NAME_TO_ID.put("minecraft:nether_brick", 112);
        NAME_TO_ID.put("minecraft:farmland", 60);
        NAME_TO_ID.put("minecraft:wheat", 59);
        NAME_TO_ID.put("minecraft:carrots", 141);
        NAME_TO_ID.put("minecraft:potatoes", 142);
        NAME_TO_ID.put("minecraft:beetroots", 207);
        NAME_TO_ID.put("minecraft:melon_block", 103);
        NAME_TO_ID.put("minecraft:melon_stem", 105);
        NAME_TO_ID.put("minecraft:pumpkin_stem", 104);
        NAME_TO_ID.put("minecraft:vine", 106);
        NAME_TO_ID.put("minecraft:mycelium", 110);
        NAME_TO_ID.put("minecraft:waterlily", 111);
        NAME_TO_ID.put("minecraft:end_stone", 121);
        NAME_TO_ID.put("minecraft:quartz_ore", 153);
        NAME_TO_ID.put("minecraft:quartz_block", 155);
        NAME_TO_ID.put("minecraft:stained_hardened_clay", 159);
        NAME_TO_ID.put("minecraft:hardened_clay", 172);
        NAME_TO_ID.put("minecraft:coal_block", 173);
        NAME_TO_ID.put("minecraft:packed_ice", 174);
        NAME_TO_ID.put("minecraft:red_flower", 38);
        NAME_TO_ID.put("minecraft:yellow_flower", 37);
        NAME_TO_ID.put("minecraft:brown_mushroom", 39);
        NAME_TO_ID.put("minecraft:red_mushroom", 40);
        NAME_TO_ID.put("minecraft:mushroom_stem", 99);
        NAME_TO_ID.put("minecraft:red_mushroom_block", 100);
        NAME_TO_ID.put("minecraft:brown_mushroom_block", 99);
        NAME_TO_ID.put("minecraft:iron_block", 42);
        NAME_TO_ID.put("minecraft:gold_block", 41);
        NAME_TO_ID.put("minecraft:emerald_ore", 129);
        NAME_TO_ID.put("minecraft:emerald_block", 133);
        NAME_TO_ID.put("minecraft:redstone_ore", 73);
        NAME_TO_ID.put("minecraft:lit_redstone_ore", 74);
        NAME_TO_ID.put("minecraft:redstone_block", 152);
        NAME_TO_ID.put("minecraft:tnt", 46);
        NAME_TO_ID.put("minecraft:bookshelf", 47);
        NAME_TO_ID.put("minecraft:mossy_cobblestone", 48);
        NAME_TO_ID.put("minecraft:obsidian", 49);
        NAME_TO_ID.put("minecraft:oak_stairs", 53);
        NAME_TO_ID.put("minecraft:wooden_door", 64);
        NAME_TO_ID.put("minecraft:ladder", 65);
        NAME_TO_ID.put("minecraft:rail", 66);
        NAME_TO_ID.put("minecraft:cobblestone_stairs", 67);
        NAME_TO_ID.put("minecraft:wall_sign", 68);
        NAME_TO_ID.put("minecraft:standing_sign", 63);
        NAME_TO_ID.put("minecraft:lever", 69);
        NAME_TO_ID.put("minecraft:stone_pressure_plate", 70);
        NAME_TO_ID.put("minecraft:wooden_pressure_plate", 72);
        NAME_TO_ID.put("minecraft:unlit_redstone_torch", 75);
        NAME_TO_ID.put("minecraft:redstone_torch", 76);
        NAME_TO_ID.put("minecraft:stone_button", 77);
        NAME_TO_ID.put("minecraft:snow_layer", 78);
        NAME_TO_ID.put("minecraft:ice", 79);
        NAME_TO_ID.put("minecraft:fence_gate", 107);
        NAME_TO_ID.put("minecraft:brick_stairs", 108);
        NAME_TO_ID.put("minecraft:stone_brick_stairs", 109);
        NAME_TO_ID.put("minecraft:nether_brick_fence", 113);
        NAME_TO_ID.put("minecraft:nether_brick_stairs", 114);
        NAME_TO_ID.put("minecraft:sandstone_stairs", 128);
        NAME_TO_ID.put("minecraft:quartz_stairs", 156);
        NAME_TO_ID.put("minecraft:wooden_button", 143);
        NAME_TO_ID.put("minecraft:wooden_door", 64);
        NAME_TO_ID.put("minecraft:spruce_door", 193);
        NAME_TO_ID.put("minecraft:birch_door", 194);
        NAME_TO_ID.put("minecraft:jungle_door", 195);
        NAME_TO_ID.put("minecraft:acacia_door", 196);
        NAME_TO_ID.put("minecraft:dark_oak_door", 197);
        NAME_TO_ID.put("minecraft:iron_trapdoor", 167);
        NAME_TO_ID.put("minecraft:spruce_fence_gate", 187);
        NAME_TO_ID.put("minecraft:birch_fence_gate", 189);
        NAME_TO_ID.put("minecraft:jungle_fence_gate", 188);
        NAME_TO_ID.put("minecraft:dark_oak_fence_gate", 186);
        NAME_TO_ID.put("minecraft:spruce_stairs", 134);
        NAME_TO_ID.put("minecraft:birch_stairs", 135);
        NAME_TO_ID.put("minecraft:jungle_stairs", 136);
        NAME_TO_ID.put("minecraft:acacia_stairs", 163);
        NAME_TO_ID.put("minecraft:dark_oak_stairs", 164);
        NAME_TO_ID.put("minecraft:end_portal_frame", 120);
        NAME_TO_ID.put("minecraft:end_portal", 119);
        NAME_TO_ID.put("minecraft:end_rod", 208);
        NAME_TO_ID.put("minecraft:shulker_box", 205);
        NAME_TO_ID.put("minecraft:undyed_shulker_box", 205);
        NAME_TO_ID.put("minecraft:concrete", 236);
        NAME_TO_ID.put("minecraft:concrete_powder", 237);
        NAME_TO_ID.put("minecraft:barrier", 416); // > 255, will be mapped to 95 by ItemMapper
        // Fallback for any "minecraft:*" not listed: use stone (1) so blocks
        // are visible. Air is special-cased.
    }

    /** Fallback ID when a block state name is not in the map (non-air). */
    private static final int FALLBACK_ID = 1; // stone

    /**
     * Converts a subchunk-format chunk blob to legacy column-ordered format.
     *
     * @param data the raw chunk blob from FullChunkDataPacket.getData()
     * @param blockMapper the runtime-id → (legacyId, meta) mapper built from
     *     the StartGamePacket block palette. Used to resolve RUNTIME palette
     *     entries (the most common case for v113 servers). May be null, in
     *     which case runtime palette entries resolve to air (legacy id 0).
     * @return legacy blob of LEGACY_BLOB_SIZE bytes + any trailing block NBT,
     *         or null if the blob could not be parsed
     */
    public static byte[] convertToLegacy(byte[] data, org.CreadoresProgram.CraftsMine.utils.BlockMapper blockMapper) {
        if (data == null || data.length == 0) return null;
        // Only chunk version 1 (subchunk format) needs conversion.
        // Version 0 or no-version (legacy) is forwarded verbatim by the caller.
        if (data[0] != 1) return null;

        ByteBuf buf = Unpooled.wrappedBuffer(data);
        try {
            return parseSubchunkFormat(buf, blockMapper);
        } catch (Throwable t) {
            return null;
        } finally {
            buf.release();
        }
    }

    private static byte[] parseSubchunkFormat(ByteBuf buf, org.CreadoresProgram.CraftsMine.utils.BlockMapper blockMapper) {
        byte chunkVersion = buf.readByte(); // 1
        // For v113/Bedrock 1.1, subchunkCount should be read as VarInt.
        // But some servers send chunkVersion 1 with a different subchunk layout.
        // We read the subchunk count as a VarInt.
        int subchunkCount = readVarInt(buf);

        // 0.15.x chunk is 16x16x128 = 8 subchunks of 16x16x16.
        // We only read up to 8 subchunks (height 128). Extra subchunks (for
        // height 256 servers) are skipped.
        int subchunksToRead = Math.min(subchunkCount, 8);

        // blockIds[subchunk][y][z][x] — we store as 3D: subchunk(8) * y(16) * xz(256)
        // But the legacy format is column-ordered (x,z,y), so we build it directly.
        byte[] orderedIds = new byte[16 * 16 * 128];      // 32768
        byte[] orderedData = new byte[16 * 16 * 64];       // 16384 (nibbles)
        byte[] orderedSkyLight = new byte[16 * 16 * 64];   // 16384 (full skylight)
        byte[] orderedBlockLight = new byte[16 * 16 * 64]; // 16384 (no block light)

        for (int sc = 0; sc < subchunksToRead; sc++) {
            byte subchunkVersion = buf.readByte();
            int storageCount;
            // Subchunk version handling (matches Bedrock 1.2+ spec):
            //   version 0: legacy single-storage (no storageCount byte)
            //   version 1: storageSize = 1 implicit (NO storageCount byte)
            //   version 8: storageSize = next byte
            //   version 9: storageSize = next byte, then height byte
            if (subchunkVersion == 0 || subchunkVersion == 1) {
                storageCount = 1;
            } else if (subchunkVersion == 8 || subchunkVersion == 9) {
                storageCount = buf.readByte() & 0xff;
                if (subchunkVersion == 9) {
                    // height byte follows storageSize - we don't use it
                    // (world Y offset is implicit in the section index).
                    if (buf.isReadable()) buf.readByte();
                }
            } else {
                // Unknown subchunk version - bail on this subchunk (air).
                storageCount = 0;
            }

            // We only care about the first storage (layer 0 = solid blocks).
            // Read all storages but only extract block IDs from storage 0.
            int[][] blockIdsForSubchunk = null;
            for (int storage = 0; storage < storageCount; storage++) {
                byte flags = buf.readByte();
                int bitsPerBlock = (flags & 0xff) >> 1;
                // CRITICAL FIX (BarrelMC reference): Bedrock's bit 0 of the
                // paletteHeader flag is the IS_RUNTIME flag (1 = runtime
                // palette with VarInt entries, 0 = persistent palette with
                // NbtMap entries). The previous code inverted this flag and
                // always read NbtMap entries, which only works if the
                // server happens to send a persistent palette. Most v113+
                // servers send runtime palettes, so the previous code tried
                // to parse VarInt runtime IDs as NbtMap bytes and crashed.
                boolean isRuntime = (flags & 1) == 1;
                if (bitsPerBlock == 0) bitsPerBlock = 1;

                // CRITICAL FIX (BarrelMC reference): the bit-array is
                // packed into 32-bit LITTLE-ENDIAN WORDS, NOT a flat byte
                // stream. The byte count must be a multiple of 4 and is
                // computed as:
                //   blocksPerWord = floor(32 / bitsPerBlock)
                //   wordsCount    = ceil(4096 / blocksPerWord)
                //   wordBytes     = wordsCount * 4
                // The previous code computed (4096 * bitsPerBlock + 7) / 8
                // which is correct only for bitsPerBlock that divide 32
                // evenly (1, 2, 4, 8, 16). For 3, 5, 6, 7 it under-reads,
                // leaving the buffer misaligned for the palette and
                // producing corrupted block indices.
                int blocksPerWord = Integer.divideUnsigned(32, bitsPerBlock);
                if (blocksPerWord <= 0) blocksPerWord = 1;
                int wordsCount = (SUBCHUNK_SIZE + blocksPerWord - 1) / blocksPerWord;
                int blockDataBytes = wordsCount * 4;
                byte[] blockData = new byte[blockDataBytes];
                buf.readBytes(blockData);

                // Decode the LE 32-bit words once - readBits will index
                // into this words[] array instead of walking bytes.
                int[] words = new int[wordsCount];
                for (int w = 0; w < wordsCount; w++) {
                    int off = w * 4;
                    if (off + 4 <= blockData.length) {
                        words[w] = ((blockData[off] & 0xff))
                                 | ((blockData[off + 1] & 0xff) << 8)
                                 | ((blockData[off + 2] & 0xff) << 16)
                                 | ((blockData[off + 3] & 0xff) << 24);
                    }
                }

                // Palette: runtime (VarInt entries) vs persistent (NbtMap).
                int paletteSize = readVarInt(buf);
                int[] paletteIds = new int[paletteSize];
                if (isRuntime) {
                    // Runtime palette: each entry is a VarInt32 runtime
                    // block id pre-resolved against the server's block
                    // palette. Resolve to legacy id via BlockMapper.
                    for (int p = 0; p < paletteSize; p++) {
                        int runtimeId = readVarInt(buf);
                        if (blockMapper != null) {
                            paletteIds[p] = blockMapper.getLegacyId(runtimeId);
                        } else {
                            // No mapper available - leave as 0 (air).
                            paletteIds[p] = 0;
                        }
                    }
                } else {
                    // Persistent palette: each entry is a block-state
                    // NbtMap encoded with the LE network NBT reader.
                    // Resolve via the hardcoded NAME_TO_ID map.
                    for (int p = 0; p < paletteSize; p++) {
                        NbtMap state = readNbtMap(buf);
                        paletteIds[p] = blockStateToId(state);
                    }
                }

                if (storage == 0) {
                    // Extract the 4096 block IDs for this subchunk.
                    blockIdsForSubchunk = new int[SUBCHUNK_SIZE][];
                    for (int idx = 0; idx < SUBCHUNK_SIZE; idx++) {
                        // CRITICAL FIX (BarrelMC reference): index the
                        // bit-array via word-aligned access, NOT a flat
                        // bit offset. Each word holds blocksPerWord block
                        // indices; the index inside the word is computed
                        // from (idx % blocksPerWord) * bitsPerBlock.
                        int paletteIndex = readBits(words, idx, bitsPerBlock, blocksPerWord);
                        if (paletteIndex >= paletteSize) paletteIndex = 0;
                        blockIdsForSubchunk[idx] = new int[]{ paletteIds[paletteIndex] };
                    }
                }
            }

            // Write the subchunk's blocks into the orderedIds array.
            // Subchunk layout: x(0..15), z(0..15), y(0..15) within the subchunk.
            // Legacy column-ordered: for each (x,z) column, write y=0..127.
            // Subchunk sc covers y = sc*16 .. sc*16+15.
            if (blockIdsForSubchunk != null) {
                for (int x = 0; x < 16; x++) {
                    for (int z = 0; z < 16; z++) {
                        for (int y = 0; y < 16; y++) {
                            // Bedrock subchunk block order is XZY: idx = x*256 + z*16 + y
                            int subIdx = x * 256 + z * 16 + y;
                            int blockId = blockIdsForSubchunk[subIdx][0];
                            int globalY = sc * 16 + y;
                            // Legacy column-ordered: orderedIds[(x*16+z)*128 + globalY]
                            // (matches ChunkRequestTask: column index = x*16+z, then y=0..127)
                            int legacyIdx = (x * 16 + z) * 128 + globalY;
                            if (legacyIdx < orderedIds.length) {
                                orderedIds[legacyIdx] = (byte) (blockId & 0xff);
                            }
                        }
                    }
                }
            }
        }

        // After subchunks, read heightmap (256 little-endian shorts = 512 bytes)
        byte[] heightMap = new byte[256];
        if (buf.readableBytes() >= 512) {
            for (int i = 0; i < 256; i++) {
                int h = buf.readShortLE() & 0xffff;
                heightMap[i] = (byte) (h & 0xff);
            }
        } else {
            // Not enough data; use default heightmap (all 0)
        }

        // Read biome data
        byte[] biomeColors = new byte[1024]; // 256 ints * 4 bytes, big-endian RGBA
        // Default biome color: plains = 0x4FAF5F (greenish)
        int defaultBiomeColor = 0x4FAF5F00; // ABGR? or RGBA?
        for (int i = 0; i < 256; i++) {
            biomeColors[i * 4]     = (byte) ((defaultBiomeColor >> 24) & 0xff);
            biomeColors[i * 4 + 1] = (byte) ((defaultBiomeColor >> 16) & 0xff);
            biomeColors[i * 4 + 2] = (byte) ((defaultBiomeColor >> 8) & 0xff);
            biomeColors[i * 4 + 3] = (byte) (defaultBiomeColor & 0xff);
        }
        // Try to read biome data if available
        if (buf.readableBytes() > 0) {
            try {
                byte biomeVersion = buf.readByte();
                if (biomeVersion == 0) {
                    // 256 bytes of biome IDs
                    byte[] biomeIds = new byte[256];
                    buf.readBytes(biomeIds);
                    // Encode biome IDs into biomeColors (biome ID in high byte)
                    for (int i = 0; i < 256; i++) {
                        int id = biomeIds[i] & 0xff;
                        // biomeColors format: (biomeId << 24) | (r << 16) | (g << 8) | b
                        biomeColors[i * 4]     = (byte) id;
                        biomeColors[i * 4 + 1] = (byte) 0x7F;
                        biomeColors[i * 4 + 2] = (byte) 0xAF;
                        biomeColors[i * 4 + 3] = (byte) 0x5F;
                    }
                }
                // biomeVersion >= 1 (palette-based) is more complex; skip for now
            } catch (Throwable ignored) {}
        }

        // Remaining bytes are block entities NBT (forward verbatim)
        byte[] blockEntities = new byte[buf.readableBytes()];
        if (blockEntities.length > 0) {
            buf.readBytes(blockEntities);
        }

        // Full skylight (all 0xFF = max light)
        java.util.Arrays.fill(orderedSkyLight, (byte) 0xFF);

        // Assemble the legacy blob
        byte[] legacy = new byte[LEGACY_BLOB_SIZE + blockEntities.length];
        int offset = 0;
        System.arraycopy(orderedIds, 0, legacy, offset, orderedIds.length); offset += orderedIds.length;
        System.arraycopy(orderedData, 0, legacy, offset, orderedData.length); offset += orderedData.length;
        System.arraycopy(orderedSkyLight, 0, legacy, offset, orderedSkyLight.length); offset += orderedSkyLight.length;
        System.arraycopy(orderedBlockLight, 0, legacy, offset, orderedBlockLight.length); offset += orderedBlockLight.length;
        System.arraycopy(heightMap, 0, legacy, offset, heightMap.length); offset += heightMap.length;
        System.arraycopy(biomeColors, 0, legacy, offset, biomeColors.length); offset += biomeColors.length;
        if (blockEntities.length > 0) {
            System.arraycopy(blockEntities, 0, legacy, offset, blockEntities.length);
        }
        return legacy;
    }

    /** Reads an unsigned VarInt from the ByteBuf. */
    private static int readVarInt(ByteBuf buf) {
        int result = 0;
        int shift = 0;
        byte b;
        do {
            b = buf.readByte();
            result |= (b & 0x7F) << shift;
            shift += 7;
        } while ((b & 0x80) != 0);
        return result;
    }

    /** Reads a Bedrock little-endian network NBT tag from the ByteBuf. */
    private static NbtMap readNbtMap(ByteBuf buf) {
        try {
            ByteBufInputStream in = new ByteBufInputStream(buf);
            // Bedrock palette entries use the "network" NBT format:
            // little-endian, no root name. NbtUtils.createNetworkReader()
            // creates the correct reader for this format.
            NBTInputStream nbtIn = NbtUtils.createNetworkReader(in);
            Object tag = nbtIn.readTag();
            if (tag instanceof NbtMap) {
                return (NbtMap) tag;
            }
        } catch (Throwable ignored) {}
        return NbtMap.builder().putString("name", "minecraft:air").build();
    }

    /** Maps a Bedrock block state (NbtMap) to a 0.15.x numeric ID. */
    private static int blockStateToId(NbtMap state) {
        if (state == null) return 0;
        String name = state.getString("name", null);
        if (name == null) {
            // Try lowercase key
            name = state.getString("Name", null);
        }
        if (name == null) return 0;
        Integer id = NAME_TO_ID.get(name);
        if (id != null) return id;
        // Unknown block: use stone as fallback (so it's visible, not invisible)
        if (name.equals("minecraft:air")) return 0;
        return FALLBACK_ID;
    }

    /**
     * Reads the palette index for a single block from the packed bit-array.
     *
     * <p>Implements the Bedrock 1.2+ subchunk bit-array read per the
     * BarrelMC reference algorithm:</p>
     * <pre>
     *   wordIndex = blockIndex / blocksPerWord
     *   bitOffset = (blockIndex % blocksPerWord) * bitsPerBlock
     *   word      = words[wordIndex]   // already endian-swapped from LE bytes
     *   mask      = (1 &lt;&lt; bitsPerBlock) - 1
     *   paletteIndex = (word &gt;&gt;&gt; bitOffset) &amp; mask
     * </pre>
     *
     * <p>The previous byte-by-byte implementation was incorrect for
     * bitsPerBlock values that do not divide 32 evenly (3, 5, 6, 7) because
     * Bedrock pads each 32-bit word with unused trailing bits, while the
     * byte-by-byte walk treated the bit-array as a flat bitstream.</p>
     *
     * @param words the 4096-block bit-array, already decoded from the LE
     *   wire bytes into 32-bit ints
     * @param blockIndex the block index in [0, 4096)
     * @param bitsPerBlock bits per block index
     * @param blocksPerWord floor(32 / bitsPerBlock)
     */
    private static int readBits(int[] words, int blockIndex, int bitsPerBlock, int blocksPerWord) {
        if (blocksPerWord <= 0) blocksPerWord = 1;
        int wordIndex = blockIndex / blocksPerWord;
        if (wordIndex < 0 || wordIndex >= words.length) return 0;
        int bitOffset = (blockIndex % blocksPerWord) * bitsPerBlock;
        int mask = (1 << bitsPerBlock) - 1;
        return (words[wordIndex] >>> bitOffset) & mask;
    }
}
