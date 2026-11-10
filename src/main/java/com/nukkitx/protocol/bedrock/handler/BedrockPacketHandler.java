package com.nukkitx.protocol.bedrock.handler;

import com.nukkitx.protocol.PacketHandler;
import com.nukkitx.protocol.bedrock.packet.*;

public interface BedrockPacketHandler extends PacketHandler {

    default boolean handle(AdventureSettingsPacket packet) {
        return false;
    }

    default boolean handle(AnimatePacket packet) {
        return false;
    }

    default boolean handle(BlockEntityDataPacket packet) {
        return false;
    }

    default boolean handle(BlockPickRequestPacket packet) {
        return false;
    }

    default boolean handle(ClientToServerHandshakePacket packet) {
        return false;
    }

    default boolean handle(CommandBlockUpdatePacket packet) {
        return false;
    }

    default boolean handle(ContainerClosePacket packet) {
        return false;
    }

    default boolean handle(CraftingEventPacket packet) {
        return false;
    }

    default boolean handle(EntityEventPacket packet) {
        return false;
    }

    default boolean handle(EntityFallPacket packet) {
        return false;
    }

    default boolean handle(EventPacket packet) {
        return false;
    }

    default boolean handle(InteractPacket packet) {
        return false;
    }

    default boolean handle(ItemFrameDropItemPacket packet) {
        return false;
    }

    default boolean handle(LevelSoundEventPacket packet) {
        return false;
    }

    default boolean handle(LoginPacket packet) {
        return false;
    }

    default boolean handle(MapInfoRequestPacket packet) {
        return false;
    }

    default boolean handle(MobArmorEquipmentPacket packet) {
        return false;
    }

    default boolean handle(MobEquipmentPacket packet) {
        return false;
    }

    default boolean handle(MoveEntityPacket packet) {
        return false;
    }

    default boolean handle(MovePlayerPacket packet) {
        return false;
    }

    default boolean handle(PlayerActionPacket packet) {
        return false;
    }

    default boolean handle(PlayerInputPacket packet) {
        return false;
    }

    default boolean handle(PurchaseReceiptPacket packet) {
        return false;
    }

    default boolean handle(RequestChunkRadiusPacket packet) {
        return false;
    }

    default boolean handle(ResourcePackChunkRequestPacket packet) {
        return false;
    }

    default boolean handle(ResourcePackClientResponsePacket packet) {
        return false;
    }

    default boolean handle(RiderJumpPacket packet) {
        return false;
    }

    default boolean handle(SetPlayerGameTypePacket packet) {
        return false;
    }

    default boolean handle(AddBehaviorTreePacket packet) {
        return false;
    }

    default boolean handle(AddEntityPacket packet) {
        return false;
    }

    default boolean handle(AddHangingEntityPacket packet) {
        return false;
    }

    default boolean handle(AddItemEntityPacket packet) {
        return false;
    }

    default boolean handle(AddPaintingPacket packet) {
        return false;
    }

    default boolean handle(AddPlayerPacket packet) {
        return false;
    }

    default boolean handle(AvailableCommandsPacket packet) {
        return false;
    }

    default boolean handle(BlockEventPacket packet) {
        return false;
    }

    default boolean handle(BossEventPacket packet) {
        return false;
    }

    default boolean handle(CameraPacket packet) {
        return false;
    }

    default boolean handle(ChangeDimensionPacket packet) {
        return false;
    }

    default boolean handle(ChunkRadiusUpdatedPacket packet) {
        return false;
    }

    default boolean handle(ClientboundMapItemDataPacket packet) {
        return false;
    }

    default boolean handle(ContainerOpenPacket packet) {
        return false;
    }

    default boolean handle(ContainerSetDataPacket packet) {
        return false;
    }

    default boolean handle(CraftingDataPacket packet) {
        return false;
    }

    default boolean handle(DisconnectPacket packet) {
        return false;
    }

    default boolean handle(ExplodePacket packet) {
        return false;
    }

    default boolean handle(GameRulesChangedPacket packet) {
        return false;
    }

    default boolean handle(HurtArmorPacket packet) {
        return false;
    }

    default boolean handle(LevelEventPacket packet) {
        return false;
    }

    default boolean handle(MobEffectPacket packet) {
        return false;
    }

    default boolean handle(PlayerListPacket packet) {
        return false;
    }

    default boolean handle(PlaySoundPacket packet) {
        return false;
    }

    default boolean handle(PlayStatusPacket packet) {
        return false;
    }

    default boolean handle(RemoveEntityPacket packet) {
        return false;
    }

    default boolean handle(ResourcePackChunkDataPacket packet) {
        return false;
    }

    default boolean handle(ResourcePackDataInfoPacket packet) {
        return false;
    }

    default boolean handle(ResourcePacksInfoPacket packet) {
        return false;
    }

    default boolean handle(ResourcePackStackPacket packet) {
        return false;
    }

    default boolean handle(RespawnPacket packet) {
        return false;
    }

    default boolean handle(ServerToClientHandshakePacket packet) {
        return false;
    }

    default boolean handle(SetCommandsEnabledPacket packet) {
        return false;
    }

    default boolean handle(SetDifficultyPacket packet) {
        return false;
    }

    default boolean handle(SetEntityDataPacket packet) {
        return false;
    }

    default boolean handle(SetEntityLinkPacket packet) {
        return false;
    }

    default boolean handle(SetEntityMotionPacket packet) {
        return false;
    }

    default boolean handle(SetHealthPacket packet) {
        return false;
    }

    default boolean handle(SetSpawnPositionPacket packet) {
        return false;
    }

    default boolean handle(SetTimePacket packet) {
        return false;
    }

    default boolean handle(SetTitlePacket packet) {
        return false;
    }

    default boolean handle(ShowCreditsPacket packet) {
        return false;
    }

    default boolean handle(ShowStoreOfferPacket packet) {
        return false;
    }

    default boolean handle(SimpleEventPacket packet) {
        return false;
    }

    default boolean handle(SpawnExperienceOrbPacket packet) {
        return false;
    }

    default boolean handle(StartGamePacket packet) {
        return false;
    }

    default boolean handle(StopSoundPacket packet) {
        return false;
    }

    default boolean handle(StructureBlockUpdatePacket packet) {
        return false;
    }

    default boolean handle(TakeItemEntityPacket packet) {
        return false;
    }

    default boolean handle(TextPacket packet) {
        return false;
    }

    default boolean handle(TransferPacket packet) {
        return false;
    }

    default boolean handle(UpdateAttributesPacket packet) {
        return false;
    }

    default boolean handle(UpdateBlockPacket packet) {
        return false;
    }

    default boolean handle(UpdateEquipPacket packet) {
        return false;
    }

    default boolean handle(UpdateTradePacket packet) {
        return false;
    }

    default boolean handle(UseItemPacket packet) {
        return false;
    }

    default boolean handle(DropItemPacket packet) {
        return false;
    }

    default boolean handle(InventoryActionPacket packet) {
        return false;
    }

    default boolean handle(RemoveBlockPacket packet) {
        return false;
    }

    default boolean handle(FullChunkDataPacket packet) {
        return false;
    }

    default boolean handle(ContainerSetSlotPacket packet) {
        return false;
    }

    default boolean handle(ContainerSetContentPacket packet) {
        return false;
    }

    default boolean handle(ReplaceItemInSlotPacket packet) {
        return false;
    }

    default boolean handle(AddItemPacket packet) {
        return false;
    }

    default boolean handle(CommandStepPacket packet) {
        return false;
    }
}
