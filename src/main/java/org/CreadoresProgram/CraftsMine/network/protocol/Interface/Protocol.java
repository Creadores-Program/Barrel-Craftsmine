/*
 * GNU LESSER GENERAL PUBLIC LICENSE
 *                       Version 3, 29 June 2007
 *
 * Copyright (C) 2007 Free Software Foundation, Inc. <http://fsf.org/>
 * Everyone is permitted to copy and distribute verbatim copies
 * of this license document, but changing it is not allowed.
 *
 * You can view LICENCE file for details. 
 *
 * @author The Dragonet Team
 */
package org.CreadoresProgram.CraftsMine.network.protocol.Interface;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import org.CreadoresProgram.CraftsMine.network.protocol.*;
import org.CreadoresProgram.CraftsMine.network.protocol.base.MCDPacket;
import org.CreadoresProgram.CraftsMine.dragonetLib.proxy.utilities.BinaryStream;

public final class Protocol {

    private final static HashMap<Byte, Class<? extends MCDPacket>> protocol;

    static {
        protocol = new HashMap<>();
        registerDecoder(ProtocolInfo.BATCH_PACKET, BatchPacket.class);
        registerDecoder(ProtocolInfo.DISCONNECT_PACKET, DisconnectPacket.class);
        registerDecoder(ProtocolInfo.TEXT_PACKET, TextPacket.class);
        registerDecoder(ProtocolInfo.LOGIN_PACKET, LoginPacket.class);
        registerDecoder(ProtocolInfo.MOVE_PLAYER_PACKET, MovePlayerPacket.class);
        registerDecoder(ProtocolInfo.PLAYER_ACTION_PACKET, PlayerActionPacket.class);
        registerDecoder(ProtocolInfo.INTERACT_PACKET, InteractPacket.class);
        registerDecoder(ProtocolInfo.ENTITY_EVENT_PACKET, EntityEventPacket.class);
        registerDecoder(ProtocolInfo.REMOVE_BLOCK_PACKET, RemoveBlockPacket.class);
        registerDecoder(ProtocolInfo.UPDATE_BLOCK_PACKET, UpdateBlockPacket.class);
        registerDecoder(ProtocolInfo.ANIMATE_PACKET, AnimatePacket.class);
        registerDecoder(ProtocolInfo.LEVEL_EVENT_PACKET, LevelEventPacket.class);
        registerDecoder(ProtocolInfo.MOVE_ENTITY_PACKET, MoveEntitiesPacket.class);
        registerDecoder(ProtocolInfo.RESPAWN_PACKET, RespawnPacket.class);
        registerDecoder(ProtocolInfo.SET_DIFFICULTY_PACKET, SetDifficultyPacket.class);
        registerDecoder(ProtocolInfo.SET_HEALTH_PACKET, SetHealthPacket.class);
        registerDecoder(ProtocolInfo.CONTAINER_CLOSE_PACKET, WindowClosePacket.class);
        registerDecoder(ProtocolInfo.BLOCK_ENTITY_DATA_PACKET, BlockEntityDataPacket.class);
        registerDecoder(ProtocolInfo.PLAYER_INPUT_PACKET, PlayerInputPacket.class);
        registerDecoder(ProtocolInfo.REQUEST_CHUNK_RADIUS_PACKET, RequestChunkRadiusPacket.class);
        registerDecoder(ProtocolInfo.START_GAME_PACKET, StartGamePacket.class);
        registerDecoder(ProtocolInfo.CONTAINER_SET_CONTENT_PACKET, WindowSetContentPacket.class);
        registerDecoder(ProtocolInfo.CONTAINER_SET_SLOT_PACKET, WindowSetSlotPacket.class);
        registerDecoder(ProtocolInfo.CRAFTING_EVENT_PACKET, CraftingEventPacket.class);
        registerDecoder(ProtocolInfo.DROP_ITEM_PACKET, DropItemPacket.class);
        registerDecoder(ProtocolInfo.ITEM_FRAME_DROP_ITEM_PACKET, ItemFrameDropItemPacket.class);
        registerDecoder(ProtocolInfo.MOB_ARMOR_EQUIPMENT_PACKET, MobArmorEquipmentPacket.class);
        registerDecoder(ProtocolInfo.MOB_EQUIPMENT_PACKET, MobEquipmentPacket.class);
        registerDecoder(ProtocolInfo.USE_ITEM_PACKET, UseItemPacket.class);
        registerDecoder(ProtocolInfo.ADD_ITEM_ENTITY_PACKET, AddItemEntityPacket.class);
    }

    private static void registerDecoder(byte id, Class<? extends MCDPacket> clazz) {
        if (protocol.containsKey(id)) {
            return;
        }
        protocol.put(id, clazz);
    }

    public static MCDPacket decode(byte[] data) {
        byte pid = data[0];
        int start = 1;

        if (pid == (byte) 0xfe) {
            pid = data[1];
            start++;
        }
        if (!protocol.containsKey(pid)) {
            return new UnknownPacket(pid, data);
        }
        try{
        MCDPacket pdata = (MCDPacket) protocol.get(pid).newInstance();
        pdata.setBuffer(data, start);
        pdata.decode();
        return pdata;
        }catch(Exception ex){
        }
        return new UnknownPacket(pid, data);
    }
}
