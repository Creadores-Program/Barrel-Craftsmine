package org.CreadoresProgram.CraftsMine.network.translator.craftsman;
import org.CreadoresProgram.CraftsMine.network.protocol.base.MCDPacket;
import org.CreadoresProgram.CraftsMine.network.translator.interfaces.CraftsmanPacketTranslator;
import org.CreadoresProgram.CraftsMine.player.Player;
import com.nukkitx.protocol.bedrock.packet.PlayerActionPacket;
import com.nukkitx.protocol.bedrock.data.PlayerActionType;
import com.nukkitx.math.vector.Vector3i;

/**
 * 0.15.x PlayerActionPacket → v113 PlayerActionPacket.
 *
 * <p>0.15.x ACTION_* byte constants map to v113 PlayerActionType enum by
 * ordinal (both 0=START_BREAK, 1=ABORT/CANCEL_BREAK, 2=STOP/FINISH_BREAK,
 * 3=RELEASE_ITEM, 4=STOP_SLEEPING, 5=RESPAWN, 6=JUMP, 7=START_SPRINT,
 * 8=STOP_SPRINT, 9=START_SNEAK, 10=STOP_SNEAK, 11=DIMENSION_CHANGE,
 * 12=NETHER_UNKNOWN).</p>
 */
public class PlayerAction implements CraftsmanPacketTranslator {
  @Override
  public void translate(MCDPacket pk, Player player){
    org.CreadoresProgram.CraftsMine.network.protocol.PlayerActionPacket packet =
        (org.CreadoresProgram.CraftsMine.network.protocol.PlayerActionPacket) pk;
    PlayerActionPacket npk = new PlayerActionPacket();
    npk.setRuntimeEntityId(packet.entityId);
    PlayerActionType[] values = PlayerActionType.values();
    int ordinal = Math.max(0, Math.min(values.length - 1, packet.action));
    npk.setAction(values[ordinal]);
    npk.setBlockPosition(Vector3i.from(packet.x, packet.y, packet.z));
    npk.setFace(packet.face);
    player.getBedrockClientSession().sendPacket(npk);
  }
}
