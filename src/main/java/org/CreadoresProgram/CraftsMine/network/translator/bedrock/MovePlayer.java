package org.CreadoresProgram.CraftsMine.network.translator.bedrock;
import org.CreadoresProgram.CraftsMine.network.translator.interfaces.BedrockPacketTranslator;
import org.CreadoresProgram.CraftsMine.player.Player;
import com.nukkitx.protocol.bedrock.BedrockPacket;
import com.nukkitx.protocol.bedrock.packet.MovePlayerPacket;
import com.nukkitx.math.vector.Vector3f;

/**
 * Translates a v113 MovePlayerPacket (server tells client where a player
 * moved) into the 0.15.x MovePlayerPacket and forwards it to the upstream
 * client.
 *
 * <p>v113 MovePlayerPacket fields:</p>
 * <ul>
 *   <li>{@code runtimeEntityId:long}</li>
 *   <li>{@code position:Vector3f}</li>
 *   <li>{@code rotation:Vector3f} (pitch, yaw, headYaw in that order
 *       per NukkitX v113 serializer)</li>
 *   <li>{@code onGround:boolean}</li>
 *   <li>{@code ridingRuntimeEntityId:long}</li>
 * </ul>
 *
 * <p>0.15.x MovePlayerPacket fields: eid, x, y, z, yaw, headYaw, pitch,
 * mode (byte), onGround. The 0.15.x client expects y = eye position
 * (player y + 1.62) so we add 1.62 back when forwarding the v113 y
 * (which is feet in v113).</p>
 */
public class MovePlayer implements BedrockPacketTranslator{
  @Override
  public void translate(BedrockPacket pk, Player player){
    MovePlayerPacket packet = (MovePlayerPacket) pk;
    org.CreadoresProgram.CraftsMine.network.protocol.MovePlayerPacket npk =
        new org.CreadoresProgram.CraftsMine.network.protocol.MovePlayerPacket();
    npk.eid = packet.getRuntimeEntityId();
    Vector3f pos = packet.getPosition();
    if (pos != null) {
      npk.x = pos.getX();
      // 0.15.x sends eye y; v113 sends feet y. Add eye height back.
      npk.y = pos.getY() + 1.62f;
      npk.z = pos.getZ();
    }
    Vector3f rot = packet.getRotation();
    if (rot != null) {
      // v113 Vector3f rotation layout per NukkitX serializer: x=pitch,
      // y=yaw, z=headYaw.
      npk.pitch = rot.getX();
      npk.yaw = rot.getY();
      npk.headYaw = rot.getZ();
    }
    // Map v113 Animation enum to 0.15.x mode byte. The 0.15.x mode values
    // are: NORMAL=0, RESET=1, ROTATION=2. v113 animation enum values are
    // server-side telegraph markers that don't have a 1:1 mapping; default
    // to NORMAL.
    npk.mode = org.CreadoresProgram.CraftsMine.network.protocol.MovePlayerPacket.MODE_NORMAL;
    npk.onGround = packet.isOnGround();
    player.sendDataCraftsman(npk);
  }
}
