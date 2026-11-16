package com.craftsmine.nkm.protocol;

import cn.nukkit.network.protocol.v113.ProtocolInfo_v113;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * Mapeador integral de contenido multiversion para protocolos legados (80-112 -> 113):
 * - Bloques y metadatos por familia (0.15.x, 0.16.x, 1.0.x, 1.1.0)
 * - Items, inventario creativo y NBT seguro (sin fuegos artificiales, estandartes, shulkers, totems, etc.)
 * - Entidades / mobs / proyectiles (downgrade o supresion si no existen en el cliente)
 * - Metadatos de entidades (0.15.x key map 0..24 + byte flags, 90..109 flag shifts)
 * - Sonidos (LevelSoundEvent y puente LevelSound -> LevelEvent en 0.15.x), particulas y eventos
 * - Skins RGBA (downscale 128x128 / 128x64 -> 64x64 para < 100)
 */
public final class LegacyContentMapper {

    public static final int DATA_TYPE_BYTE = 0;
    public static final int DATA_TYPE_SHORT = 1;
    public static final int DATA_TYPE_INT = 2;
    public static final int DATA_TYPE_FLOAT = 3;
    public static final int DATA_TYPE_STRING = 4;
    public static final int DATA_TYPE_SLOT = 5;
    public static final int DATA_TYPE_POS = 6;
    public static final int DATA_TYPE_LONG = 7;
    public static final int DATA_TYPE_VECTOR3F = 8;

    private static final int EVENT_ADD_PARTICLE_MASK = 0x4000;

    private LegacyContentMapper() {
    }

    // =========================================================================
    // 1. Bloques y metadatos de bloque
    // =========================================================================

    /**
     * Convierte (blockId, meta) canonico de v113 a un bloque valido para {@code dst}.
     * Retorna valor empaquetado {@code (mappedId << 8) | (mappedMeta & 0x0F)}.
     */
    public static int mapBlockAndMeta(int blockId, int meta, LegacyProtocol dst) {
        int id = blockId & 0xFF;
        int m = meta & 0x0F;

        if (!dst.needsTranslation()) {
            return (id << 8) | m;
        }

        // Bloques 1.2+ / Education / posteriores que nunca deben llegar crudos a < 113
        switch (id) {
            case 166 -> { // BARRIER / GLOW_STICK
                id = 20;
                m = 0;
            }
            case 176, 177 -> { // BANNER -> Sign / Wall Sign
                id = (id == 176) ? 63 : 68;
                m = m & 0x07;
            }
            case 190, 191, 253, 254 -> { // HARD_GLASS / STAINED
                id = 20;
                m = 0;
            }
            case 192, 238 -> { // CHEMICAL_HEAT / CHEMISTRY_TABLE
                id = 58;
                m = 0;
            }
            case 202, 204, 239 -> { // COLORED / UNDERWATER TORCH
                id = 50;
                m = Math.min(5, m);
            }
            case 241 -> { // STAINED_GLASS (1.2+) -> Glass
                id = 20;
                m = 0;
            }
            case 242 -> { // CAMERA_BLOCK -> Cobblestone
                id = 4;
                m = 0;
            }
            default -> {
            }
        }
        if (blockId > 255) {
            id = 1; // Stone fallback
            m = 0;
        }

        // 1.1.0 bloques (205, 218 Shulker, 219..237 GlazedTerracotta/Concrete) no existen en < 110
        if (dst.isBefore110()) {
            switch (id) {
                case 205, 218 -> { // Shulker box -> Chest / Stained clay
                    id = 54;
                    m = 2;
                }
                case 219, 220, 221, 222, 223, 224, 225, 226, 227, 228, 229, 230, 231, 232, 233, 234, 235 -> {
                    int color = switch (id) {
                        case 219 -> 10; // purple
                        case 220 -> 0;  // white
                        case 221 -> 1;  // orange
                        case 222 -> 2;  // magenta
                        case 223 -> 3;  // light_blue
                        case 224 -> 4;  // yellow
                        case 225 -> 5;  // lime
                        case 226 -> 6;  // pink
                        case 227 -> 7;  // gray
                        case 228 -> 8;  // silver
                        case 229 -> 9;  // cyan
                        case 230 -> 10; // purple extra
                        case 231 -> 11; // blue
                        case 232 -> 12; // brown
                        case 233 -> 13; // green
                        case 234 -> 14; // red
                        default -> 15;  // black
                    };
                    id = 159;
                    m = color;
                }
                case 236 -> { // Concrete -> Stained clay
                    id = 159;
                    m = m & 0x0F;
                }
                case 237 -> { // Concrete powder -> Sand
                    id = 12;
                    m = 0;
                }
                case 207 -> { // Frosted ice -> Ice
                    id = 79;
                    m = 0;
                }
                default -> {
                }
            }
        }

        // 1.0.x bloques (End / Command Blocks / Magma / Bone / Polar) no existen en < 92 (0.15 - 0.16)
        if (dst.isBefore92()) {
            switch (id) {
                case 119 -> { id = 90; m = 0; }    // END_PORTAL -> PORTAL
                case 120 -> { id = 48; m = 0; }    // END_PORTAL_FRAME -> MOSSY_COBBLE
                case 121 -> { id = 24; m = 0; }    // END_STONE -> SANDSTONE
                case 122 -> { id = 49; m = 0; }    // DRAGON_EGG -> OBSIDIAN
                case 130 -> { id = 54; m = 2; }    // ENDER_CHEST -> CHEST
                case 137, 188, 189 -> { id = 159; m = 1; } // COMMAND_BLOCK* -> ORANGE_CLAY
                case 200, 240 -> { id = 175; m = 0; } // CHORUS -> DOUBLE_PLANT
                case 201 -> { id = 155; m = 0; }   // PURPUR_BLOCK -> QUARTZ_BLOCK
                case 203 -> { id = 156; m = m & 7; } // PURPUR_STAIRS -> QUARTZ_STAIRS
                case 206 -> { id = 24; m = 2; }    // END_BRICKS -> SMOOTH_SANDSTONE
                case 208 -> { id = 50; m = 5; }    // END_ROD -> TORCH
                case 209 -> { id = 49; m = 0; }    // END_GATEWAY -> OBSIDIAN
                case 210, 211, 212 -> { id = 7; m = 0; } // ALLOW/DENY/BORDER -> BEDROCK
                case 213 -> { id = 87; m = 0; }    // MAGMA -> NETHERRACK
                case 214, 215 -> { id = 112; m = 0; } // NETHER_WART_BLOCK / RED_NETHER_BRICK -> NETHER_BRICK
                case 216 -> { id = 155; m = 0; }   // BONE_BLOCK -> QUARTZ_BLOCK
                case 217 -> { id = 0; m = 0; }     // STRUCTURE_VOID -> AIR
                case 252 -> { id = 7; m = 0; }     // STRUCTURE_BLOCK -> BEDROCK
                default -> {
                }
            }
        }

        // 0.16.x bloques (Beacon 138, Prismarine 168, SeaLantern 169) no existen en 0.15.x (< 90)
        if (dst.isBefore90()) {
            switch (id) {
                case 138 -> { id = 41; m = 0; }    // BEACON -> GOLD_BLOCK
                case 168 -> {                      // PRISMARINE -> STONE_BRICKS / MOSSY
                    id = 98;
                    m = (m == 2) ? 1 : 0;
                }
                case 169 -> { id = 89; m = 0; }    // SEA_LANTERN -> GLOWSTONE
                case 255 -> { id = 0; m = 0; }     // RESERVED6 -> AIR
                default -> {
                }
            }
            // Sanitizar metadata de bloques antiguos para evitar crash de renderer en 0.15.x
            if (id == 19 && m > 0) { // WET SPONGE -> DRY SPONGE (0.16+)
                m = 0;
            } else if (id == 175 && (m & 0x07) > 5) { // DOUBLE_PLANT max variant 5
                m = (m & 0x08);
            } else if (id == 1 && m > 6) {
                m = 0;
            } else if (id == 5 && m > 5) {
                m = 0;
            } else if (id == 6 && m > 13) {
                m = m & 0x03;
            } else if (id == 38 && m > 8) {
                m = 0;
            } else if (id == 97 && m > 5) {
                m = 0;
            } else if (id == 98 && m > 3) {
                m = 0;
            } else if (id == 155 && m > 2) {
                m = 0;
            }
        }

        return (id << 8) | (m & 0x0F);
    }

    // =========================================================================
    // 2. Items, creativo y slots
    // =========================================================================

    /**
     * Traduce/downgradea un slot saliente para {@code dst}.
     * Si {@code creativeMode == true} y el item no existe en el cliente destino, retorna {@code null}
     * para omitirlo del inventario creativo sin provocar crashes en 0.15.10 / 0.16 / 1.0.
     */
    public static LegacyStream.LegacySlot mapSlotOutbound(
            LegacyStream.LegacySlot slot,
            LegacyProtocol dst,
            boolean creativeMode
    ) {
        if (slot == null || slot.id() <= 0) {
            return LegacyStream.LegacySlot.AIR;
        }

        int id = slot.id();
        int meta = slot.meta();
        int count = Math.max(1, Math.min(255, slot.count()));
        byte[] nbt = slot.nbt();
        String[] canPlace = dst.isBefore110() ? new String[0] : slot.canPlaceOn();
        String[] canDestroy = dst.isBefore110() ? new String[0] : slot.canDestroy();

        // Bloque como item (1..255)
        if (id >= 1 && id <= 255) {
            if (creativeMode && !isCreativeBlockSupported(id, meta, dst)) {
                return null;
            }
            int packed = mapBlockAndMeta(id, Math.max(0, meta), dst);
            int mappedId = (packed >>> 8) & 0xFF;
            int mappedMeta = meta < 0 ? meta : (packed & 0x0F);
            if (mappedId == 0) {
                return creativeMode ? null : LegacyStream.LegacySlot.AIR;
            }
            byte[] safeNbt = sanitizeItemNbtForSlot(nbt, dst);
            return new LegacyStream.LegacySlot(mappedId, mappedMeta, count, safeNbt, canPlace, canDestroy);
        }

        // Items 1.1+ / modernos -> compatibilidad hacia atras
        if (id == 466) { // ENCHANTED_GOLDEN_APPLE -> 322:1 en <= 1.1.x
            id = 322;
            meta = 1;
        }

        if (id > 509 || (id >= 464 && id <= 499)) {
            return creativeMode ? null : LegacyStream.LegacySlot.AIR;
        }

        // Discos de musica 500..511 (1.2+)
        if (id >= 500) {
            return creativeMode ? null : LegacyStream.LegacySlot.AIR;
        }

        // Fireworks (401), Firework Star (402), Armor Stand (425), Banner (446), Banner Pattern (434), Respawn/Chem (442..456)
        if (id == 401 || id == 402 || id == 425 || id == 434 || id == 442 || (id >= 447 && id <= 449) || id == 451 || id >= 453 && id <= 456) {
            return creativeMode ? null : LegacyStream.LegacySlot.AIR;
        }
        if (id == 446) { // Banner -> Sign en inventario si no creativo
            if (creativeMode) {
                return null;
            }
            id = 323;
            meta = 0;
        }

        if (dst.isBefore110()) {
            // Camas de colores 355:1..15 se introdujeron en 1.1.0 (antes solo 355:0)
            if (id == 355 && meta != 0) {
                if (creativeMode) {
                    return null;
                }
                meta = 0;
            }
            // Items exclusivos de 1.1.0: Totem (450), Shulker Shell (445), Iron Nugget (452)
            if (id == 450 || id == 445 || id == 452) {
                if (creativeMode) {
                    return null;
                }
                id = (id == 450) ? 322 : 0;
                meta = (id == 322) ? 1 : 0;
                if (id == 0) {
                    return LegacyStream.LegacySlot.AIR;
                }
            }
        }

        if (dst.isBefore92()) {
            // Items exclusivos de 1.0.x+: Ender Pearl (368), Eye of Ender (381), End Crystal (426),
            // Chorus (432,433), Dragon Breath (437), Lingering Potion (441), Command Minecart (443), Elytra (444)
            if (id == 368 || id == 381 || id == 426 || id == 432 || id == 433 || id == 437 || id == 441 || id == 443 || id == 444) {
                if (creativeMode) {
                    return null;
                }
                if (id == 444) {
                    id = 307; // Iron chestplate fallback
                    meta = 0;
                } else {
                    return LegacyStream.LegacySlot.AIR;
                }
            }
        }

        if (dst.isBefore90()) {
            // Items de 0.16+: Nether Star (399), Prismarine Shard (409), Prismarine Crystals (422)
            if (id == 399 || id == 409 || id == 422) {
                if (creativeMode) {
                    return null;
                }
                id = 264; // Diamond / Gold ingot fallback no invasivo o AIR
                return LegacyStream.LegacySlot.AIR;
            }
        }

        // Spawn Eggs (383): filtrar mobs no soportados por la version destino (evita crash en inventario 0.15.10)
        if (id == 383) {
            int mappedMob = mapEntityTypeOutbound(meta, dst);
            if (mappedMob < 0) {
                return creativeMode ? null : LegacyStream.LegacySlot.AIR;
            }
            meta = mappedMob;
        }

        // Potions / Splash Potions (373, 438): limitar variantes de pocion en < 110
        if ((id == 373 || id == 438) && dst.isBefore92() && meta > 36) {
            if (creativeMode) {
                return null;
            }
            meta = 0;
        }

        // Tipped Arrows (262:1..37): en < 92 solo flecha normal (262:0)
        if (id == 262 && meta > 0 && dst.isBefore92()) {
            if (creativeMode) {
                return null;
            }
            meta = 0;
        }

        byte[] safeNbt = sanitizeItemNbtForSlot(nbt, dst);
        if (id == 403 && dst.isBefore90() && (safeNbt == null || safeNbt.length == 0)) {
            if (creativeMode) {
                return null; // libro encantado cuyo encantamiento no existe en 0.15
            }
        }

        return new LegacyStream.LegacySlot(id, meta, count, safeNbt, canPlace, canDestroy);
    }

    private static boolean isCreativeBlockSupported(int blockId, int meta, LegacyProtocol dst) {
        if (blockId == 0 || blockId == 255 || blockId == 250 || blockId == 119 || blockId == 209 || blockId == 246 || blockId == 248 || blockId == 249) {
            return false;
        }
        if (blockId == 166 || blockId == 176 || blockId == 177 || blockId == 190 || blockId == 191 || blockId == 192
                || blockId == 202 || blockId == 204 || blockId == 238 || blockId == 239 || blockId == 241
                || blockId == 242 || blockId == 253 || blockId == 254 || blockId > 255) {
            return false;
        }
        if (dst.isBefore110()) {
            if (blockId == 205 || blockId == 207 || (blockId >= 218 && blockId <= 237)) {
                return false;
            }
        }
        if (dst.isBefore92()) {
            if (blockId == 120 || blockId == 121 || blockId == 122 || blockId == 130 || blockId == 137
                    || blockId == 188 || blockId == 189 || (blockId >= 200 && blockId <= 217)
                    || blockId == 240 || blockId == 252) {
                return false;
            }
        }
        if (dst.isBefore90()) {
            if (blockId == 138 || blockId == 168 || blockId == 169 || (blockId == 19 && meta > 0)) {
                return false;
            }
        }
        return true;
    }

    private static byte[] sanitizeItemNbtForSlot(byte[] nbt, LegacyProtocol dst) {
        if (nbt == null || nbt.length == 0) {
            return new byte[0];
        }
        if (dst.isBefore110()) {
            return LegacyNbtConverter.sanitizeItemNbt(nbt, dst);
        }
        return nbt;
    }

    // =========================================================================
    // 3. Entidades, Mobs y Proyectiles
    // =========================================================================

    /**
     * Mapea o filtra un Entity Network ID hacia {@code dst}.
     * Retorna {@code -1} cuando la entidad no existe y no tiene fallback seguro (el AddEntity debe omitirse).
     */
    public static int mapEntityTypeOutbound(int entityType, LegacyProtocol dst) {
        if (!dst.needsTranslation()) {
            return entityType;
        }

        // Entidades modernas (1.2+ / 1.13+ / 1.16+) -> downgrade a equivalentes clasicos
        int mapped = switch (entityType) {
            case 110 -> 32;  // Drowned -> Zombie
            case 114 -> 34;  // Pillager -> Skeleton
            case 115 -> 15;  // VillagerV2 -> Villager
            case 116 -> 44;  // ZombieVillagerV2 -> ZombieVillager
            case 118 -> 15;  // WanderingTrader -> Villager
            case 123, 127 -> 36; // Piglin / Brute -> ZombiePigman
            case 124 -> 12;  // Hoglin -> Pig
            case 126 -> 36;  // Zoglin -> ZombiePigman
            case 144 -> 34;  // Bogged -> Skeleton
            case 30, 31, 58, 59, 73, 74, 75, 108, 109, 111, 112, 113, 121, 122, 125, 128, 129, 130, 131,
                    132, 133, 134, 138, 139, 140, 141, 142, 143, 146, 147, 148, 218 -> -1;
            default -> entityType;
        };
        if (mapped < 0) {
            return -1;
        }

        if (dst.isBefore110()) {
            // 1.1.0: Llama (29), Vindicator (57), Evoker (104), Vex (105), LlamaSpit (102), EvocationFangs (103), Fireworks (72)
            mapped = switch (mapped) {
                case 29 -> 24;   // Llama -> Donkey
                case 57 -> 45;   // Vindicator -> Witch
                case 72, 102, 103, 104, 105 -> -1;
                default -> mapped;
            };
            if (mapped < 0) {
                return -1;
            }
        }

        if (dst.isBefore92()) {
            // 1.0.x: PolarBear (28), EnderDragon (53), Shulker (54), Endermite (55), EndCrystal (71),
            // ShulkerBullet (76), EnderEye (70), EnderPearl (87), BlueWitherSkull (91), CommandMinecart (100), LingeringPotion (101)
            mapped = switch (mapped) {
                case 55 -> 39;   // Endermite -> Silverfish
                case 101 -> 86;  // LingeringPotion -> ThrownPotion
                case 28, 53, 54, 70, 71, 76, 79, 87, 91, 100 -> -1;
                default -> mapped;
            };
            if (mapped < 0) {
                return -1;
            }
        }

        if (dst.isBefore90()) {
            // 0.16.x: Guardian (49), ElderGuardian (50), NPC (51), Wither (52), AreaEffectCloud (95)
            // Ademas ArmorStand (61) es 1.2+
            mapped = switch (mapped) {
                case 49, 50, 51, 52, 61, 95, 99 -> -1;
                default -> mapped;
            };
            if (mapped < 0) {
                return -1;
            }
        }

        // Verificacion estricta de IDs validos <= 1.1
        return switch (mapped) {
            case 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29,
                    32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 57,
                    63, 64, 65, 66, 67, 68, 69, 70, 71, 72, 76, 77, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 91, 93, 94, 95,
                    96, 97, 98, 100, 101, 102, 103, 104, 105 -> mapped;
            default -> -1;
        };
    }

    // =========================================================================
    // 4. Metadatos de Entidades (v113 -> 0.15.x y v113 -> 0.16..1.0.9)
    // =========================================================================

    /**
     * Traduce EntityMetadata desde formato v113 al formato exacto del cliente {@code dst}.
     */
    public static byte[] translateMetadataOutbound(LegacyStream in, LegacyProtocol dst) {
        if (dst.isBefore90()) {
            return translateMetadataToPre90(in, dst);
        }
        return translateMetadataTo90Through112(in, dst);
    }

    /**
     * Convierte metadata v113 (VarInt key/type) al mapa de 0.15.x (ActorMetadataList::PROTOCOL_81):
     * keys 0,1,2,3,4,7,8,14,15,16,17,18,19,20,21,23,24 + terminador 0x7F.
     */
    private static byte[] translateMetadataToPre90(LegacyStream in, LegacyProtocol dst) {
        LegacyStream out = LegacyStream.writer(96);
        int count = in.remaining() > 0 ? in.readUnsignedVarInt() : 0;
        boolean wroteShowNametag = false;
        boolean wroteSilent = false;
        boolean wroteNoAi = false;

        for (int i = 0; i < count && in.remaining() > 0; i++) {
            int key = in.readUnsignedVarInt();
            int type = in.readUnsignedVarInt();

            // DATA_FLAGS (key 0, long en v113 -> byte en 0.15.x con solo bits 0..5 + extrae flags booleanos)
            if (key == 0 && type == DATA_TYPE_LONG) {
                long flags = in.readVarLong();
                out.writeByte((DATA_TYPE_BYTE << 5) | 0);
                out.writeByte((int) (flags & 0x3F));

                boolean alwaysShowName = (flags & (1L << 15)) != 0 || (flags & (1L << 14)) != 0;
                if (!wroteShowNametag) {
                    wroteShowNametag = true;
                    out.writeByte((DATA_TYPE_BYTE << 5) | 3); // DATA_SHOW_NAMETAG
                    out.writeByte(alwaysShowName ? 1 : 0);
                }
                if (!wroteSilent) {
                    wroteSilent = true;
                    out.writeByte((DATA_TYPE_BYTE << 5) | 4); // DATA_SILENT
                    out.writeByte((flags & (1L << 17)) != 0 ? 1 : 0);
                }
                if (!wroteNoAi) {
                    wroteNoAi = true;
                    out.writeByte((DATA_TYPE_BYTE << 5) | 15); // DATA_NO_AI
                    out.writeByte((flags & (1L << 16)) != 0 ? 1 : 0);
                }
                if ((flags & (1L << 7)) != 0) { // DATA_FLAG_INLOVE
                    out.writeByte((DATA_TYPE_BYTE << 5) | 21); // DATA_IN_LOVE
                    out.writeByte(1);
                }
                if ((flags & (1L << 30)) != 0) { // DATA_FLAG_LEASHED (v113 bit 30)
                    out.writeByte((DATA_TYPE_BYTE << 5) | 24); // DATA_LEAD
                    out.writeByte(1);
                }
                continue;
            }

            switch (key) {
                case 7 -> { // DATA_AIR (v113 key 7 short -> 0.15 key 1 short)
                    int air = readMetaAsInt(in, type);
                    out.writeByte((DATA_TYPE_SHORT << 5) | 1);
                    out.writeShortLE(air);
                }
                case 4 -> { // DATA_NAMETAG (v113 key 4 string -> 0.15 key 2 string)
                    String tag = readMetaAsString(in, type);
                    out.writeByte((DATA_TYPE_STRING << 5) | 2);
                    writeLegacyMetaString(out, tag);
                }
                case 8 -> { // DATA_POTION_COLOR (v113 key 8 int -> 0.15 key 7 int)
                    int color = readMetaAsInt(in, type);
                    out.writeByte((DATA_TYPE_INT << 5) | 7);
                    out.writeIntLE(color);
                }
                case 9 -> { // DATA_POTION_AMBIENT (v113 key 9 byte -> 0.15 key 8 byte)
                    int amb = readMetaAsInt(in, type);
                    out.writeByte((DATA_TYPE_BYTE << 5) | 8);
                    out.writeByte(amb);
                }
                case 25 -> { // DATA_ENTITY_AGE (v113 key 25 int/byte -> 0.15 key 14 byte)
                    int age = readMetaAsInt(in, type);
                    out.writeByte((DATA_TYPE_BYTE << 5) | 14);
                    out.writeByte(age != 0 ? 1 : 0);
                }
                case 27 -> { // DATA_PLAYER_FLAGS (v113 key 27 byte -> 0.15 key 16 byte)
                    int pf = readMetaAsInt(in, type);
                    out.writeByte((DATA_TYPE_BYTE << 5) | 16);
                    out.writeByte(pf);
                }
                case 29 -> { // DATA_PLAYER_BED_POSITION (v113 key 29 pos -> 0.15 key 17 pos)
                    if (type == DATA_TYPE_POS) {
                        int[] bp = in.readSignedBlockPosition(LegacyProtocol.PROTOCOL_113);
                        out.writeByte((DATA_TYPE_POS << 5) | 17);
                        out.writeIntLE(bp[0]);
                        out.writeIntLE(bp[1]);
                        out.writeIntLE(bp[2]);
                    } else {
                        skipMetaValue(in, type);
                    }
                }
                case 2 -> { // DATA_VARIANT (v113 key 2 int -> 0.15 key 18 int)
                    int var = readMetaAsInt(in, type);
                    out.writeByte((DATA_TYPE_INT << 5) | 18);
                    out.writeIntLE(var);
                }
                case 19 -> { // DATA_SWELL (v113 key 19 int/byte -> 0.15 key 19 byte)
                    int swell = readMetaAsInt(in, type);
                    out.writeByte((DATA_TYPE_BYTE << 5) | 19);
                    out.writeByte(swell);
                }
                case 3 -> { // DATA_COLOUR (v113 key 3 byte -> 0.15 key 20 DATA_BOAT_COLOR byte)
                    int col = readMetaAsInt(in, type);
                    out.writeByte((DATA_TYPE_BYTE << 5) | 20);
                    out.writeByte(col & 0x0F);
                }
                case 38 -> { // DATA_LEAD_HOLDER_EID (v113 key 38 long -> 0.15 key 23 long)
                    long holder = (type == DATA_TYPE_LONG) ? in.readVarLong() : readMetaAsInt(in, type);
                    out.writeByte((DATA_TYPE_LONG << 5) | 23);
                    out.writeLongLE(holder);
                }
                default -> skipMetaValue(in, type);
            }
        }
        out.writeByte(0x7F); // Terminador 0.15.x
        return out.toByteArray();
    }

    /**
     * Traduce metadata v113 para clientes 90..112:
     * - En < 110 corrige desplazamiento de bits 19..41 en DATA_FLAGS (+3 bits en 110+)
     * - Remapea keys 40..44 segun ActorMetadataList PROTOCOL_90 vs PROTOCOL_110
     * - Filtra VECTOR3F y keys desconocidas por la familia del cliente
     */
    private static byte[] translateMetadataTo90Through112(LegacyStream in, LegacyProtocol dst) {
        int count = in.remaining() > 0 ? in.readUnsignedVarInt() : 0;
        LegacyStream entries = LegacyStream.writer(96);
        int writtenCount = 0;

        for (int i = 0; i < count && in.remaining() > 0; i++) {
            int key = in.readUnsignedVarInt();
            int type = in.readUnsignedVarInt();

            if (type == DATA_TYPE_VECTOR3F) {
                skipMetaValue(in, type);
                continue;
            }

            if (key == 0 && type == DATA_TYPE_LONG) {
                long flags = in.readVarLong();
                if (dst.isBefore110()) {
                    // En < 110: bits 0..18 iguales; 19..21 (CAN_CLIMB/SWIMMER/CAN_FLY) no existian,
                    // y bits 23..41 (RESTING..IDLING) estaban en 19..36!
                    long low = flags & 0x7FFFFL; // bits 0..18
                    long shifted = ((flags >>> 23) & 0x3FFFFL) << 19; // bits 23..40 -> 19..36
                    flags = low | shifted;
                }
                writtenCount++;
                entries.writeUnsignedVarInt(0);
                entries.writeUnsignedVarInt(DATA_TYPE_LONG);
                entries.writeVarLong(flags);
                continue;
            }

            // Remapeo de keys entre v113 y 90..109
            int mappedKey = key;
            if (dst.isBefore110()) {
                if (key == 41 || key == 42 || key == 43 || key == 44) {
                    mappedKey = key + 1; // 41->42, 42->43, 43->44, 44->45
                } else if (key == 45) {
                    mappedKey = 44;
                } else if (key > 60 || key == 46 || key == 47 || (key >= 48 && key <= 52 && dst.isBefore92())) {
                    skipMetaValue(in, type);
                    continue;
                }
            }

            // Remapear bloque display de minecart (key 16 en INT cuando > 0)
            if (key == 16 && type == DATA_TYPE_INT) {
                int display = in.readVarInt();
                if (display > 0) {
                    int bid = display & 0xFF;
                    int bmeta = (display >> 16) & 0x0F;
                    int packed = mapBlockAndMeta(bid, bmeta, dst);
                    display = ((packed >>> 8) & 0xFF) | ((packed & 0x0F) << 16);
                }
                writtenCount++;
                entries.writeUnsignedVarInt(mappedKey);
                entries.writeUnsignedVarInt(DATA_TYPE_INT);
                entries.writeVarInt(display);
                continue;
            }

            if (type == DATA_TYPE_SLOT) {
                LegacyStream.LegacySlot slot = mapSlotOutbound(in.readSlot(LegacyProtocol.PROTOCOL_113), dst, false);
                writtenCount++;
                entries.writeUnsignedVarInt(mappedKey);
                entries.writeUnsignedVarInt(DATA_TYPE_SLOT);
                entries.writeSlot(dst, slot == null ? LegacyStream.LegacySlot.AIR : slot);
                continue;
            }

            writtenCount++;
            entries.writeUnsignedVarInt(mappedKey);
            entries.writeUnsignedVarInt(type);
            copyMetaValue90(in, entries, type);
        }

        LegacyStream out = LegacyStream.writer(entries.toByteArray().length + 8);
        out.writeUnsignedVarInt(writtenCount);
        out.writeBytes(entries.toByteArray());
        return out.toByteArray();
    }

    private static void copyMetaValue90(LegacyStream in, LegacyStream out, int type) {
        switch (type) {
            case DATA_TYPE_BYTE -> out.writeByte(in.readByte());
            case DATA_TYPE_SHORT -> out.writeShortLE(in.readShortLE());
            case DATA_TYPE_INT -> out.writeVarInt(in.readVarInt());
            case DATA_TYPE_FLOAT -> out.writeFloatLE(in.readFloatLE());
            case DATA_TYPE_STRING -> out.writeString(LegacyProtocol.PROTOCOL_113, in.readString(LegacyProtocol.PROTOCOL_113));
            case DATA_TYPE_POS -> {
                int[] p = in.readSignedBlockPosition(LegacyProtocol.PROTOCOL_113);
                out.writeVarInt(p[0]);
                out.writeVarInt(p[1]);
                out.writeVarInt(p[2]);
            }
            case DATA_TYPE_LONG -> out.writeVarLong(in.readVarLong());
            default -> {
            }
        }
    }

    private static int readMetaAsInt(LegacyStream in, int type) {
        return switch (type) {
            case DATA_TYPE_BYTE -> in.readByte();
            case DATA_TYPE_SHORT -> in.readSignedShortLE();
            case DATA_TYPE_INT -> in.readVarInt();
            case DATA_TYPE_LONG -> (int) in.readVarLong();
            case DATA_TYPE_FLOAT -> (int) in.readFloatLE();
            default -> {
                skipMetaValue(in, type);
                yield 0;
            }
        };
    }

    private static String readMetaAsString(LegacyStream in, int type) {
        if (type == DATA_TYPE_STRING) {
            return in.readString(LegacyProtocol.PROTOCOL_113);
        }
        skipMetaValue(in, type);
        return "";
    }

    private static void writeLegacyMetaString(LegacyStream out, String s) {
        byte[] bytes = (s == null ? "" : s).getBytes(StandardCharsets.UTF_8);
        out.writeShortLE(bytes.length);
        out.writeBytes(bytes);
    }

    public static void skipMetaValue(LegacyStream in, int type) {
        switch (type) {
            case DATA_TYPE_BYTE -> in.readByte();
            case DATA_TYPE_SHORT -> in.readShortLE();
            case DATA_TYPE_INT -> in.readVarInt();
            case DATA_TYPE_FLOAT -> in.readFloatLE();
            case DATA_TYPE_STRING -> in.readString(LegacyProtocol.PROTOCOL_113);
            case DATA_TYPE_SLOT -> in.readSlot(LegacyProtocol.PROTOCOL_113);
            case DATA_TYPE_POS -> in.readSignedBlockPosition(LegacyProtocol.PROTOCOL_113);
            case DATA_TYPE_LONG -> in.readVarLong();
            case DATA_TYPE_VECTOR3F -> in.readVector3(LegacyProtocol.PROTOCOL_113);
            default -> {
            }
        }
    }

    // =========================================================================
    // 5. Sonidos, LevelEvents, Particulas y EntityEvents
    // =========================================================================

    /**
     * Traduce LevelSoundEventPacketV1 (v113 0x19) para el protocolo destino:
     * - En >= 110: valida limites y bloque extraData
     * - En 90..109: mapea la tabla PROTOCOL_110 (0..107) -> PROTOCOL_81 (0..97) sin corrimiento de IDs
     * - En < 90 (0.15.x): convierte sonidos equivalentes en LevelEventPacket (0x16) para que se escuchen!
     */
    public static PacketTranslator.OutboundResult translateLevelSoundEventOutbound(byte[] v113Payload, LegacyProtocol dst) {
        LegacyStream in = LegacyStream.reader(v113Payload);
        int sound = in.readByte();
        float[] pos = in.readVector3(LegacyProtocol.PROTOCOL_113);
        int extraData = in.remaining() > 0 ? in.readVarInt() : -1;
        int pitch = in.remaining() > 0 ? in.readVarInt() : 1;
        boolean isBabyMob = in.remaining() > 0 && in.readBoolean();
        boolean isGlobal = in.remaining() > 0 && in.readBoolean();

        // Puente de sonido hacia LevelEventPacket (0x16) para MCPE 0.15.x (< 90)
        if (dst.isBefore90()) {
            int levelEventId = mapLevelSoundToPre90LevelEvent(sound);
            if (levelEventId < 0) {
                return PacketTranslator.OutboundResult.drop(false);
            }
            LegacyStream out = LegacyStream.writer(24);
            out.writeByte(PacketIdMapper.toLegacy(dst, ProtocolInfo_v113.LEVEL_EVENT_PACKET & 0xFF));
            out.writeShortBE(levelEventId);
            out.writeVector3(dst, pos[0], pos[1], pos[2]);
            out.writeIntBE(Math.max(0, extraData));
            return PacketTranslator.OutboundResult.forward(out.toByteArray());
        }

        int mappedSound = mapLevelSoundId(sound, dst);
        if (mappedSound < 0) {
            return PacketTranslator.OutboundResult.drop(false);
        }

        // Si el extraData codifica un bloque (0..5 o 52 = break/step/place/hit), ajustarlo a bloques validos del destino
        if ((sound >= 0 && sound <= 5) || sound == 52) {
            if (extraData >= 0) {
                int bid = extraData & 0xFF;
                int bmeta = (extraData >> 8) & 0x0F;
                int packed = mapBlockAndMeta(bid, bmeta, dst);
                extraData = ((packed >>> 8) & 0xFF) | ((packed & 0x0F) << 8);
            }
        }

        int legacyId = PacketIdMapper.toLegacy(dst, ProtocolInfo_v113.LEVEL_SOUND_EVENT_PACKET & 0xFF);
        if (legacyId == PacketIdMapper.UNMAPPED) {
            return PacketTranslator.OutboundResult.drop(false);
        }

        LegacyStream out = LegacyStream.writer(32);
        out.writeByte(legacyId & 0xFF);
        out.writeByte(mappedSound);
        out.writeVector3(dst, pos[0], pos[1], pos[2]);
        out.writeVarInt(extraData);
        out.writeVarInt(pitch);
        out.writeBoolean(isBabyMob);
        out.writeBoolean(isGlobal);
        return PacketTranslator.OutboundResult.forward(out.toByteArray());
    }

    private static int mapLevelSoundId(int v113Sound, LegacyProtocol dst) {
        if (!dst.isBefore110()) {
            return v113Sound <= 107 ? v113Sound : 106; // <= SOUND_UNDEFINED
        }
        // Tabla 90..109 (PROTOCOL_81 en MySoft LevelSoundIds.php)
        if (v113Sound <= 61) {
            return v113Sound;
        }
        if (v113Sound == 62 || v113Sound == 63) { // SHULKERBOX_OPEN / CLOSED (1.1)
            return 60 + (v113Sound - 62); // fallback a CHEST_OPEN / CHEST_CLOSED
        }
        if (v113Sound >= 64 && v113Sound <= 97) {
            return v113Sound - 2; // 64..97 -> 62..95 exacto
        }
        if (v113Sound == 106) {
            return 96; // SOUND_DEFAULT
        }
        if (v113Sound == 107) {
            return 97; // SOUND_UNDEFINED
        }
        // 98..105 (Glass/Illager/Camera en 1.1) o > 107
        return -1;
    }

    private static int mapLevelSoundToPre90LevelEvent(int v113Sound) {
        return switch (v113Sound) {
            case 20, 51 -> 1002;           // BOW / SHOOT -> EVENT_SOUND_SHOOT
            case 26, 58, 59 -> 1004;       // FIZZ / EXTINGUISH_FIRE -> EVENT_SOUND_FIZZ
            case 45 -> 1005;               // EXPLODE -> EVENT_SOUND_TNT
            case 60, 61 -> 1003;           // CHEST_OPEN/CLOSED -> EVENT_SOUND_DOOR (suave) o 1000 click
            case 38 -> 1031;               // THROW -> EVENT_SOUND_ITEM_THROWN
            case 70, 71 -> 1030;           // POP / DROP_SLOT -> EVENT_SOUND_ITEM_DROP
            case 76 -> 1032;               // PORTAL -> EVENT_SOUND_PORTAL
            case 89 -> 1018;               // TELEPORT -> EVENT_SOUND_ENDERMAN_TELEPORT
            default -> -1;
        };
    }

    /**
     * Traduce PlaySoundPacket (v113 0x57) para clientes sin PlaySound (< 105) o >= 105.
     */
    public static PacketTranslator.OutboundResult translatePlaySoundOutbound(byte[] v113Payload, LegacyProtocol dst) {
        if (dst.getProtocol() >= 105) {
            int legacyId = PacketIdMapper.toLegacy(dst, ProtocolInfo_v113.PLAY_SOUND_PACKET & 0xFF);
            if (legacyId == PacketIdMapper.UNMAPPED) {
                return PacketTranslator.OutboundResult.drop(false);
            }
            byte[] out = new byte[1 + v113Payload.length];
            out[0] = (byte) (legacyId & 0xFF);
            System.arraycopy(v113Payload, 0, out, 1, v113Payload.length);
            return PacketTranslator.OutboundResult.forward(out);
        }

        // En < 105, traducir sonidos conocidos a LevelEvent (< 90) o LevelSoundEvent (90..104)
        LegacyStream in = LegacyStream.reader(v113Payload);
        String name = in.readString(LegacyProtocol.PROTOCOL_113).toLowerCase(java.util.Locale.ROOT);
        int[] bp = in.readBlockPosition(LegacyProtocol.PROTOCOL_113);
        float x = bp[0] / 8.0f;
        float y = bp[1] / 8.0f;
        float z = bp[2] / 8.0f;

        if (dst.isBefore90()) {
            int lev = mapNamedSoundToLevelEvent(name);
            if (lev < 0) {
                return PacketTranslator.OutboundResult.drop(false);
            }
            LegacyStream out = LegacyStream.writer(20);
            out.writeByte(PacketIdMapper.toLegacy(dst, ProtocolInfo_v113.LEVEL_EVENT_PACKET & 0xFF));
            out.writeShortBE(lev);
            out.writeVector3(dst, x, y, z);
            out.writeIntBE(0);
            return PacketTranslator.OutboundResult.forward(out.toByteArray());
        }
        return PacketTranslator.OutboundResult.drop(false);
    }

    private static int mapNamedSoundToLevelEvent(String name) {
        if (name.contains("explode")) return 1005;
        if (name.contains("bow")) return 1002;
        if (name.contains("door")) return 1003;
        if (name.contains("fizz") || name.contains("extinguish")) return 1004;
        if (name.contains("anvil.break")) return 1020;
        if (name.contains("anvil.use")) return 1021;
        if (name.contains("anvil.land")) return 1022;
        if (name.contains("portal")) return 1032;
        if (name.contains("teleport")) return 1018;
        if (name.contains("click")) return 1000;
        return -1;
    }

    /**
     * Traduce y sanitiza LevelEventPacket (0x1a -> 0x16 en < 90):
     * - Ajusta bloques en EVENT_PARTICLE_DESTROY (2001) y EVENT_PARTICLE_PUNCH_BLOCK (2014)
     * - Filtra IDs de particulas y eventos no soportados por la version del cliente
     */
    public static PacketTranslator.OutboundResult translateLevelEventOutbound(byte[] v113Payload, LegacyProtocol dst) {
        LegacyStream in = LegacyStream.reader(v113Payload);
        int evid = in.readVarInt();
        float[] pos = in.readVector3(LegacyProtocol.PROTOCOL_113);
        int data = in.readVarInt();

        // Validar rango de particulas (0x4000 | particleId)
        if ((evid & EVENT_ADD_PARTICLE_MASK) != 0) {
            int pid = evid & ~EVENT_ADD_PARTICLE_MASK;
            if (dst.isBefore90() && pid > 38) {
                return PacketTranslator.OutboundResult.drop(false);
            }
            if (dst.isBefore110() && pid > 45) {
                return PacketTranslator.OutboundResult.drop(false);
            }
            if (pid > 47) {
                return PacketTranslator.OutboundResult.drop(false);
            }
        } else if (dst.isBefore90()) {
            // 0.15.x soporta sonidos 1000..1044, particulas 2000..2015, clima 3001..3004 y eventos 3500..3508
            if ((evid >= 1050 && evid < 2000) || (evid >= 2016 && evid < 3000) || evid > 3508) {
                return PacketTranslator.OutboundResult.drop(false);
            }
        } else if (dst.isBefore110()) {
            if ((evid >= 1052 && evid < 2000) || (evid >= 2017 && evid < 3000) || evid > 3510) {
                return PacketTranslator.OutboundResult.drop(false);
            }
        }

        // Remapear bloque en particulas de destruccion / golpeo de bloque
        if (evid == 2001) { // EVENT_PARTICLE_DESTROY: blockId | (damage << 8)
            int packed = mapBlockAndMeta(data & 0xFF, (data >> 8) & 0x0F, dst);
            data = ((packed >>> 8) & 0xFF) | ((packed & 0x0F) << 8);
        } else if (evid == 2014) { // EVENT_PARTICLE_PUNCH_BLOCK: blockId | (damage << 8) | (face << 16)
            int face = (data >> 16) & 0xFF;
            int packed = mapBlockAndMeta(data & 0xFF, (data >> 8) & 0x0F, dst);
            data = ((packed >>> 8) & 0xFF) | ((packed & 0x0F) << 8) | (face << 16);
        }

        int legacyId = PacketIdMapper.toLegacy(dst, ProtocolInfo_v113.LEVEL_EVENT_PACKET & 0xFF);
        if (legacyId == PacketIdMapper.UNMAPPED) {
            return PacketTranslator.OutboundResult.drop(false);
        }

        LegacyStream out = LegacyStream.writer(24);
        out.writeByte(legacyId & 0xFF);
        if (dst.isBefore90()) {
            out.writeShortBE(evid);
            out.writeVector3(dst, pos[0], pos[1], pos[2]);
            out.writeIntBE(data);
        } else {
            out.writeVarInt(evid);
            out.writeVector3(dst, pos[0], pos[1], pos[2]);
            out.writeVarInt(data);
        }
        return PacketTranslator.OutboundResult.forward(out.toByteArray());
    }

    /**
     * Filtra y traduce EntityEventPacket (0x1c -> 0x18) evitando IDs de eventos desconocidos en 0.15.x.
     */
    public static PacketTranslator.OutboundResult translateEntityEventOutbound(byte[] v113Payload, LegacyProtocol dst) {
        LegacyStream in = LegacyStream.reader(v113Payload);
        long eid = in.readEntityRuntimeId(LegacyProtocol.PROTOCOL_113);
        int event = in.readByte();
        int data = in.remaining() > 0 ? in.readVarInt() : 0;

        if (dst.isBefore90()) {
            if (event == 28 || (event >= 34 && event <= 37) || event >= 40) {
                return PacketTranslator.OutboundResult.drop(false);
            }
        } else if (dst.isBefore110() && event > 64) {
            return PacketTranslator.OutboundResult.drop(false);
        }

        int legacyId = PacketIdMapper.toLegacy(dst, ProtocolInfo_v113.ENTITY_EVENT_PACKET & 0xFF);
        if (legacyId == PacketIdMapper.UNMAPPED) {
            return PacketTranslator.OutboundResult.drop(false);
        }

        LegacyStream out = LegacyStream.writer(16);
        out.writeByte(legacyId & 0xFF);
        out.writeEntityRuntimeId(dst, eid);
        out.writeByte(event);
        if (!dst.isBefore90()) {
            out.writeVarInt(data);
        }
        return PacketTranslator.OutboundResult.forward(out.toByteArray());
    }

    // =========================================================================
    // 6. Normalizacion de Skins RGBA (128x128 -> 64x64 para <= 0.16)
    // =========================================================================

    /**
     * Garantiza que los bytes RGBA de la skin tengan un tamano valido para clientes legados:
     * En < 100 (0.15.x - 0.16.x) solo existen 64x32 (8192 B) y 64x64 (16384 B).
     * Un skin HD 128x128 (65536 B) desbordaria writeShortBE en 0.15.x y crashearia el cliente.
     */
    public static byte[] normalizeSkinData(byte[] skinData, LegacyProtocol dst) {
        if (skinData == null || skinData.length == 0) {
            return defaultBlankSkin(16384);
        }
        if (dst.getProtocol() >= 100) {
            if (skinData.length == 8192 || skinData.length == 16384 || skinData.length == 65536) {
                return skinData;
            }
            return resizeRgbaNearest(skinData, 64, 64);
        }
        if (skinData.length == 8192 || skinData.length == 16384) {
            return skinData;
        }
        if (skinData.length == 32768) {
            return downsampleRgba(skinData, 128, 64, 64, 32);
        }
        if (skinData.length == 65536) {
            return downsampleRgba(skinData, 128, 128, 64, 64);
        }
        return resizeRgbaNearest(skinData, 64, 64);
    }

    private static byte[] downsampleRgba(byte[] src, int srcW, int srcH, int dstW, int dstH) {
        byte[] dst = new byte[dstW * dstH * 4];
        for (int y = 0; y < dstH; y++) {
            int sy = (y * srcH) / dstH;
            for (int x = 0; x < dstW; x++) {
                int sx = (x * srcW) / dstW;
                int sIdx = (sy * srcW + sx) * 4;
                int dIdx = (y * dstW + x) * 4;
                if (sIdx + 3 < src.length) {
                    dst[dIdx] = src[sIdx];
                    dst[dIdx + 1] = src[sIdx + 1];
                    dst[dIdx + 2] = src[sIdx + 2];
                    dst[dIdx + 3] = src[sIdx + 3];
                }
            }
        }
        return dst;
    }

    private static byte[] resizeRgbaNearest(byte[] src, int dstW, int dstH) {
        int pixels = src.length / 4;
        if (pixels <= 0) {
            return defaultBlankSkin(dstW * dstH * 4);
        }
        int srcW = (int) Math.max(1, Math.round(Math.sqrt(pixels)));
        int srcH = Math.max(1, pixels / srcW);
        return downsampleRgba(src, srcW, srcH, dstW, dstH);
    }

    private static byte[] defaultBlankSkin(int size) {
        byte[] blank = new byte[size];
        Arrays.fill(blank, (byte) 0xFF);
        return blank;
    }
}
