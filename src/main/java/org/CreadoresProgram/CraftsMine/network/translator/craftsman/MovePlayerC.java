package org.CreadoresProgram.CraftsMine.network.translator.craftsman;
import org.CreadoresProgram.CraftsMine.network.protocol.base.MCDPacket;
import org.CreadoresProgram.CraftsMine.network.translator.interfaces.CraftsmanPacketTranslator;
import org.CreadoresProgram.CraftsMine.player.Player;
import com.nukkitx.protocol.bedrock.packet.MovePlayerPacket;
import com.nukkitx.math.vector.Vector3f;

/**
 * Translates a 0.15.x MovePlayerPacket (sent by the upstream client when
 * it moves) into the v113 MovePlayerPacket and forwards it to the MCPE 1.1
 * downstream server.
 *
 * <p>0.15.x MovePlayerPacket fields: eid, x, y, z, yaw, headYaw, pitch,
 * mode (byte), onGround.</p>
 *
 * <p>v113 MovePlayerPacket expects: runtimeEntityId, position(Vector3f),
 * rotation(Vector3f), animation, onGround, ridingRuntimeEntityId.</p>
 */
public class MovePlayerC implements CraftsmanPacketTranslator {
  @Override
  public void translate(MCDPacket pk, Player player){
    org.CreadoresProgram.CraftsMine.network.protocol.MovePlayerPacket packet =
        (org.CreadoresProgram.CraftsMine.network.protocol.MovePlayerPacket) pk;
    MovePlayerPacket npk = new MovePlayerPacket();
    npk.setRuntimeEntityId(packet.eid);
    // 0.15.x sends eye y; v113 expects feet y. Subtract eye height.
    npk.setPosition(Vector3f.from(packet.x, packet.y - 1.62f, packet.z));
    // v113 Vector3f rotation layout: x=pitch, y=yaw, z=headYaw.
    npk.setRotation(Vector3f.from(packet.pitch, packet.yaw, packet.headYaw));
    npk.setOnGround(packet.onGround);
    // v113 Animation enum default; the 0.15.x mode byte doesn't map
    // cleanly to v113 Animation - use default.
    player.getBedrockClientSession().sendPacket(npk);
  }
}
