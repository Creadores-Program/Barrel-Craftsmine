package com.craftsmine.nkm;

import cn.nukkit.GameVersion;
import cn.nukkit.entity.data.Skin;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.nbt.tag.LongTag;
import cn.nukkit.nbt.tag.ShortTag;
import cn.nukkit.network.protocol.AdventureSettingsPacket;
import cn.nukkit.network.protocol.MovePlayerPacket;
import cn.nukkit.network.protocol.PlayerListPacket;
import cn.nukkit.network.protocol.RequestChunkRadiusPacket;
import cn.nukkit.network.protocol.TextPacket;
import cn.nukkit.network.protocol.v113.ProtocolInfo_v113;
import cn.nukkit.network.protocol.v113.RemoveBlockPacket_v113;
import cn.nukkit.network.protocol.v113.UseItemPacket_v113;
import cn.nukkit.utils.BinaryStream;
import com.craftsmine.nkm.protocol.BatchCodec;
import com.craftsmine.nkm.protocol.ChunkTranslator;
import com.craftsmine.nkm.protocol.LegacyContentMapper;
import com.craftsmine.nkm.protocol.LegacyNbtConverter;
import com.craftsmine.nkm.protocol.LegacyProtocol;
import com.craftsmine.nkm.protocol.LegacyStream;
import com.craftsmine.nkm.protocol.PacketIdMapper;
import com.craftsmine.nkm.protocol.PacketTranslator;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.zip.DataFormatException;

import static org.junit.jupiter.api.Assertions.*;

class ProtocolBridgeTest {

    @Test
    void resolvesProtocols80Through113Correctly() {
        LegacyProtocol p80 = LegacyProtocol.resolve(80);
        assertEquals(80, p80.getProtocol());
        assertEquals(7, p80.getRakNetVersion());
        assertTrue(p80.isBefore90());
        assertTrue(p80.needsTranslation());

        LegacyProtocol p84 = LegacyProtocol.resolve(84);
        assertEquals(84, p84.getProtocol());
        assertEquals(7, p84.getRakNetVersion());
        assertTrue(p84.isBefore90());

        LegacyProtocol p91 = LegacyProtocol.resolve(91);
        assertEquals(91, p91.getProtocol());
        assertEquals(8, p91.getRakNetVersion());
        assertFalse(p91.isBefore90());
        assertTrue(p91.isBefore92());

        LegacyProtocol p105 = LegacyProtocol.resolve(105);
        assertEquals(105, p105.getProtocol());
        assertFalse(p105.isBefore92());
        assertTrue(p105.isBefore110());

        LegacyProtocol p113 = LegacyProtocol.resolve(113);
        assertFalse(p113.needsTranslation());
    }

    @Test
    void mapsPacketIdsBidirectionallyAcrossFamilies() {
        // 0.15.x (80-84): StartGame is 0x09 -> v113 0x0b
        assertEquals(0x0b, PacketIdMapper.toV113(LegacyProtocol.PROTOCOL_80, 0x09));
        assertEquals(0x09, PacketIdMapper.toLegacy(LegacyProtocol.PROTOCOL_84, 0x0b));

        // 0.15.x: ChunkRadiusUpdated is 0x3e -> v113 0x46
        assertEquals(0x46, PacketIdMapper.toV113(LegacyProtocol.PROTOCOL_81, 0x3e));
        assertEquals(0x3e, PacketIdMapper.toLegacy(LegacyProtocol.PROTOCOL_81, 0x46));

        // 1.1.0.0 (110): TakeItemEntity is 0x11 -> v113 0x11
        assertEquals(0x11, PacketIdMapper.toV113(LegacyProtocol.PROTOCOL_110, 0x11));

        // 1.0.4 (102): ResourcePacksInfo is 0x07 -> v113 0x06
        assertEquals(0x06, PacketIdMapper.toV113(LegacyProtocol.PROTOCOL_102, 0x07));
    }

    @Test
    void normalizesLegacy01510LoginPacketInsideLiteral06BatchAndDecodesWithNukkitMot() throws DataFormatException {
        UUID playerUuid = UUID.randomUUID();
        String chainPayloadJson = "{\"extraData\":{\"displayName\":\"Craftsmine015\",\"identity\":\""
                + playerUuid + "\",\"XUID\":\"\"}}";
        String clientPayloadJson = "{\"ClientRandomId\":123456789,\"SkinId\":\"Standard_Custom\",\"SkinData\":\""
                + Base64.getEncoder().encodeToString(new byte[Skin.SINGLE_SKIN_SIZE]) + "\"}";

        String headerB64 = Base64.getUrlEncoder().withoutPadding().encodeToString("{\"alg\":\"none\"}".getBytes(StandardCharsets.UTF_8));
        String chainJwt = headerB64 + "."
                + Base64.getUrlEncoder().withoutPadding().encodeToString(chainPayloadJson.getBytes(StandardCharsets.UTF_8))
                + ".sig";
        String clientJwt = headerB64 + "."
                + Base64.getUrlEncoder().withoutPadding().encodeToString(clientPayloadJson.getBytes(StandardCharsets.UTF_8))
                + ".sig";

        byte[] chainJsonBytes = ("{\"chain\":[\"" + chainJwt + "\"]}").getBytes(StandardCharsets.UTF_8);
        byte[] clientJwtBytes = clientJwt.getBytes(StandardCharsets.UTF_8);

        LegacyStream payloadStream = LegacyStream.writer(chainJsonBytes.length + clientJwtBytes.length + 16);
        payloadStream.writeIntLE(chainJsonBytes.length);
        payloadStream.writeBytes(chainJsonBytes);
        payloadStream.writeIntLE(clientJwtBytes.length);
        payloadStream.writeBytes(clientJwtBytes);
        byte[] rawConnectionPayload = payloadStream.toByteArray();
        byte[] compressedPayload = BatchCodec.deflateZlib(rawConnectionPayload, 6);

        LegacyStream legacyLogin = LegacyStream.writer(compressedPayload.length + 16);
        legacyLogin.writeByte(0x01);
        legacyLogin.writeIntBE(84); // 0.15.10 protocol 84
        legacyLogin.writeByteArray(LegacyProtocol.PROTOCOL_84, compressedPayload);
        byte[] legacyLoginBytes = legacyLogin.toByteArray();

        // 0.15.10 puede enviar el LoginPacket dentro de un BatchPacket literal (0x06 + int32(compLen) + zlib)
        byte[] literalBatchBody = BatchCodec.encodeRakNetBatchBody(
                LegacyProtocol.PROTOCOL_84, List.of(legacyLoginBytes), 6
        );
        List<byte[]> decodedPreLogin = BatchCodec.decodeRakNetBatchBody(literalBatchBody, 7, null, 2 * 1024 * 1024);
        assertNotNull(decodedPreLogin);
        assertEquals(1, decodedPreLogin.size());
        assertArrayEquals(legacyLoginBytes, decodedPreLogin.get(0));

        PacketTranslator.LoginSniffResult sniff = PacketTranslator.tryNormalizeLogin(decodedPreLogin.get(0), 80, 113);
        assertNotNull(sniff);
        assertEquals(84, sniff.rawProtocol());
        assertEquals("Craftsmine015", sniff.username());

        // Verificar con BinaryStream real de Nukkit-MOT la estructura canonica v113 (sin requerir Server booted)
        BinaryStream nukkitStream = new BinaryStream(sniff.canonicalV113LoginPacket(), 1);
        assertEquals(113, nukkitStream.getInt());
        assertEquals(0, nukkitStream.getByte()); // gameEdition v1.1
        byte[] decodedConnectionPayload = nukkitStream.getByteArray();
        assertArrayEquals(rawConnectionPayload, decodedConnectionPayload);

        BinaryStream connStream = new BinaryStream(decodedConnectionPayload, 0);
        byte[] outChain = connStream.get(connStream.getLInt());
        byte[] outClientJwt = connStream.get(connStream.getLInt());
        assertArrayEquals(chainJsonBytes, outChain);
        assertArrayEquals(clientJwtBytes, outClientJwt);
    }

    @Test
    void bypassesModern113LoginPacket() throws DataFormatException {
        LegacyStream modernLogin = LegacyStream.writer(32);
        modernLogin.writeByte(0x01);
        modernLogin.writeIntBE(113);
        modernLogin.writeByte(0);
        modernLogin.writeByteArray(LegacyProtocol.PROTOCOL_113, "modern".getBytes(StandardCharsets.UTF_8));

        assertNull(PacketTranslator.tryNormalizeLogin(modernLogin.toByteArray(), 80, 113));
    }

    @Test
    void bypassesResourcePacksFor015ClientAndRequestsSyntheticComplete() {
        byte[] fakeResourcePacksInfo = new byte[]{ProtocolInfo_v113.RESOURCE_PACKS_INFO_PACKET, 0, 0, 0, 0, 0};
        PacketTranslator.OutboundResult res = PacketTranslator.translateOutbound(
                fakeResourcePacksInfo, LegacyProtocol.PROTOCOL_84, true
        );
        assertTrue(res.suppress());
        assertTrue(res.synthesizeResourceComplete());

        byte[] synthetic = PacketTranslator.buildSyntheticResourcePackCompleteV113();
        assertEquals(ProtocolInfo_v113.RESOURCE_PACK_CLIENT_RESPONSE_PACKET & 0xFF, synthetic[0] & 0xFF);
        assertEquals(4, synthetic[1]);
    }

    @Test
    void translates01510InboundPacketsAndDecodesWithRealNukkitMotCodecs() {
        GameVersion v110 = GameVersion.byProtocol(113, false);

        // 1) UseItemPacket (0.15.10 pid 0x1f -> v113 0x23)
        LegacyStream useItem015 = LegacyStream.writer(64);
        useItem015.writeByte(0x1f);
        useItem015.writeBlockPosition(LegacyProtocol.PROTOCOL_84, 12, 64, -8);
        useItem015.writeByte(1); // face
        useItem015.writeVector3(LegacyProtocol.PROTOCOL_84, 0.5f, 1.0f, 0.25f);
        useItem015.writeVector3(LegacyProtocol.PROTOCOL_84, 12.5f, 65.62f, -7.5f);
        useItem015.writeIntBE(3); // slot
        useItem015.writeSlot(LegacyProtocol.PROTOCOL_84, new LegacyStream.LegacySlot(1, 0, 16, new byte[0], new String[0], new String[0]));

        PacketTranslator.InboundResult useItemCanon = PacketTranslator.translateInbound(useItem015.toByteArray(), LegacyProtocol.PROTOCOL_84);
        assertFalse(useItemCanon.suppress());
        assertNotNull(useItemCanon.canonicalPacket());
        UseItemPacket_v113 nukkitUseItem = new UseItemPacket_v113();
        nukkitUseItem.protocol = 113;
        nukkitUseItem.gameVersion = v110;
        nukkitUseItem.setBuffer(useItemCanon.canonicalPacket(), 1);
        nukkitUseItem.decode();
        assertEquals(12, nukkitUseItem.x);
        assertEquals(64, nukkitUseItem.y);
        assertEquals(-8, nukkitUseItem.z);
        assertEquals(1, nukkitUseItem.face);
        assertEquals(3, nukkitUseItem.slot);
        assertEquals(1, nukkitUseItem.item.getId());
        assertEquals(16, nukkitUseItem.item.getCount());

        // 2) MovePlayerPacket MODE_TELEPORT (0.15.10 pid 0x10 -> v113 0x13)
        LegacyStream move015 = LegacyStream.writer(64);
        move015.writeByte(0x10);
        move015.writeEntityRuntimeId(LegacyProtocol.PROTOCOL_84, 42L);
        move015.writeVector3(LegacyProtocol.PROTOCOL_84, 100.5f, 70.0f, -50.25f);
        move015.writeFloatBE(90.0f);  // yaw
        move015.writeFloatBE(90.0f);  // headYaw
        move015.writeFloatBE(15.0f);  // pitch
        move015.writeByte(MovePlayerPacket.MODE_TELEPORT);
        move015.writeBoolean(true);

        PacketTranslator.InboundResult moveCanon = PacketTranslator.translateInbound(move015.toByteArray(), LegacyProtocol.PROTOCOL_84);
        assertFalse(moveCanon.suppress());
        MovePlayerPacket nukkitMove = new MovePlayerPacket();
        nukkitMove.protocol = 113;
        nukkitMove.gameVersion = v110;
        nukkitMove.setBuffer(moveCanon.canonicalPacket(), 1);
        nukkitMove.decode();
        assertEquals(42L, nukkitMove.eid);
        assertEquals(MovePlayerPacket.MODE_TELEPORT, nukkitMove.mode);
        assertTrue(nukkitMove.onGround);

        // 3) RemoveBlockPacket (0.15.10 pid 0x12 -> v113 0x15)
        LegacyStream rm015 = LegacyStream.writer(32);
        rm015.writeByte(0x12);
        rm015.writeEntityRuntimeId(LegacyProtocol.PROTOCOL_84, 42L);
        rm015.writeIntBE(5);
        rm015.writeIntBE(-9);
        rm015.writeByte(63);
        PacketTranslator.InboundResult rmCanon = PacketTranslator.translateInbound(rm015.toByteArray(), LegacyProtocol.PROTOCOL_84);
        RemoveBlockPacket_v113 nukkitRm = new RemoveBlockPacket_v113();
        nukkitRm.protocol = 113;
        nukkitRm.gameVersion = v110;
        nukkitRm.setBuffer(rmCanon.canonicalPacket(), 1);
        nukkitRm.decode();
        assertEquals(5, nukkitRm.x);
        assertEquals(63, nukkitRm.y);
        assertEquals(-9, nukkitRm.z);

        // 4) RequestChunkRadiusPacket (0.15.10 pid 0x3d -> v113 0x45)
        LegacyStream radius015 = LegacyStream.writer(8);
        radius015.writeByte(0x3d);
        radius015.writeIntBE(8);
        PacketTranslator.InboundResult radiusCanon = PacketTranslator.translateInbound(radius015.toByteArray(), LegacyProtocol.PROTOCOL_84);
        RequestChunkRadiusPacket nukkitRadius = new RequestChunkRadiusPacket();
        nukkitRadius.protocol = 113;
        nukkitRadius.gameVersion = v110;
        nukkitRadius.setBuffer(radiusCanon.canonicalPacket(), 1);
        nukkitRadius.decode();
        assertEquals(8, nukkitRadius.radius);

        // 5) AdventureSettingsPacket (0.15.10 pid 0x31 -> v113 0x37)
        LegacyStream adv015 = LegacyStream.writer(16);
        adv015.writeByte(0x31);
        adv015.writeIntBE(0x20);
        adv015.writeIntBE(1);
        adv015.writeIntBE(1);
        PacketTranslator.InboundResult advCanon = PacketTranslator.translateInbound(adv015.toByteArray(), LegacyProtocol.PROTOCOL_84);
        AdventureSettingsPacket nukkitAdv = new AdventureSettingsPacket();
        nukkitAdv.protocol = 113;
        nukkitAdv.gameVersion = v110;
        nukkitAdv.setBuffer(advCanon.canonicalPacket(), 1);
        nukkitAdv.decode();
        assertEquals(0x20L, nukkitAdv.flags);
        assertEquals(1L, nukkitAdv.commandPermission);
    }

    @Test
    void translatesOutboundTextPopupAttributesPlayerListAndCraftingDataFor015() {
        // 1) TextPacket TYPE_POPUP (formato v113: type + message + UnsignedVarInt(paramCount) + params[])
        LegacyStream textV113 = LegacyStream.writer(64);
        textV113.writeByte(ProtocolInfo_v113.TEXT_PACKET & 0xFF);
        textV113.writeByte(TextPacket.TYPE_POPUP);
        textV113.writeString(LegacyProtocol.PROTOCOL_113, "Bienvenido");
        textV113.writeUnsignedVarInt(1);
        textV113.writeString(LegacyProtocol.PROTOCOL_113, "Titulo");
        PacketTranslator.OutboundResult textOut = PacketTranslator.translateOutbound(textV113.toByteArray(), LegacyProtocol.PROTOCOL_84, true);
        assertFalse(textOut.suppress());
        LegacyStream tr = LegacyStream.reader(textOut.legacyPacket());
        assertEquals(0x07, tr.readByte());
        assertEquals(TextPacket.TYPE_POPUP, tr.readByte());
        assertEquals("Titulo", tr.readString(LegacyProtocol.PROTOCOL_84));
        assertEquals("Bienvenido", tr.readString(LegacyProtocol.PROTOCOL_84));

        // 2) UpdateAttributesPacket: EntityUniqueId (VarLong zigzag)
        LegacyStream attrV113 = LegacyStream.writer(64);
        attrV113.writeByte(ProtocolInfo_v113.UPDATE_ATTRIBUTES_PACKET & 0xFF);
        attrV113.writeEntityUniqueId(LegacyProtocol.PROTOCOL_113, 1L);
        attrV113.writeUnsignedVarInt(1);
        attrV113.writeFloatLE(0f);
        attrV113.writeFloatLE(20f);
        attrV113.writeFloatLE(20f);
        attrV113.writeFloatLE(20f);
        attrV113.writeString(LegacyProtocol.PROTOCOL_113, "minecraft:health");
        PacketTranslator.OutboundResult attrOut = PacketTranslator.translateOutbound(attrV113.toByteArray(), LegacyProtocol.PROTOCOL_84, true);
        LegacyStream ar = LegacyStream.reader(attrOut.legacyPacket());
        assertEquals(0x1a, ar.readByte());
        assertEquals(1L, ar.readEntityRuntimeId(LegacyProtocol.PROTOCOL_84));
        assertEquals(1, ar.readShortBE());
        assertEquals(0f, ar.readFloatBE());
        assertEquals(20f, ar.readFloatBE());
        assertEquals(20f, ar.readFloatBE());
        assertEquals("minecraft:health", ar.readString(LegacyProtocol.PROTOCOL_84));

        // 3) PlayerListPacket (2 entradas con geometryName + geometryData + xboxUserId de Nukkit-MOT V1_1_0)
        UUID u1 = UUID.randomUUID();
        UUID u2 = UUID.randomUUID();
        byte[] skinBytes = new byte[Skin.SINGLE_SKIN_SIZE];
        LegacyStream plV113 = LegacyStream.writer(16384);
        plV113.writeByte(ProtocolInfo_v113.PLAYER_LIST_PACKET & 0xFF);
        plV113.writeByte(PlayerListPacket.TYPE_ADD);
        plV113.writeUnsignedVarInt(2);
        plV113.writeUUID(u1);
        plV113.writeEntityUniqueId(LegacyProtocol.PROTOCOL_113, 10L);
        plV113.writeString(LegacyProtocol.PROTOCOL_113, "JugadorUno");
        plV113.writeString(LegacyProtocol.PROTOCOL_113, "Standard_Custom");
        plV113.writeByteArray(LegacyProtocol.PROTOCOL_113, skinBytes);
        plV113.writeString(LegacyProtocol.PROTOCOL_113, "geometry.humanoid.custom");
        plV113.writeString(LegacyProtocol.PROTOCOL_113, "{}");
        plV113.writeString(LegacyProtocol.PROTOCOL_113, "111");
        plV113.writeUUID(u2);
        plV113.writeEntityUniqueId(LegacyProtocol.PROTOCOL_113, 11L);
        plV113.writeString(LegacyProtocol.PROTOCOL_113, "JugadorDos");
        plV113.writeString(LegacyProtocol.PROTOCOL_113, "Standard_CustomSlim");
        plV113.writeByteArray(LegacyProtocol.PROTOCOL_113, skinBytes);
        plV113.writeString(LegacyProtocol.PROTOCOL_113, "geometry.humanoid.customSlim");
        plV113.writeString(LegacyProtocol.PROTOCOL_113, "{}");
        plV113.writeString(LegacyProtocol.PROTOCOL_113, "222");

        PacketTranslator.OutboundResult plOut = PacketTranslator.translateOutbound(plV113.toByteArray(), LegacyProtocol.PROTOCOL_84, true);
        LegacyStream plr = LegacyStream.reader(plOut.legacyPacket());
        assertEquals(0x38, plr.readByte());
        assertEquals(PlayerListPacket.TYPE_ADD, plr.readByte());
        assertEquals(2, plr.readIntBE());
        assertEquals(u1, plr.readUUID());
        assertEquals(10L, plr.readEntityUniqueId(LegacyProtocol.PROTOCOL_84));
        assertEquals("JugadorUno", plr.readString(LegacyProtocol.PROTOCOL_84));
        assertEquals("Standard_Custom", plr.readString(LegacyProtocol.PROTOCOL_84));
        int s1Len = plr.readShortBE();
        assertEquals(Skin.SINGLE_SKIN_SIZE, s1Len);
        plr.readBytes(s1Len);
        assertEquals(u2, plr.readUUID());
        assertEquals(11L, plr.readEntityUniqueId(LegacyProtocol.PROTOCOL_84));
        assertEquals("JugadorDos", plr.readString(LegacyProtocol.PROTOCOL_84));
        assertEquals("Standard_CustomSlim", plr.readString(LegacyProtocol.PROTOCOL_84));

        // 4) CraftingDataPacket (v113 -> 0.15.10)
        LegacyStream craftV113 = LegacyStream.writer(128);
        craftV113.writeByte(ProtocolInfo_v113.CRAFTING_DATA_PACKET & 0xFF);
        craftV113.writeUnsignedVarInt(1); // 1 recipe
        craftV113.writeVarInt(2); // ENTRY_FURNACE
        craftV113.writeVarInt(15); // iron ore
        craftV113.writeSlot(LegacyProtocol.PROTOCOL_113, new LegacyStream.LegacySlot(265, 0, 1, new byte[0], new String[0], new String[0]));
        craftV113.writeBoolean(true);
        PacketTranslator.OutboundResult craftOut = PacketTranslator.translateOutbound(craftV113.toByteArray(), LegacyProtocol.PROTOCOL_84, true);
        LegacyStream cr = LegacyStream.reader(craftOut.legacyPacket());
        assertEquals(0x2f, cr.readByte());
        assertEquals(1, cr.readIntBE());
        assertEquals(2, cr.readIntBE());
        int entryLen = cr.readIntBE();
        assertTrue(entryLen > 0);
        assertEquals(15, cr.readIntBE());
        LegacyStream.LegacySlot resSlot = cr.readSlot(LegacyProtocol.PROTOCOL_84);
        assertEquals(265, resSlot.id());
        assertEquals(1, resSlot.count());
        assertTrue(cr.readBoolean());

        // 5) ContainerOpenPacket (v113 -> 0.15.10 y 1.0.5)
        LegacyStream openV113 = LegacyStream.writer(32);
        openV113.writeByte(ProtocolInfo_v113.CONTAINER_OPEN_PACKET & 0xFF);
        openV113.writeByte(2); // windowId
        openV113.writeByte(0); // CONTAINER
        openV113.writeBlockPosition(LegacyProtocol.PROTOCOL_113, 10, 64, 20);
        openV113.writeEntityUniqueId(LegacyProtocol.PROTOCOL_113, -1L);
        PacketTranslator.OutboundResult open015 = PacketTranslator.translateOutbound(openV113.toByteArray(), LegacyProtocol.PROTOCOL_84, true);
        LegacyStream or015 = LegacyStream.reader(open015.legacyPacket());
        assertEquals(0x2a, or015.readByte());
        assertEquals(2, or015.readByte());
        assertEquals(0, or015.readByte());
        assertEquals(27, or015.readShortBE());
        assertEquals(10, or015.readIntBE());
        assertEquals(64, or015.readIntBE());
        assertEquals(20, or015.readIntBE());

        PacketTranslator.OutboundResult open105 = PacketTranslator.translateOutbound(openV113.toByteArray(), LegacyProtocol.PROTOCOL_105, false);
        LegacyStream or105 = LegacyStream.reader(open105.legacyPacket());
        assertEquals(0x31, or105.readByte());
        assertEquals(2, or105.readByte());
        assertEquals(0, or105.readByte());
        assertEquals(27, or105.readVarInt());
        assertArrayEquals(new int[]{10, 64, 20}, or105.readBlockPosition(LegacyProtocol.PROTOCOL_105));
        assertEquals(-1L, or105.readEntityUniqueId(LegacyProtocol.PROTOCOL_105));
    }

    @Test
    void convertsV113ChunkToPre92ColumnFormat() {
        LegacyStream nukkitStyle = LegacyStream.writer(16384);
        nukkitStyle.writeVarInt(2);
        nukkitStyle.writeVarInt(-3);
        byte[] payloadBytes = new byte[1 + 1 + 4096 + 2048 + 2048 + 2048 + 512 + 256 + 1 + 1];
        payloadBytes[0] = 1; // 1 subchunk
        payloadBytes[1] = 0; // version 0
        Arrays.fill(payloadBytes, 2, 2 + 4096, (byte) 7); // bedrock
        nukkitStyle.writeByteArray(LegacyProtocol.PROTOCOL_113, payloadBytes);

        byte[] p80Chunk = ChunkTranslator.translateOutbound(nukkitStyle.toByteArray(), LegacyProtocol.PROTOCOL_80);
        LegacyStream outReader = LegacyStream.reader(p80Chunk);
        assertEquals(2, outReader.readIntBE());
        assertEquals(-3, outReader.readIntBE());
        assertEquals(0, outReader.readByte()); // order = 0 (COLUMNS)
        byte[] legacyCol = outReader.readByteArray(LegacyProtocol.PROTOCOL_80);
        assertTrue(legacyCol.length >= 83204);
        assertEquals(7, legacyCol[0] & 0xFF);
    }

    @Test
    void packsAndUnpacksBatchesAcrossProtocols84And105And113() throws DataFormatException {
        List<byte[]> subs = List.of(new byte[]{0x09, 0x01, 0x02}, new byte[]{0x0a, 0x05});

        // 0.15.10 (protocolo 84) batch body: 0x06 + int32(compLen) + zlib(int32(len)+pkt)
        byte[] body84 = BatchCodec.encodeRakNetBatchBody(LegacyProtocol.PROTOCOL_84, subs, 6);
        assertEquals(0x06, body84[0] & 0xFF);
        List<byte[]> unpacked84 = BatchCodec.decodeRakNetBatchBody(body84, 7, LegacyProtocol.PROTOCOL_84, 1024 * 1024);
        assertEquals(2, unpacked84.size());
        assertArrayEquals(subs.get(0), unpacked84.get(0));
        assertArrayEquals(subs.get(1), unpacked84.get(1));

        // 1.0.5 (protocolo 105) batch body: 0x06 + UnsignedVarInt(compLen) + zlib(UnsignedVarInt(len)+pkt)
        byte[] body105 = BatchCodec.encodeRakNetBatchBody(LegacyProtocol.PROTOCOL_105, subs, 6);
        assertEquals(0x06, body105[0] & 0xFF);
        List<byte[]> unpacked105 = BatchCodec.decodeRakNetBatchBody(body105, 8, LegacyProtocol.PROTOCOL_105, 1024 * 1024);
        assertEquals(2, unpacked105.size());
        assertArrayEquals(subs.get(0), unpacked105.get(0));
        assertArrayEquals(subs.get(1), unpacked105.get(1));

        // 1.1.0+ canonical body: zlib(varint(len)+pk)
        byte[] body113 = BatchCodec.encodeCanonicalV113Body(subs, 6);
        List<byte[]> unpacked113 = BatchCodec.decodeRakNetBatchBody(body113, 8, LegacyProtocol.PROTOCOL_113, 1024 * 1024);
        assertEquals(2, unpacked113.size());
        assertArrayEquals(subs.get(1), unpacked113.get(1));
    }

    @Test
    void filtersCreativeContentAndHandlesNetwork0xFFFNSlotsFor01510() throws IOException {
        CompoundTag bookTag = new CompoundTag();
        ListTag<CompoundTag> enchList = new ListTag<>("ench");
        enchList.add(new CompoundTag().putShort("id", 0).putShort("lvl", 4));
        enchList.add(new CompoundTag().putShort("id", 70).putShort("lvl", 1)); // Mending (> 24 en 0.15)
        bookTag.putList(enchList);
        byte[] networkNbt = NBTIO.write(bookTag, ByteOrder.LITTLE_ENDIAN, true);

        LegacyStream pk = LegacyStream.writer(512);
        pk.writeByte(ProtocolInfo_v113.CONTAINER_SET_CONTENT_PACKET & 0xFF);
        pk.writeUnsignedVarInt(0x79); // SPECIAL_CREATIVE
        pk.writeEntityUniqueId(LegacyProtocol.PROTOCOL_113, 1L);
        pk.writeUnsignedVarInt(7); // 7 items

        // 1) Stone (valido)
        pk.writeSlot(LegacyProtocol.PROTOCOL_113, new LegacyStream.LegacySlot(1, 0, 1, new byte[0], new String[0], new String[0]));
        // 2) Fireworks 401 (no existe en 0.15 -> filtrar)
        pk.writeSlot(LegacyProtocol.PROTOCOL_113, new LegacyStream.LegacySlot(401, 0, 1, new byte[0], new String[0], new String[0]));
        // 3) Firework Star 402 (filtrar)
        pk.writeSlot(LegacyProtocol.PROTOCOL_113, new LegacyStream.LegacySlot(402, 0, 1, new byte[0], new String[0], new String[0]));
        // 4) Banner 446 (filtrar en creativo)
        pk.writeSlot(LegacyProtocol.PROTOCOL_113, new LegacyStream.LegacySlot(446, 0, 1, new byte[0], new String[0], new String[0]));
        // 5) Shulker spawn egg 383:54 (filtrar)
        pk.writeSlot(LegacyProtocol.PROTOCOL_113, new LegacyStream.LegacySlot(383, 54, 1, new byte[0], new String[0], new String[0]));
        // 6) Sea Lantern 169 (no existe en 0.15 creativo -> filtrar)
        pk.writeSlot(LegacyProtocol.PROTOCOL_113, new LegacyStream.LegacySlot(169, 0, 1, new byte[0], new String[0], new String[0]));
        // 7) Enchanted Book 403 con formato 0xFFFF NetworkLittleEndianNBT
        pk.writeVarInt(403);
        pk.writeVarInt(1); // meta=0, count=1
        pk.writeShortLE(0xFFFF);
        pk.writeByte(1); // nbt version / tag count
        pk.writeBytes(networkNbt);
        pk.writeVarInt(0); // canPlaceOn
        pk.writeVarInt(0); // canDestroy

        pk.writeUnsignedVarInt(0); // hotbar

        PacketTranslator.OutboundResult res = PacketTranslator.translateOutbound(pk.toByteArray(), LegacyProtocol.PROTOCOL_84, true);
        assertFalse(res.suppress());
        LegacyStream out = LegacyStream.reader(res.legacyPacket());
        assertEquals(0x2e, out.readByte());
        assertEquals(0x79, out.readByte());
        assertEquals(2, out.readShortBE()); // solo Stone y Enchanted Book permanecen

        LegacyStream.LegacySlot s1 = out.readSlot(LegacyProtocol.PROTOCOL_84);
        assertEquals(1, s1.id());
        LegacyStream.LegacySlot s2 = out.readSlot(LegacyProtocol.PROTOCOL_84);
        assertEquals(403, s2.id());
        assertTrue(s2.nbt().length > 0);
        CompoundTag parsedOut = (CompoundTag) NBTIO.readTag(new ByteArrayInputStream(s2.nbt()), ByteOrder.LITTLE_ENDIAN, false);
        assertEquals(1, parsedOut.getList("ench", CompoundTag.class).size());
        assertEquals(0, parsedOut.getList("ench", CompoundTag.class).get(0).getShort("id"));
    }

    @Test
    void patchesAddEntityModifiersMetadataAndSuppressionFor01510() {
        // 1) Shulker (54) debe suprimirse en < 92
        LegacyStream shulker = LegacyStream.writer(64);
        shulker.writeByte(ProtocolInfo_v113.ADD_ENTITY_PACKET & 0xFF);
        shulker.writeEntityUniqueId(LegacyProtocol.PROTOCOL_113, 100L);
        shulker.writeEntityRuntimeId(LegacyProtocol.PROTOCOL_113, 100L);
        shulker.writeUnsignedVarInt(54);
        shulker.writeVector3(LegacyProtocol.PROTOCOL_113, 0f, 64f, 0f);
        shulker.writeVector3(LegacyProtocol.PROTOCOL_113, 0f, 0f, 0f);
        shulker.writeFloatLE(0f);
        shulker.writeFloatLE(0f);
        shulker.writeUnsignedVarInt(0);
        shulker.writeUnsignedVarInt(0); // meta
        assertTrue(PacketTranslator.translateOutbound(shulker.toByteArray(), LegacyProtocol.PROTOCOL_84, true).suppress());

        // 2) Zombie (32) en 0.15.10 incluye modifiers(intBE 0) y metadata 0.15 + terminador 0x7F
        LegacyStream zombie = LegacyStream.writer(96);
        zombie.writeByte(ProtocolInfo_v113.ADD_ENTITY_PACKET & 0xFF);
        zombie.writeEntityUniqueId(LegacyProtocol.PROTOCOL_113, 77L);
        zombie.writeEntityRuntimeId(LegacyProtocol.PROTOCOL_113, 77L);
        zombie.writeUnsignedVarInt(32);
        zombie.writeVector3(LegacyProtocol.PROTOCOL_113, 1f, 65f, 2f);
        zombie.writeVector3(LegacyProtocol.PROTOCOL_113, 0f, 0f, 0f);
        zombie.writeFloatLE(10f);
        zombie.writeFloatLE(20f);
        zombie.writeUnsignedVarInt(0); // attributes
        // Metadata v113: count=2 -> key 0 flags (LONG), key 4 nametag (STRING)
        zombie.writeUnsignedVarInt(2);
        zombie.writeUnsignedVarInt(0);
        zombie.writeUnsignedVarInt(LegacyContentMapper.DATA_TYPE_LONG);
        zombie.writeVarLong(1L << 14); // CAN_SHOW_NAME
        zombie.writeUnsignedVarInt(4);
        zombie.writeUnsignedVarInt(LegacyContentMapper.DATA_TYPE_STRING);
        zombie.writeString(LegacyProtocol.PROTOCOL_113, "Zombi");

        PacketTranslator.OutboundResult outRes = PacketTranslator.translateOutbound(zombie.toByteArray(), LegacyProtocol.PROTOCOL_84, true);
        assertFalse(outRes.suppress());
        LegacyStream r = LegacyStream.reader(outRes.legacyPacket());
        assertEquals(0x0b, r.readByte());
        assertEquals(77L, r.readEntityRuntimeId(LegacyProtocol.PROTOCOL_84));
        assertEquals(32, r.readIntBE());
        r.readVector3(LegacyProtocol.PROTOCOL_84);
        r.readVector3(LegacyProtocol.PROTOCOL_84);
        assertEquals(20f, r.readFloatBE());
        assertEquals(10f, r.readFloatBE());
        assertEquals(0, r.readIntBE()); // modifiers intBE
        // Debe terminar en 0x7F seguido por shortBE(0) links
        byte[] tail = r.readRemaining();
        assertTrue(tail.length >= 3);
        assertEquals(0x7F, tail[tail.length - 3] & 0xFF);
        assertEquals(0, tail[tail.length - 2] & 0xFF);
        assertEquals(0, tail[tail.length - 1] & 0xFF);
    }

    @Test
    void convertsChunkBlocksAndTileEntitiesAndBlockEntityDataSafely() throws IOException {
        // Chunk subchunk v113 con bloque 218 (Shulker Box) -> debe bajar a Chest (54) en 0.15
        // y trailing tile entities: Sign (conservado como LittleEndianNBT) + ShulkerBox (filtrado)
        CompoundTag signTile = new CompoundTag()
                .putString("id", "Sign")
                .putInt("x", 1)
                .putInt("y", 64)
                .putInt("z", 2)
                .putString("Text1", "Hola");
        CompoundTag shulkerTile = new CompoundTag()
                .putString("id", "ShulkerBox")
                .putInt("x", 3)
                .putInt("y", 64)
                .putInt("z", 4);
        byte[] signNet = NBTIO.write(signTile, ByteOrder.LITTLE_ENDIAN, true);
        byte[] shulkerNet = NBTIO.write(shulkerTile, ByteOrder.LITTLE_ENDIAN, true);

        LegacyStream chunkPayload = LegacyStream.writer(16384);
        chunkPayload.writeByte(1); // 1 subchunk
        chunkPayload.writeByte(0); // version 0
        byte[] blockIds = new byte[4096];
        blockIds[0] = (byte) 218; // Shulker Box -> 54 Chest en 0.15
        blockIds[1] = (byte) 169; // Sea Lantern -> 89 Glowstone en 0.15
        chunkPayload.writeBytes(blockIds);
        chunkPayload.writeBytes(new byte[2048]); // data
        chunkPayload.writeBytes(new byte[2048]); // sky light
        chunkPayload.writeBytes(new byte[2048]); // block light
        chunkPayload.writeBytes(new byte[256]);  // heightmap (256B)
        chunkPayload.writeBytes(new byte[256]);  // PAD_256 (256B)
        chunkPayload.writeBytes(new byte[256]);  // biomes
        chunkPayload.writeByte(0);               // border blocks
        chunkPayload.writeVarInt(0);             // extraData count
        chunkPayload.writeBytes(signNet);
        chunkPayload.writeBytes(shulkerNet);

        LegacyStream fullChunk = LegacyStream.writer(16384);
        fullChunk.writeVarInt(0);
        fullChunk.writeVarInt(0);
        fullChunk.writeByteArray(LegacyProtocol.PROTOCOL_113, chunkPayload.toByteArray());

        byte[] p84Chunk = ChunkTranslator.translateOutbound(fullChunk.toByteArray(), LegacyProtocol.PROTOCOL_84);
        LegacyStream outReader = LegacyStream.reader(p84Chunk);
        assertEquals(0, outReader.readIntBE());
        assertEquals(0, outReader.readIntBE());
        assertEquals(0, outReader.readByte());
        byte[] legacyCol = outReader.readByteArray(LegacyProtocol.PROTOCOL_84);
        assertEquals(54, legacyCol[0] & 0xFF);
        assertEquals(89, legacyCol[1] & 0xFF);

        // Verificar que la entidad de bloque Sign al final esta en LittleEndianNBT (no network VarInt!)
        int tileStart = 83204;
        assertTrue(legacyCol.length > tileStart);
        ByteArrayInputStream bais = new ByteArrayInputStream(legacyCol, tileStart, legacyCol.length - tileStart);
        CompoundTag decodedTile = (CompoundTag) NBTIO.readTag(bais, ByteOrder.LITTLE_ENDIAN, false);
        assertEquals("Sign", decodedTile.getString("id"));
        assertEquals("Hola", decodedTile.getString("Text1"));
        assertEquals(0, bais.available()); // ShulkerBox fue eliminado limpiamente

        // BlockEntityDataPacket: Bed debe suprimirse en < 110; ItemFrame map_uuid LongTag -> StringTag
        CompoundTag frameTile = new CompoundTag()
                .putString("id", "ItemFrame")
                .putInt("x", 5).putInt("y", 65).putInt("z", 5)
                .putCompound("Item", new CompoundTag().putShort("id", 358).putShort("Damage", 0).putByte("Count", 1)
                        .put("map_uuid", new LongTag("map_uuid", 9999L)));
        byte[] convertedFrame = LegacyNbtConverter.convertSingleBlockEntity(
                NBTIO.write(frameTile, ByteOrder.LITTLE_ENDIAN, true), LegacyProtocol.PROTOCOL_84
        );
        assertNotNull(convertedFrame);
        CompoundTag frameOut = (CompoundTag) NBTIO.readTag(new ByteArrayInputStream(convertedFrame), ByteOrder.LITTLE_ENDIAN, false);
        assertEquals("9999", frameOut.getCompound("Item").getString("map_uuid"));

        CompoundTag bedTile = new CompoundTag().putString("id", "Bed").putInt("x", 0).putInt("y", 64).putInt("z", 0);
        assertNull(LegacyNbtConverter.convertSingleBlockEntity(
                NBTIO.write(bedTile, ByteOrder.LITTLE_ENDIAN, true), LegacyProtocol.PROTOCOL_105
        ));
    }

    @Test
    void bridgesSoundsMoveRotationSkinsEntityLinksAndUseItemFace() {
        // 1) LevelSoundEvent SHOOT (20) en 0.15.10 -> LevelEvent (0x16) id 1002
        LegacyStream sndV113 = LegacyStream.writer(32);
        sndV113.writeByte(ProtocolInfo_v113.LEVEL_SOUND_EVENT_PACKET & 0xFF);
        sndV113.writeByte(20);
        sndV113.writeVector3(LegacyProtocol.PROTOCOL_113, 1f, 64f, 1f);
        sndV113.writeVarInt(-1);
        sndV113.writeVarInt(1);
        sndV113.writeBoolean(false);
        sndV113.writeBoolean(false);
        PacketTranslator.OutboundResult snd015 = PacketTranslator.translateOutbound(sndV113.toByteArray(), LegacyProtocol.PROTOCOL_84, true);
        assertFalse(snd015.suppress());
        LegacyStream s015r = LegacyStream.reader(snd015.legacyPacket());
        assertEquals(0x16, s015r.readByte());
        assertEquals(1002, s015r.readShortBE());

        // 2) LevelSoundEvent POP (70) en 1.0.5 (90..109) -> sound 68 (-2 shift)
        LegacyStream popV113 = LegacyStream.writer(32);
        popV113.writeByte(ProtocolInfo_v113.LEVEL_SOUND_EVENT_PACKET & 0xFF);
        popV113.writeByte(70);
        popV113.writeVector3(LegacyProtocol.PROTOCOL_113, 0f, 64f, 0f);
        popV113.writeVarInt(-1);
        popV113.writeVarInt(1);
        popV113.writeBoolean(false);
        popV113.writeBoolean(false);
        PacketTranslator.OutboundResult pop105 = PacketTranslator.translateOutbound(popV113.toByteArray(), LegacyProtocol.PROTOCOL_105, false);
        assertFalse(pop105.suppress());
        LegacyStream p105r = LegacyStream.reader(pop105.legacyPacket());
        assertEquals(0x1a, p105r.readByte());
        assertEquals(68, p105r.readByte());

        // 3) SetEntityLink suprimido en 0.15.x
        LegacyStream linkV113 = LegacyStream.writer(16);
        linkV113.writeByte(ProtocolInfo_v113.SET_ENTITY_LINK_PACKET & 0xFF);
        linkV113.writeEntityUniqueId(LegacyProtocol.PROTOCOL_113, 1L);
        linkV113.writeEntityUniqueId(LegacyProtocol.PROTOCOL_113, 2L);
        linkV113.writeByte(1);
        assertTrue(PacketTranslator.translateOutbound(linkV113.toByteArray(), LegacyProtocol.PROTOCOL_84, true).suppress());

        // 4) Downsample skin HD 128x128 (65536B) -> 64x64 (16384B) para 0.15.10 (< 100)
        byte[] hdSkin = new byte[128 * 128 * 4];
        byte[] safeSkin = LegacyContentMapper.normalizeSkinData(hdSkin, LegacyProtocol.PROTOCOL_84);
        assertEquals(Skin.DOUBLE_SKIN_SIZE, safeSkin.length);

        // 5) MoveEntityAbsolutePacket rotacion 110..112 vs 0.15.x
        LegacyStream moveEntV113 = LegacyStream.writer(24);
        moveEntV113.writeByte(ProtocolInfo_v113.MOVE_ENTITY_PACKET & 0xFF);
        moveEntV113.writeEntityRuntimeId(LegacyProtocol.PROTOCOL_113, 9L);
        moveEntV113.writeVector3(LegacyProtocol.PROTOCOL_113, 1f, 2f, 3f);
        moveEntV113.writeByte(10); // pitch
        moveEntV113.writeByte(20); // yaw
        moveEntV113.writeByte(30); // headYaw
        moveEntV113.writeByte(0x03); // onGround + teleport
        LegacyStream m110 = LegacyStream.reader(PacketTranslator.translateOutbound(moveEntV113.toByteArray(), LegacyProtocol.PROTOCOL_110, false).legacyPacket());
        assertEquals(0x12, m110.readByte());
        assertEquals(9L, m110.readEntityRuntimeId(LegacyProtocol.PROTOCOL_110));
        m110.readVector3(LegacyProtocol.PROTOCOL_110);
        assertEquals(10, m110.readByte()); // pitch
        assertEquals(30, m110.readByte()); // headYaw
        assertEquals(20, m110.readByte()); // yaw
        assertTrue(m110.readBoolean());
        assertTrue(m110.readBoolean());

        // 6) UseItemPacket inbound 0.15.10 con face=0xFF (-1 lanzar/usar en el aire)
        LegacyStream useAir = LegacyStream.writer(64);
        useAir.writeByte(0x1f);
        useAir.writeBlockPosition(LegacyProtocol.PROTOCOL_84, 0, 0, 0);
        useAir.writeByte(0xFF);
        useAir.writeVector3(LegacyProtocol.PROTOCOL_84, 0f, 0f, 0f);
        useAir.writeVector3(LegacyProtocol.PROTOCOL_84, 0f, 64f, 0f);
        useAir.writeIntBE(0);
        useAir.writeSlot(LegacyProtocol.PROTOCOL_84, LegacyStream.LegacySlot.AIR);
        PacketTranslator.InboundResult useAirCanon = PacketTranslator.translateInbound(useAir.toByteArray(), LegacyProtocol.PROTOCOL_84);
        UseItemPacket_v113 decodedUse = new UseItemPacket_v113();
        decodedUse.protocol = 113;
        decodedUse.gameVersion = GameVersion.byProtocol(113, false);
        decodedUse.setBuffer(useAirCanon.canonicalPacket(), 1);
        decodedUse.decode();
        assertEquals(-1, decodedUse.face);
    }
}

