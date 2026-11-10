package org.CreadoresProgram.CraftsMine.network.translator.bedrock;
import org.CreadoresProgram.CraftsMine.network.translator.interfaces.BedrockPacketTranslator;
import org.CreadoresProgram.CraftsMine.player.Player;
import com.nukkitx.protocol.bedrock.BedrockPacket;
import com.nukkitx.protocol.bedrock.packet.EntityEventPacket;
import com.nukkitx.protocol.bedrock.data.entity.EntityEventType;

/**
 * v113 EntityEventPacket (EntityEventType type, int data, runtimeEntityId)
 * → 0.15.x EntityEventPacket (byte event, long eid).
 *
 * <p>v113 EntityEventType enum ordinals align with 0.15.x byte event
 * constants for the common cases:</p>
 * <ul>
 *   <li>HURT (2) → HURT_ANIMATION (2)</li>
 *   <li>DEATH (3) → DEATH_ANIMATION (3)</li>
 *   <li>TAME_FAILED (6) → TAME_FAIL (6)</li>
 *   <li>TAME_SUCCEEDED (7) → TAME_SUCCESS (7)</li>
 *   <li>SHAKE_WETNESS (8) → SHAKE_WET (8)</li>
 *   <li>USE_ITEM (9) → USE_ITEM (9)</li>
 *   <li>EAT_GRASS (10) → EAT_GRASS_ANIMATION (10)</li>
 *   <li>FISH_HOOK_BUBBLE/POSITION/TIME/TEASE → matching 0.15.x constants</li>
 *   <li>PLAY_AMBIENT (16) → AMBIENT_SOUND (16)</li>
 *   <li>RESPAWN (17) → RESPAWN (17)</li>
 * </ul>
 *
 * <p>0.15.x has no `data` field; it is dropped.</p>
 */
public class EntityEvent implements BedrockPacketTranslator{
  @Override
  public void translate(BedrockPacket pk, Player player){
    EntityEventPacket packet = (EntityEventPacket) pk;
    if (packet.getType() == null) return;
    org.CreadoresProgram.CraftsMine.network.protocol.EntityEventPacket npk =
        new org.CreadoresProgram.CraftsMine.network.protocol.EntityEventPacket();
    npk.eid = packet.getRuntimeEntityId();
    npk.event = (byte) packet.getType().ordinal();
    player.sendDataCraftsman(npk);
  }
}
