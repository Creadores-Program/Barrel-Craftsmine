package org.CreadoresProgram.CraftsMine.network.translator;
import org.CreadoresProgram.CraftsMine.player.Player;
import org.CreadoresProgram.CraftsMine.server.Server;
import com.nukkitx.protocol.bedrock.BedrockPacket;
import com.nukkitx.protocol.bedrock.packet.*;
import lombok.Getter;
import org.CreadoresProgram.CraftsMine.network.protocol.base.MCDPacket;
import org.CreadoresProgram.CraftsMine.network.translator.interfaces.*;
import org.CreadoresProgram.CraftsMine.network.translator.craftsman.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Per-player translator registry. Holds the bidirectional map between packet
 * classes and translator instances and dispatches translation jobs to the
 * shared thread pool owned by {@link Server#getTranslationExecutor()}.
 *
 * <p>Previously this class created its own {@code ThreadPoolExecutor} per
 * player with {@code maxPoolSize = Integer.MAX_VALUE}, which under bursty
 * traffic produced one unbounded worker pool per connected client. Sharing a
 * single capped pool across all players keeps steady-state thread and memory
 * usage flat regardless of how many clients are connected.</p>
 */
public class PacketTranslatorManager {
  @Getter
  private final Map<Class<? extends MCDPacket>, CraftsmanPacketTranslator> craftsmanTranslators = new HashMap<>();
  @Getter
  private final Map<Class<? extends BedrockPacket>, BedrockPacketTranslator> bedrockTranslators = new HashMap<>();
  private final Player player;
  public PacketTranslatorManager(Player player){
    this.player = player;
    this.registerDefaultPackets();
  }
  private void registerDefaultPackets(){
    //Bedrock packets
    bedrockTranslators.put(TextPacket.class, new org.CreadoresProgram.CraftsMine.network.translator.bedrock.TextPack());
    bedrockTranslators.put(PlayStatusPacket.class, new org.CreadoresProgram.CraftsMine.network.translator.bedrock.PlayStatus());
    bedrockTranslators.put(DisconnectPacket.class, new org.CreadoresProgram.CraftsMine.network.translator.bedrock.DisconnectPack());
    bedrockTranslators.put(ResourcePackStackPacket.class, new org.CreadoresProgram.CraftsMine.network.translator.bedrock.ResourcePackStack());
    bedrockTranslators.put(ResourcePacksInfoPacket.class, new org.CreadoresProgram.CraftsMine.network.translator.bedrock.ResourcePacksInfo());
    bedrockTranslators.put(ServerToClientHandshakePacket.class, new org.CreadoresProgram.CraftsMine.network.translator.bedrock.ServerToClientHandshake());
    bedrockTranslators.put(PlayerListPacket.class, new org.CreadoresProgram.CraftsMine.network.translator.bedrock.PlayerList());
    bedrockTranslators.put(SetSpawnPositionPacket.class, new org.CreadoresProgram.CraftsMine.network.translator.bedrock.SetSpawnPosition());
    bedrockTranslators.put(SetPlayerGameTypePacket.class, new org.CreadoresProgram.CraftsMine.network.translator.bedrock.SetPlayerGameType());
    bedrockTranslators.put(StartGamePacket.class, new org.CreadoresProgram.CraftsMine.network.translator.bedrock.StartGame());
    bedrockTranslators.put(SetTimePacket.class, new org.CreadoresProgram.CraftsMine.network.translator.bedrock.SetTime());
    bedrockTranslators.put(AnimatePacket.class, new org.CreadoresProgram.CraftsMine.network.translator.bedrock.Animate());
    bedrockTranslators.put(MovePlayerPacket.class, new org.CreadoresProgram.CraftsMine.network.translator.bedrock.MovePlayer());
    bedrockTranslators.put(SetHealthPacket.class, new org.CreadoresProgram.CraftsMine.network.translator.bedrock.SetHealth());
    bedrockTranslators.put(ChangeDimensionPacket.class, new org.CreadoresProgram.CraftsMine.network.translator.bedrock.ChangeDimension());
    bedrockTranslators.put(SetTitlePacket.class, new org.CreadoresProgram.CraftsMine.network.translator.bedrock.SetTitle());
    bedrockTranslators.put(RespawnPacket.class, new org.CreadoresProgram.CraftsMine.network.translator.bedrock.Respawn());
    bedrockTranslators.put(SetDifficultyPacket.class, new org.CreadoresProgram.CraftsMine.network.translator.bedrock.SetDifficulty());
    bedrockTranslators.put(ContainerClosePacket.class, new org.CreadoresProgram.CraftsMine.network.translator.bedrock.ContainerClose());
    bedrockTranslators.put(ContainerOpenPacket.class, new org.CreadoresProgram.CraftsMine.network.translator.bedrock.ContainerOpen());
    bedrockTranslators.put(ContainerSetDataPacket.class, new org.CreadoresProgram.CraftsMine.network.translator.bedrock.ContainerSetData());
    bedrockTranslators.put(ExplodePacket.class, new org.CreadoresProgram.CraftsMine.network.translator.bedrock.Explode());
    bedrockTranslators.put(HurtArmorPacket.class, new org.CreadoresProgram.CraftsMine.network.translator.bedrock.HurtArmor());
    bedrockTranslators.put(ContainerSetContentPacket.class, new org.CreadoresProgram.CraftsMine.network.translator.bedrock.ContainerSetContent());
    bedrockTranslators.put(ContainerSetSlotPacket.class, new org.CreadoresProgram.CraftsMine.network.translator.bedrock.ContainerSetSlot());
    bedrockTranslators.put(UpdateBlockPacket.class, new org.CreadoresProgram.CraftsMine.network.translator.bedrock.UpdateBlock());
    bedrockTranslators.put(AddPaintingPacket.class, new org.CreadoresProgram.CraftsMine.network.translator.bedrock.AddPainting());
    bedrockTranslators.put(MobEffectPacket.class, new org.CreadoresProgram.CraftsMine.network.translator.bedrock.MobEffect());
    bedrockTranslators.put(ChunkRadiusUpdatedPacket.class, new org.CreadoresProgram.CraftsMine.network.translator.bedrock.ChunkRadiusUpdated());
    bedrockTranslators.put(InteractPacket.class, new org.CreadoresProgram.CraftsMine.network.translator.bedrock.Interact());
    bedrockTranslators.put(PlayerActionPacket.class, new org.CreadoresProgram.CraftsMine.network.translator.bedrock.PlayerAction());
    bedrockTranslators.put(FullChunkDataPacket.class, new org.CreadoresProgram.CraftsMine.network.translator.bedrock.FullChunkData());
    bedrockTranslators.put(AddPlayerPacket.class, new org.CreadoresProgram.CraftsMine.network.translator.bedrock.AddPlayer());
    bedrockTranslators.put(UpdateAttributesPacket.class, new org.CreadoresProgram.CraftsMine.network.translator.bedrock.UpdateAttributes());
    bedrockTranslators.put(LevelEventPacket.class, new org.CreadoresProgram.CraftsMine.network.translator.bedrock.LevelEventT());
    bedrockTranslators.put(AddEntityPacket.class, new org.CreadoresProgram.CraftsMine.network.translator.bedrock.AddEntity());
    bedrockTranslators.put(MoveEntityPacket.class, new org.CreadoresProgram.CraftsMine.network.translator.bedrock.MoveEntityAbsolute());
    bedrockTranslators.put(MoveEntityPacket.class, new org.CreadoresProgram.CraftsMine.network.translator.bedrock.MoveEntityDelta());
    bedrockTranslators.put(EntityEventPacket.class, new org.CreadoresProgram.CraftsMine.network.translator.bedrock.EntityEvent());
    bedrockTranslators.put(RemoveEntityPacket.class, new org.CreadoresProgram.CraftsMine.network.translator.bedrock.RemoveEntity());
    bedrockTranslators.put(SetEntityLinkPacket.class, new org.CreadoresProgram.CraftsMine.network.translator.bedrock.SetEntityLink());
    bedrockTranslators.put(SetEntityMotionPacket.class, new org.CreadoresProgram.CraftsMine.network.translator.bedrock.SetEntityMotion());
    bedrockTranslators.put(SetEntityDataPacket.class, new org.CreadoresProgram.CraftsMine.network.translator.bedrock.SetEntityData());
    bedrockTranslators.put(BlockEventPacket.class, new org.CreadoresProgram.CraftsMine.network.translator.bedrock.BlockEvent());
    bedrockTranslators.put(AdventureSettingsPacket.class, new org.CreadoresProgram.CraftsMine.network.translator.bedrock.AdventureSettings());
    bedrockTranslators.put(AddItemEntityPacket.class, new org.CreadoresProgram.CraftsMine.network.translator.bedrock.AddItemEntity());
    bedrockTranslators.put(TakeItemEntityPacket.class, new org.CreadoresProgram.CraftsMine.network.translator.bedrock.TakeItemEntity());
    bedrockTranslators.put(MobArmorEquipmentPacket.class, new org.CreadoresProgram.CraftsMine.network.translator.bedrock.MobArmorEquipment());
    bedrockTranslators.put(MobEquipmentPacket.class, new org.CreadoresProgram.CraftsMine.network.translator.bedrock.MobEquipment());
    bedrockTranslators.put(BlockEntityDataPacket.class, new org.CreadoresProgram.CraftsMine.network.translator.bedrock.BlockEntityData());
    // SetScorePacket does not exist in NukkitX v113. Scoreboards in v113 are
    // managed via SetScoreboardIdentityPacket / SetScoreboardScorePacket
    // which are not yet implemented as translators. The previous
    // bedrockTranslators.put(SetScorePacket.class, ...) line has been
    // removed; restore it once the v113 scoreboard packet translators are
    // written.
    bedrockTranslators.put(BossEventPacket.class, new org.CreadoresProgram.CraftsMine.network.translator.bedrock.BossEvent());

    //Craftsman packets
    craftsmanTranslators.put(org.CreadoresProgram.CraftsMine.network.protocol.TextPacket.class, new ChatPacket());
    craftsmanTranslators.put(org.CreadoresProgram.CraftsMine.network.protocol.LoginPacket.class, new LoginHPacket());
    craftsmanTranslators.put(org.CreadoresProgram.CraftsMine.network.protocol.MovePlayerPacket.class, new MovePlayerC());
    craftsmanTranslators.put(org.CreadoresProgram.CraftsMine.network.protocol.PlayerActionPacket.class, new PlayerAction());
    craftsmanTranslators.put(org.CreadoresProgram.CraftsMine.network.protocol.BatchPacket.class, new BatchP());
    craftsmanTranslators.put(org.CreadoresProgram.CraftsMine.network.protocol.DisconnectPacket.class, new DisconnectPack());
    craftsmanTranslators.put(org.CreadoresProgram.CraftsMine.network.protocol.RequestChunkRadiusPacket.class, new RequestChunkRadius());
    craftsmanTranslators.put(org.CreadoresProgram.CraftsMine.network.protocol.InteractPacket.class, new Interact());
    craftsmanTranslators.put(org.CreadoresProgram.CraftsMine.network.protocol.AnimatePacket.class, new Animate());
    craftsmanTranslators.put(org.CreadoresProgram.CraftsMine.network.protocol.SetHealthPacket.class, new SetHealth());
    craftsmanTranslators.put(org.CreadoresProgram.CraftsMine.network.protocol.PlayerInputPacket.class, new PlayerInput());
    craftsmanTranslators.put(org.CreadoresProgram.CraftsMine.network.protocol.RespawnPacket.class, new Respawn());
    craftsmanTranslators.put(org.CreadoresProgram.CraftsMine.network.protocol.SetDifficultyPacket.class, new SetDifficulty());
    craftsmanTranslators.put(org.CreadoresProgram.CraftsMine.network.protocol.WindowClosePacket.class, new WindowClose());
    craftsmanTranslators.put(org.CreadoresProgram.CraftsMine.network.protocol.ItemFrameDropItemPacket.class, new ItemFrameDropItem());
    craftsmanTranslators.put(org.CreadoresProgram.CraftsMine.network.protocol.WindowSetContentPacket.class, new WindowSetContent());
    craftsmanTranslators.put(org.CreadoresProgram.CraftsMine.network.protocol.WindowSetSlotPacket.class, new WindowSetSlot());
    craftsmanTranslators.put(org.CreadoresProgram.CraftsMine.network.protocol.EntityEventPacket.class, new EntityEvent());
    craftsmanTranslators.put(org.CreadoresProgram.CraftsMine.network.protocol.MoveEntitiesPacket.class, new MoveEntities());
    craftsmanTranslators.put(org.CreadoresProgram.CraftsMine.network.protocol.CraftingEventPacket.class, new CraftingEvent());
    craftsmanTranslators.put(org.CreadoresProgram.CraftsMine.network.protocol.MobArmorEquipmentPacket.class, new MobArmorEquipment());
    craftsmanTranslators.put(org.CreadoresProgram.CraftsMine.network.protocol.MobEquipmentPacket.class, new MobEquipment());
    craftsmanTranslators.put(org.CreadoresProgram.CraftsMine.network.protocol.UpdateBlockPacket.class, new UpdateBlock());
    craftsmanTranslators.put(org.CreadoresProgram.CraftsMine.network.protocol.BlockEntityDataPacket.class, new BlockEntityData());
    // Gameplay-critical client→server translators that were missing:
    craftsmanTranslators.put(org.CreadoresProgram.CraftsMine.network.protocol.RemoveBlockPacket.class, new RemoveBlock());
    craftsmanTranslators.put(org.CreadoresProgram.CraftsMine.network.protocol.UseItemPacket.class, new UseItem());
    craftsmanTranslators.put(org.CreadoresProgram.CraftsMine.network.protocol.DropItemPacket.class, new DropItem());
  }
  public void translate(MCDPacket pk){
    CraftsmanPacketTranslator translator = craftsmanTranslators.get(pk.getClass());
    if(translator != null){
      ThreadPoolExecutor pool = Server.getInstance().getTranslationExecutor();
      pool.execute(() -> translator.translate(pk, player));
    }
  }
  public void translate(BedrockPacket pk){
    BedrockPacketTranslator translator = bedrockTranslators.get(pk.getClass());
    if(translator != null){
      if(translator.immediate()){
        translator.translate(pk, player);
      }else{
        ThreadPoolExecutor pool = Server.getInstance().getTranslationExecutor();
        pool.execute(() -> translator.translate(pk, player));
      }
    }
  }
}
