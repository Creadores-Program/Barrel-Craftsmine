package org.CreadoresProgram.CraftsMine.network.translator.craftsman;
import org.CreadoresProgram.CraftsMine.network.protocol.base.MCDPacket;
import org.CreadoresProgram.CraftsMine.network.translator.interfaces.CraftsmanPacketTranslator;
import org.CreadoresProgram.CraftsMine.player.Player;
import com.nukkitx.protocol.bedrock.packet.PlayerActionPacket;
import com.nukkitx.protocol.bedrock.data.PlayerActionType;
import com.nukkitx.math.vector.Vector3i;

/**
 * 0.15.x RemoveBlockPacket (eid, x, z, y) → v113 PlayerActionPacket con
 * action STOP_BREAK.
 */
public class RemoveBlock implements CraftsmanPacketTranslator {
  @Override
  public void translate(MCDPacket pk, Player player){
    org.CreadoresProgram.CraftsMine.network.protocol.RemoveBlockPacket packet =
        (org.CreadoresProgram.CraftsMine.network.protocol.RemoveBlockPacket) pk;
    PlayerActionPacket npk = new PlayerActionPacket();
    npk.setRuntimeEntityId(player.getRuntimeEntityId());
    npk.setAction(PlayerActionType.STOP_BREAK);
    // 0.15.x decodes as x(int), z(int), y(byte)
    npk.setBlockPosition(Vector3i.from(packet.x, packet.y & 0xff, packet.z));
    npk.setFace(0);
    player.getBedrockClientSession().sendPacket(npk);
  }
}
