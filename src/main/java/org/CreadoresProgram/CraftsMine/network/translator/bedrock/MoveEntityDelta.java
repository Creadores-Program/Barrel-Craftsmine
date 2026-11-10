package org.CreadoresProgram.CraftsMine.network.translator.bedrock;
import org.CreadoresProgram.CraftsMine.network.translator.interfaces.BedrockPacketTranslator;
import org.CreadoresProgram.CraftsMine.player.Player;
import com.nukkitx.protocol.bedrock.BedrockPacket;
import com.nukkitx.protocol.bedrock.packet.MoveEntityPacket;
import com.nukkitx.math.vector.Vector3f;

/**
 * Same v113 source as MoveEntityAbsolute - both v113 MoveEntityAbsolutePacket
 * and MoveEntityDeltaPacket were renamed by the migration script to
 * MoveEntityPacket. This stub forwards the same way as MoveEntityAbsolute.
 */
public class MoveEntityDelta implements BedrockPacketTranslator{
  @Override
  public void translate(BedrockPacket pk, Player player){
    MoveEntityPacket packet = (MoveEntityPacket) pk;
    org.CreadoresProgram.CraftsMine.network.protocol.MoveEntitiesPacket npk =
        new org.CreadoresProgram.CraftsMine.network.protocol.MoveEntitiesPacket();
    npk.eid = packet.getRuntimeEntityId();
    Vector3f pos = packet.getPosition();
    if (pos != null) {
      npk.x = pos.getX();
      npk.y = pos.getY();
      npk.z = pos.getZ();
    }
    npk.pitch = packet.getPitch() & 0xFF;
    npk.headYaw = packet.getHeadYaw() & 0xFF;
    npk.yaw = packet.getYaw() & 0xFF;
    player.sendDataCraftsman(npk);
  }
}
