package org.CreadoresProgram.CraftsMine.network.translator.bedrock;
import org.CreadoresProgram.CraftsMine.network.translator.interfaces.BedrockPacketTranslator;
import org.CreadoresProgram.CraftsMine.player.Player;
import com.nukkitx.protocol.bedrock.BedrockPacket;
import com.nukkitx.protocol.bedrock.packet.TakeItemEntityPacket;

/**
 * v113 TakeItemEntityPacket (itemRuntimeEntityId, runtimeEntityId) →
 * 0.15.x PickUpItemPacket (target=collector, entityId=item).
 *
 * <p>Wire order is inverted: v113 sends item first then collector;
 * 0.15.x sends collector (target) first then item (entityId).</p>
 */
public class TakeItemEntity implements BedrockPacketTranslator{
  @Override
  public void translate(BedrockPacket pk, Player player){
    TakeItemEntityPacket packet = (TakeItemEntityPacket) pk;
    // Unknown-entity guard (see Player.knownEntities): both the collector
    // and the item entity must exist on the 0.15.x client.
    if (!player.getKnownEntities().contains(packet.getRuntimeEntityId())
        || !player.getKnownEntities().contains(packet.getItemRuntimeEntityId())) {
      return;
    }
    org.CreadoresProgram.CraftsMine.network.protocol.PickUpItemPacket npk =
        new org.CreadoresProgram.CraftsMine.network.protocol.PickUpItemPacket();
    // 0.15.x target = the entity that picks up; entityId = the item entity.
    npk.target = packet.getRuntimeEntityId();
    npk.entityId = packet.getItemRuntimeEntityId();
    player.sendDataCraftsman(npk);
  }
}
