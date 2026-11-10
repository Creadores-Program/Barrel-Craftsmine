package org.CreadoresProgram.CraftsMine.network.translator.bedrock;
import org.CreadoresProgram.CraftsMine.network.translator.interfaces.BedrockPacketTranslator;
import org.CreadoresProgram.CraftsMine.player.Player;
import com.nukkitx.protocol.bedrock.BedrockPacket;
import com.nukkitx.protocol.bedrock.packet.PlayerActionPacket;
import com.nukkitx.math.vector.Vector3i;

/**
 * v113 PlayerActionPacket → 0.15.x PlayerActionPacket.
 *
 * <p>v113 PlayerActionType enum ordinals align with 0.15.x ACTION_*
 * constants (START_BREAK=0, ABORT_BREAK/CANCEL_BREAK=1, STOP_BREAK/
 * FINISH_BREAK=2, RELEASE_ITEM=3, STOP_SLEEPING=4, RESPAWN=5, JUMP=6,
 * START_SPRINT=7, STOP_SPRINT=8, START_SNEAK=9, STOP_SNEAK=10,
 * DIMENSION_CHANGE=11). The wire format also matches: eid + action int
 * + blockPos + face.</p>
 */
public class PlayerAction implements BedrockPacketTranslator{
  @Override
  public void translate(BedrockPacket pk, Player player){
    PlayerActionPacket packet = (PlayerActionPacket) pk;
    org.CreadoresProgram.CraftsMine.network.protocol.PlayerActionPacket npk =
        new org.CreadoresProgram.CraftsMine.network.protocol.PlayerActionPacket();
    npk.entityId = packet.getRuntimeEntityId();
    npk.action = packet.getAction() == null ? 0 : packet.getAction().ordinal();
    Vector3i pos = packet.getBlockPosition();
    if (pos != null) {
      npk.x = pos.getX();
      npk.y = pos.getY();
      npk.z = pos.getZ();
    }
    npk.face = packet.getFace();
    player.sendDataCraftsman(npk);
  }
}
